package unipi.fotistsiou.eduverse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

    @GetMapping("/access_denied")
    public String accessDenied() {
        return "access_denied";
    }
}
