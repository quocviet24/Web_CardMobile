package com.nishikatakagi.ProductDigital.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {
    @GetMapping("")
    public String showPageAdmin() {
        return "redirect:/statistic";
    }
    
}
