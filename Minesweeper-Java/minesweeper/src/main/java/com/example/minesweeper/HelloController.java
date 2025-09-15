package com.example.minesweeper;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "localhost:5500"})
public class HelloController {

    @GetMapping("/hello")
    public HelloMessage hello() {
        return new HelloMessage("Hello from backend!", "Server");
    }

    @PostMapping("/hello")
    public HelloResponse postMethodName(@RequestBody HelloRequest req) {
        String n = (req != null && req.name != null && !req.name.isBlank())
        ? req.name:"stranger";
        
        return new HelloResponse("Hello, " + n + "!", System.currentTimeMillis());
    }
    
    
}
