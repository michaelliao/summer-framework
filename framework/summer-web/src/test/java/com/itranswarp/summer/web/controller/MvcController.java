package com.itranswarp.summer.web.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itranswarp.summer.annotation.Controller;
import com.itranswarp.summer.annotation.GetMapping;
import com.itranswarp.summer.annotation.PathVariable;
import com.itranswarp.summer.annotation.PostMapping;
import com.itranswarp.summer.annotation.RequestParam;
import com.itranswarp.summer.annotation.ResponseBody;
import com.itranswarp.summer.web.ModelAndView;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MvcController {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/hello/{name}")
    @ResponseBody
    String hello(@PathVariable("name") String name) {
        return "Hello, " + name;
    }

    @GetMapping("/greeting")
    @ResponseBody
    String greeting(@RequestParam(value = "action", defaultValue = "Hello") String action, @RequestParam("name") String name) {
        return action + ", " + name;
    }

    @GetMapping("/download/{file}")
    @ResponseBody
    byte[] download(@PathVariable("file") String file, @RequestParam("time") Float downloadTime, @RequestParam("md5") String md5,
            @RequestParam("length") int length, @RequestParam("hasChecksum") boolean checksum) {
        return "A".repeat(length).getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/download-part")
    void downloadPart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(206);
        resp.setHeader("Range", "bytes=100-108");
        ServletOutputStream output = resp.getOutputStream();
        output.write("A".repeat(8).getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    @GetMapping("/login")
    String login(@RequestParam(value = "next", defaultValue = "/signin") String next) {
        return "redirect:" + next;
    }

    @GetMapping("/product/{id}")
    ModelAndView product(@PathVariable("id") long id, @RequestParam("name") String name) {
        return new ModelAndView("/product.html", Map.of("name", name, "product", Map.of("id", id, "name", "Summer Software")));
    }

    @PostMapping("/signin")
    ModelAndView signin(@RequestParam("name") String name, @RequestParam("password") String password) {
        return new ModelAndView("redirect:/home?name=" + name);
    }

    @PostMapping("/register")
    ModelAndView register(@RequestParam("name") String name, @RequestParam("password") String password) {
        return new ModelAndView("/register.html", Map.of("name", name));
    }

    @PostMapping("/signout")
    ModelAndView signout(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        session.setAttribute("signout", Boolean.TRUE);
        resp.sendRedirect("/signin?name=" + name);
        return null;
    }
}
