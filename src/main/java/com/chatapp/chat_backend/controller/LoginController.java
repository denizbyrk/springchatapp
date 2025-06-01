package com.chatapp.chat_backend.controller;


import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

 
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html sayfası
    }

   
}
