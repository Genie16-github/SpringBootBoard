package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/sbb")
    @ResponseBody
    public String index() {
        System.out.println("index");
        return "index";
    }

    @GetMapping("/")
    public String root() {
        // 질문 게시판으로
        return "redirect:/question/1/list";
    }
}
