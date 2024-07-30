package com.nishikatakagi.ProductDigital.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("icon")
@Controller
public class IconController {

    @GetMapping("")
    public String showIconPage() {
        return "pages/icons/mdi.html";
    }
}
