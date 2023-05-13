package ru.selivanov.springproject.diplomaProject.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PathController {
    @GetMapping("/error")
    public String getErrorPage() {
        return "error";
    }

    @GetMapping("/")
    public String getRedirect() {
        return "redirect:/auth/redirect";
    }
}

