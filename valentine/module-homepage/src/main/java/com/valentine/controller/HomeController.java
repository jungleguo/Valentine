package com.valentine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    // 处理其他前端路由（用于支持 React Router）
    @GetMapping(value = { "/{path:[^\\.]*}" })
    public String redirect() {
        return "forward:/index.html";
    }
}
