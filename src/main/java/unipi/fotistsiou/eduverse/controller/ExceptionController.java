package unipi.fotistsiou.eduverse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExceptionController {
    @GetMapping("/exception/access_denied")
    public String accessDenied() {
        return "exception/access_denied";
    }

    @GetMapping("/exception/unauthorized")
    public String unauthorized() {
        return "exception/unauthorized";
    }
}
