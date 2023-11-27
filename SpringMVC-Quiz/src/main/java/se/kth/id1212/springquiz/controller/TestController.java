package se.kth.id1212.springquiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class TestController {

    @RequestMapping("/login")
    public String showHelloPage() {
        System.out.println("#############showHelloPage method#############");

        return "login";
    }
}