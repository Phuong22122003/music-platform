package com.MusicPlatForm.notification_service.controller;

import java.net.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {
    @GetMapping("/a")
    public String getMethodName() {
        return "a";
    }
    @GetMapping("/header")
    public ResponseEntity<?> getHeader(@RequestHeader HttpHeaders headers){
        return ResponseEntity.ok().body(headers);
    }
}
