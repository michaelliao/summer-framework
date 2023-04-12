package com.itranswarp.summer.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.summer.annotation.Controller;
import com.itranswarp.summer.annotation.GetMapping;
import com.itranswarp.summer.annotation.PathVariable;
import com.itranswarp.summer.annotation.PostMapping;
import com.itranswarp.summer.annotation.RequestBody;
import com.itranswarp.summer.annotation.RequestParam;
import com.itranswarp.summer.annotation.ResponseBody;
import com.itranswarp.summer.annotation.RestController;
import com.itranswarp.summer.context.ApplicationContext;
import com.itranswarp.summer.context.ConfigurableApplicationContext;
import com.itranswarp.summer.exception.ErrorResponseException;
import com.itranswarp.summer.exception.NestedRuntimeException;
import com.itranswarp.summer.exception.ServerErrorException;
import com.itranswarp.summer.exception.ServerWebInputException;
import com.itranswarp.summer.io.PropertyResolver;
import com.itranswarp.summer.utils.ClassUtils;
import com.itranswarp.summer.web.utils.JsonUtils;
import com.itranswarp.summer.web.utils.PathUtils;
import com.itranswarp.summer.web.utils.WebUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DispatcherServlet extends HttpServlet {

    final Logger logger = LoggerFactory.getLogger(getClass());

    ApplicationContext applicationContext;
    ViewResolver viewResolver;

    String resourcePath;
    String faviconPath;

    List<Dispatcher> getDispatchers = new ArrayList<>();
    List<Dispatcher> postDispatchers = new ArrayList<>();

    public DispatcherServlet(ApplicationContext applicationContext, PropertyResolver properyResolver) {
        this.applicationContext = applicationContext;
        this.viewResolver = applicationContext.getBean(ViewResolver.class);
        this.resourcePath = properyResolver.getProperty("${summer.web.static-path:/static/}");
        this.faviconPath = properyResolver.getProperty("${summer.web.favicon-path:/favicon.ico}");
        if (!this.resourcePath.endsWith("/")) {
            this.resourcePath = this.resourcePath + "/";
        }
    }

    @Override
    public void init() throws ServletException {
        logger.info("init {}.", getClass().getName());
        // scan @Controller and @RestController:
        for (var def : ((ConfigurableApplicationContext) this.applicationContext).findBeanDefinitions(Object.class)) {
            Class<?> beanClass = def.getBeanClass();
            Object bean = def.getRequiredInstance();
            Controller controller = beanClass.getAnnotation(Controller.class);
            RestController restController = beanClass.getAnnotation(RestController.class);
            if (controller != null && restController != null) {
                throw new ServletException("Found @Controller and @RestController on class: " + beanClass.getName());
            }
            if (controller != null) {
                addController(false, def.getName(), bean);
            }
            if (restController != null) {
                addController(true, def.getName(), bean);
            }
        }
    }

    @Override
    public void destroy() {
        this.applicationContext.close();
    }

    void addController(boolean isRest, String name, Object instance) throws ServletException {
        logger.info("add {} controller '{}': {}", isRest ? "REST" : "MVC", name, instance.getClass().getName());
        addMethods(isRest, name, instance, instance.getClass());
    }

    void addMethods(boolean isRest, String name, Object instance, Class<?> type) throws ServletException {
        for (Method m : type.getDeclaredMethods()) {
            GetMapping get = m.getAnnotation(GetMapping.class);
            if (get != null) {
                checkMethod(m);
                this.getDispatchers.add(new Dispatcher("GET", isRest, instance, m, get.value()));
            }
            PostMapping post = m.getAnnotation(PostMapping.class);
            if (post != null) {
                checkMethod(m);
                this.postDispatchers.add(new Dispatcher("POST", isRest, instance, m, post.value()));
            }
        }
        Class<?> superClass = type.getSuperclass();
        if (superClass != null) {
            addMethods(isRest, name, instance, superClass);
        }
    }

    void checkMethod(Method m) throws ServletException {
        int mod = m.getModifiers();
        if (Modifier.isStatic(mod)) {
            throw new ServletException("Cannot do URL mapping to static method: " + m);
        }
        m.setAccessible(true);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        if (url.equals(this.faviconPath) || url.startsWith(this.resourcePath)) {
            doResource(url, req, resp);
        } else {
            doService(req, resp, this.getDispatchers);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doService(req, resp, this.postDispatchers);
    }

    void doService(HttpServletRequest req, HttpServletResponse resp, List<Dispatcher> dispatchers) throws ServletException, IOException {
        String url = req.getRequestURI();
        try {
            doService(url, req, resp, dispatchers);
        } catch (ErrorResponseException e) {
            logger.warn("process request failed with status " + e.statusCode + " : " + url, e);
            if (!resp.isCommitted()) {
                resp.resetBuffer();
                resp.sendError(e.statusCode);
            }
        } catch (RuntimeException | ServletException | IOException e) {
            logger.warn("process request failed: " + url, e);
            throw e;
        } catch (Exception e) {
            logger.warn("process request failed: " + url, e);
            throw new NestedRuntimeException(e);
        }
    }

    void doService(String url, HttpServletRequest req, HttpServletResponse resp, List<Dispatcher> dispatchers) throws Exception {
        for (Dispatcher dispatcher : dispatchers) {
            Result result = dispatcher.process(url, req, resp);
            if (result.processed()) {
                Object r = result.returnObject();
                if (dispatcher.isRest) {
                    // send rest response:
                    if (!resp.isCommitted()) {
                        resp.setContentType("application/json");
                    }
                    if (dispatcher.isResponseBody) {
                        if (r instanceof String s) {
                            // send as response body:
                            PrintWriter pw = resp.getWriter();
                            pw.write(s);
                            pw.flush();
                        } else if (r instanceof byte[] data) {
                            // send as response body:
                            ServletOutputStream output = resp.getOutputStream();
                            output.write(data);
                            output.flush();
                        } else {
                            // error:
                            throw new ServletException("Unable to process REST result when handle url: " + url);
                        }
                    } else if (!dispatcher.isVoid) {
                        PrintWriter pw = resp.getWriter();
                        JsonUtils.writeJson(pw, r);
                        pw.flush();
                    }
                } else {
                    // process MVC:
                    if (!resp.isCommitted()) {
                        resp.setContentType("text/html");
                    }
                    if (r instanceof String s) {
                        if (dispatcher.isResponseBody) {
                            // send as response body:
                            PrintWriter pw = resp.getWriter();
                            pw.write(s);
                            pw.flush();
                        } else if (s.startsWith("redirect:")) {
                            // send redirect:
                            resp.sendRedirect(s.substring(9));
                        } else {
                            // error:
                            throw new ServletException("Unable to process String result when handle url: " + url);
                        }
                    } else if (r instanceof byte[] data) {
                        if (dispatcher.isResponseBody) {
                            // send as response body:
                            ServletOutputStream output = resp.getOutputStream();
                            output.write(data);
                            output.flush();
                        } else {
                            // error:
                            throw new ServletException("Unable to process byte[] result when handle url: " + url);
                        }
                    } else if (r instanceof ModelAndView mv) {
                        String view = mv.getViewName();
                        if (view.startsWith("redirect:")) {
                            // send redirect:
                            resp.sendRedirect(view.substring(9));
                        } else {
                            this.viewResolver.render(view, mv.getModel(), req, resp);
                        }
                    } else if (!dispatcher.isVoid && r != null) {
                        // error:
                        throw new ServletException("Unable to process " + r.getClass().getName() + " result when handle url: " + url);
                    }
                }
                return;
            }
        }
        // not found:
        resp.sendError(404, "Not Found");
    }

    void doResource(String url, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext ctx = req.getServletContext();
        try (InputStream input = ctx.getResourceAsStream(url)) {
            if (input == null) {
                resp.sendError(404, "Not Found");
            } else {
                // guess content type:
                String file = url;
                int n = url.lastIndexOf('/');
                if (n >= 0) {
                    file = url.substring(n + 1);
                }
                String mime = ctx.getMimeType(file);
                if (mime == null) {
                    mime = "application/octet-stream";
                }
                resp.setContentType(mime);
                ServletOutputStream output = resp.getOutputStream();
                input.transferTo(output);
                output.flush();
            }
        }
    }

    static class Dispatcher {

        final static Result NOT_PROCESSED = new Result(false, null);
        final Logger logger = LoggerFactory.getLogger(getClass());

        boolean isRest;
        boolean isResponseBody;
        boolean isVoid;
        Pattern urlPattern;
        Object controller;
        Method handlerMethod;
        Param[] methodParameters;

        public Dispatcher(String httpMethod, boolean isRest, Object controller, Method method, String urlPattern) throws ServletException {
            this.isRest = isRest;
            this.isResponseBody = method.getAnnotation(ResponseBody.class) != null;
            this.isVoid = method.getReturnType() == void.class;
            this.urlPattern = PathUtils.compile(urlPattern);
            this.controller = controller;
            this.handlerMethod = method;
            Parameter[] params = method.getParameters();
            Annotation[][] paramsAnnos = method.getParameterAnnotations();
            this.methodParameters = new Param[params.length];
            for (int i = 0; i < params.length; i++) {
                this.methodParameters[i] = new Param(httpMethod, method, params[i], paramsAnnos[i]);
            }
            logger.atDebug().log("mapping {} to handler {}.{}", urlPattern, controller.getClass().getSimpleName(), method.getName());
            if (logger.isDebugEnabled()) {
                for (var p : this.methodParameters) {
                    logger.debug("> parameter: {}", p);
                }
            }
        }

        Result process(String url, HttpServletRequest request, HttpServletResponse response) throws Exception {
            Matcher matcher = urlPattern.matcher(url);
            if (matcher.matches()) {
                Object[] arguments = new Object[this.methodParameters.length];
                for (int i = 0; i < arguments.length; i++) {
                    Param param = methodParameters[i];
                    arguments[i] = switch (param.paramType) {
                    case PATH_VARIABLE -> {
                        try {
                            String s = matcher.group(param.name);
                            yield convertToType(param.classType, s);
                        } catch (IllegalArgumentException e) {
                            throw new ServerWebInputException("Path variable '" + param.name + "' not found.");
                        }
                    }
                    case REQUEST_BODY -> {
                        BufferedReader reader = request.getReader();
                        yield JsonUtils.readJson(reader, param.classType);
                    }
                    case REQUEST_PARAM -> {
                        String s = getOrDefault(request, param.name, param.defaultValue);
                        yield convertToType(param.classType, s);
                    }
                    case SERVLET_VARIABLE -> {
                        Class<?> classType = param.classType;
                        if (classType == HttpServletRequest.class) {
                            yield request;
                        } else if (classType == HttpServletResponse.class) {
                            yield response;
                        } else if (classType == HttpSession.class) {
                            yield request.getSession();
                        } else if (classType == ServletContext.class) {
                            yield request.getServletContext();
                        } else {
                            throw new ServerErrorException("Could not determine argument type: " + classType);
                        }
                    }
                    };
                }
                Object result = null;
                try {
                    result = this.handlerMethod.invoke(this.controller, arguments);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getCause();
                    if (t instanceof Exception ex) {
                        throw ex;
                    }
                    throw e;
                } catch (ReflectiveOperationException e) {
                    throw new ServerErrorException(e);
                }
                return new Result(true, result);
            }
            return NOT_PROCESSED;
        }

        Object convertToType(Class<?> classType, String s) {
            if (classType == String.class) {
                return s;
            } else if (classType == boolean.class || classType == Boolean.class) {
                return Boolean.valueOf(s);
            } else if (classType == int.class || classType == Integer.class) {
                return Integer.valueOf(s);
            } else if (classType == long.class || classType == Long.class) {
                return Long.valueOf(s);
            } else if (classType == byte.class || classType == Byte.class) {
                return Byte.valueOf(s);
            } else if (classType == short.class || classType == Short.class) {
                return Short.valueOf(s);
            } else if (classType == float.class || classType == Float.class) {
                return Float.valueOf(s);
            } else if (classType == double.class || classType == Double.class) {
                return Double.valueOf(s);
            } else {
                throw new ServerErrorException("Could not determine argument type: " + classType);
            }
        }

        String getOrDefault(HttpServletRequest request, String name, String defaultValue) {
            String s = request.getParameter(name);
            if (s == null) {
                if (WebUtils.DEFAULT_PARAM_VALUE.equals(defaultValue)) {
                    throw new ServerWebInputException("Request parameter '" + name + "' not found.");
                }
                return defaultValue;
            }
            return s;
        }
    }

    static enum ParamType {
        PATH_VARIABLE, REQUEST_PARAM, REQUEST_BODY, SERVLET_VARIABLE;
    }

    static class Param {

        String name;
        ParamType paramType;
        Class<?> classType;
        String defaultValue;

        public Param(String httpMethod, Method method, Parameter parameter, Annotation[] annotations) throws ServletException {
            PathVariable pv = ClassUtils.getAnnotation(annotations, PathVariable.class);
            RequestParam rp = ClassUtils.getAnnotation(annotations, RequestParam.class);
            RequestBody rb = ClassUtils.getAnnotation(annotations, RequestBody.class);
            // should only have 1 annotation:
            int total = (pv == null ? 0 : 1) + (rp == null ? 0 : 1) + (rb == null ? 0 : 1);
            if (total > 1) {
                throw new ServletException("Annotation @PathVariable, @RequestParam and @RequestBody cannot be combined at method: " + method);
            }
            this.classType = parameter.getType();
            if (pv != null) {
                this.name = pv.value();
                this.paramType = ParamType.PATH_VARIABLE;
            } else if (rp != null) {
                this.name = rp.value();
                this.defaultValue = rp.defaultValue();
                this.paramType = ParamType.REQUEST_PARAM;
            } else if (rb != null) {
                this.paramType = ParamType.REQUEST_BODY;
            } else {
                this.paramType = ParamType.SERVLET_VARIABLE;
                // check servlet variable type:
                if (this.classType != HttpServletRequest.class && this.classType != HttpServletResponse.class && this.classType != HttpSession.class
                        && this.classType != ServletContext.class) {
                    throw new ServerErrorException("(Missing annotation?) Unsupported argument type: " + classType + " at method: " + method);
                }
            }
        }

        @Override
        public String toString() {
            return "Param [name=" + name + ", paramType=" + paramType + ", classType=" + classType + ", defaultValue=" + defaultValue + "]";
        }
    }

    static record Result(boolean processed, Object returnObject) {
    }
}
