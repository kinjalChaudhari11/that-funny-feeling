package com.thatfunnyfeeling.art_gallery;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/home")
    public String getHomePage(@RequestParam(value = "continue", required = false) String continueParam, Model model) {
        // You can handle "continue" logic here if needed
        return "home";
    }

    @GetMapping("/login")
    public String getLoginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        return "login";
    }
}
