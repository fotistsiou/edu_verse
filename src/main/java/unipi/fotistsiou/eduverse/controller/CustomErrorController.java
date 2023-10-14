package unipi.fotistsiou.eduverse.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    @GetMapping("/exception")
    public String getException() {
        return "exception/exception";
    }

    @GetMapping("/exception_403")
    public String getException403() {
        return "exception/exception_403";
    }

    @GetMapping("/exception_404")
    public String getException404() {
        return "exception/exception_404";
    }

    @GetMapping("/exception_500")
    public String getException500() {
        return "exception/exception_500";
    }
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "redirect:/exception_403";
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "redirect:/exception_404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "redirect:/exception_500";
            }
        }
        return "redirect:/exception";
    }
}