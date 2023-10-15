package unipi.fotistsiou.eduverse.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/account/info/{userId}")
    @PreAuthorize("isAuthenticated()")
    public String getAccountInfo(
        @PathVariable Long userId,
        Model model,
        Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getEmail().equals(authUsername)) {
                model.addAttribute("user", user);
                return "account/account_info";
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }

    @GetMapping("/account/info/edit/{userId}")
    @PreAuthorize("isAuthenticated()")
    public String editAccountInfo(
        @PathVariable Long userId,
        Model model,
        Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getEmail().equals(authUsername)) {
                model.addAttribute("user", user);
                return "account/account_info_edit";
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }

    @PostMapping("/account/info/edit/{userId}")
    @PreAuthorize("isAuthenticated()")
    public String updateAccountInfo (
        @PathVariable Long userId,
        @Valid @ModelAttribute("user") User user,
        BindingResult result,
        Model model,
        Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (existingUser.getEmail().equals(authUsername)) {
                if (!user.getEmail().equals(existingUser.getEmail())) {
                    Optional<User> optUser = userService.findUserByEmail(user.getEmail());
                    if (optUser.isPresent()) {
                        result.rejectValue("email", "error.email", "Υπάρχει ήδη χρήστης με το συγκεκριμένο email.");
                    }
                }
                if (result.hasErrors()) {
                    for (FieldError error : result.getFieldErrors()) {
                        if (!error.getField().equals("password")) {
                            System.out.println(result);
                            model.addAttribute("user", user);
                            return "account/account_info_edit";
                        }
                    }
                }
                existingUser.setEmail(user.getEmail());
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                existingUser.setTelephone(user.getTelephone());
                userService.updateUserDetails(existingUser);
                // TODO : Handle the case when the customer changes their email.
                return String.format("redirect:/account/info/%d?success", userId);
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }

    @GetMapping("/account/password/edit/{userId}")
    @PreAuthorize("isAuthenticated()")
    public String editPassword(
        @PathVariable Long userId,
        Model model,
        Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getEmail().equals(authUsername)) {
                model.addAttribute("user", user);
                return "account/account_password_edit";
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }

    @PostMapping("/account/password/edit/{userId}")
    @PreAuthorize("isAuthenticated()")
    public String updatePassword (
        @PathVariable Long userId,
        @RequestParam String oldPassword,
        @RequestParam String newPassword,
        @RequestParam String confirmPassword,
        Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (existingUser.getEmail().equals(authUsername)) {
                String storedEncodedPassword = existingUser.getPassword();
                if (!passwordEncoder.matches(oldPassword, storedEncodedPassword)) {
                    return  String.format("redirect:/account/password/edit/%d?error_oldPassword", userId);
                }
                if (!newPassword.equals(confirmPassword)) {
                    return  String.format("redirect:/account/password/edit/%d?error_confirmPassword", userId);
                }
                String encodedPassword = passwordEncoder.encode(newPassword);
                existingUser.setPassword(encodedPassword);
                userService.updatePassword(existingUser);
                return String.format("redirect:/account/info/%d?success_update_pass", userId);
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }
}