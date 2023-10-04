package unipi.fotistsiou.eduverse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import unipi.fotistsiou.eduverse.entity.User;
import unipi.fotistsiou.eduverse.service.UserService;
import java.security.Principal;
import java.util.Optional;

@Controller
public class AccountController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountController(
            UserService userService,
            PasswordEncoder passwordEncoder
    ){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

    @GetMapping("/account/password/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editPassword(
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
            return "account_password_edit";
        } else {
            return "404";
        }
    }

    @PostMapping("/account/password/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updatePassword (
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword
    ){
        Optional<User> optionalUser = this.userService.getUserById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            // Decode the stored (encoded) old password
            String storedEncodedPassword = existingUser.getPassword();

            // Compare the user-provided old password after encoding with the stored encoded password
            if (!passwordEncoder.matches(oldPassword, storedEncodedPassword)) {
                return  String.format("redirect:/account/password/edit/%d?error_oldPassword", id); // Redirect back to the profile page with an error message
            }

            if (!newPassword.equals(confirmPassword)) {
                return  String.format("redirect:/account/password/edit/%d?error_confirmPassword", id); // Redirect back to the profile page with an error message
            }

            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);

            userService.updatePassword(existingUser);
        }
        return String.format("redirect:/account/info/%d?success_update_pass", id);
    }
}
