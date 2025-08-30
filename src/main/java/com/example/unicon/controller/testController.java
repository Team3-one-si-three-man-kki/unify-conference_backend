package com.example.unicon.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class testController {
//    @Value("${TEST_VALUE}")
//    private int httpPort;

     @GetMapping("/test")
     public String testEndpoint() {
//         System.out.println("HTTP Port: " + httpPort);
         return "This is a test endpoint";
     }
}
