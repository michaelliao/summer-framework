package com.itranswarp.hello.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.hello.User;
import com.itranswarp.hello.service.UserService;
import com.itranswarp.summer.annotation.Autowired;
import com.itranswarp.summer.annotation.Controller;
import com.itranswarp.summer.annotation.GetMapping;
import com.itranswarp.summer.annotation.PostMapping;
import com.itranswarp.summer.annotation.RequestParam;
import com.itranswarp.summer.exception.DataAccessException;
import com.itranswarp.summer.web.ModelAndView;

import jakarta.servlet.http.HttpSession;

@Controller
public class MvcController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    static final String USER_SESSION_KEY = "__user__";

    @GetMapping("/")
    ModelAndView index(HttpSession session) {
        User user = (User) session.getAttribute(USER_SESSION_KEY);
        if (user == null) {
            return new ModelAndView("redirect:/register");
        }
        return new ModelAndView("/index.html", Map.of("user", user));
    }

    @GetMapping("/register")
    ModelAndView register() {
        return new ModelAndView("/register.html");
    }

    @PostMapping("/register")
    ModelAndView doRegister(@RequestParam("email") String email, @RequestParam("name") String name, @RequestParam("password") String password) {
        try {
            userService.createUser(email, name, password);
        } catch (DataAccessException e) {
            return new ModelAndView("/register.html", Map.of("error", "Email already exist."));
        }
        return new ModelAndView("redirect:/signin");
    }

    @GetMapping("/signin")
    ModelAndView signin() {
        return new ModelAndView("/signin.html");
    }

    @PostMapping("/signin")
    ModelAndView doSignin(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session) {
        User user = null;
        try {
            user = userService.getUser(email.strip().toLowerCase());
            if (!user.password.equals(password)) {
                throw new DataAccessException("bad password.");
            }
        } catch (DataAccessException e) {
            // user not found:
            return new ModelAndView("/signin.html", Map.of("error", "Bad email or password."));
        }
        session.setAttribute(USER_SESSION_KEY, user);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/signout")
    String signout(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
        return "redirect:/signin";
    }

}
