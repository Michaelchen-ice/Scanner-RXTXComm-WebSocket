package com.hziee.scanner.domain.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/w")
@Controller
public class WebSocketController {

    @RequestMapping("/index")
    public String showTestWebsite(){
        return "index";
    }
}
