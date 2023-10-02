package unipi.fotistsiou.eduguide.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import unipi.fotistsiou.eduguide.entity.User;
import unipi.fotistsiou.eduguide.service.UserService;
import java.security.Principal;
import java.util.Optional;

@Controller
public class AccountController {
    private final UserService userService;

    @Autowired
    public AccountController(
            UserService userService
    ){
        this.userService = userService;
    }

    @GetMapping("/account/info/{id}")
    @PreAuthorize("isAuthenticated()")
    public String getAccountInfo(
            @PathVariable Long id,
            Model model,
            Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = this.userService.getUserById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.getEmail().equals(authUsername)) {
                return "404";
            }
            model.addAttribute("user", user);
            return "account_info";
        } else {
            return "404";
        }
    }

    @GetMapping("/account/info/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editAccountInfo(
            @PathVariable Long id,
            Model model,
            Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = this.userService.getUserById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.getEmail().equals(authUsername)) {
                return "404";
            }
            model.addAttribute("user", user);
            return "account_info_edit";
        } else {
            return "404";
        }
    }

    @PostMapping("/account/info/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateAccountInfo (
            @PathVariable Long id,
            User user
    ){
        Optional<User> optionalUser = this.userService.getUserById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setEmail(user.getEmail());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setTelephone(user.getTelephone());
            userService.updateUser(existingUser);
        }
        return String.format("redirect:/account/info/%d?success", user.getId());
    }
}
