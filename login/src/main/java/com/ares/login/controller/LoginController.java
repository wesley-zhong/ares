package com.ares.login.controller;


import com.ares.core.service.AresController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController implements AresController {
    @RequestMapping("/helloworld")
    public String helloworld() {
        return "helloworld";
    }
}
