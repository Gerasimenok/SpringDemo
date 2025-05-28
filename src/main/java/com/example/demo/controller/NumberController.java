package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NumberController {

    private static final int MULTIPLIER = 5;

    @GetMapping("/multiply")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String multiplyForm() {
        return "multiply";
    }

    @GetMapping("/multiply/calculate")
    @ResponseBody
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public int multiplyNumber(@RequestParam("number") int number) {
        return number * MULTIPLIER;
    }
}