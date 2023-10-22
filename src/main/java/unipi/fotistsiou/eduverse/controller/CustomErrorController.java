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
    @GetMapping("/error")
    public String getException() {
        return "error/error";
    }

    @GetMapping("/error_403")
    public String getException403() {
        return "error/error_403";
    }

    @GetMapping("/error_404")
    public String getException404() {
        return "error/error_404";
    }

    @GetMapping("/error_500")
    public String getException500() {
        return "error/error_500";
    }
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "redirect:/error_403";
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "redirect:/error_404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "redirect:/error_500";
            }
        }
        return "redirect:/error";
    }
}