package sesac.sesac_spring_boot_security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public String home(){
        return "hello, world! basic controller, CICD test";
    }

    @GetMapping("/test")
    public String toLoginUser(){
        return "you're a login user!";
    }
}
