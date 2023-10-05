package unipi.fotistsiou.eduverse.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import unipi.fotistsiou.eduverse.entity.Course;
import unipi.fotistsiou.eduverse.entity.User;
import unipi.fotistsiou.eduverse.service.CourseService;
import unipi.fotistsiou.eduverse.service.UserService;

import java.security.Principal;
import java.util.Optional;

@Controller
public class CourseController {
    final private CourseService courseService;
    private final UserService userService;

    @Autowired
    public CourseController (
        CourseService courseService,
        UserService userService
    ){
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/course/new")
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public String createNewCourseForm(
        Model model,
        Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = userService.findOneByEmail(authUsername);
        if (optionalUser.isPresent()) {
            Course course = new Course();
            course.setProfessor(optionalUser.get());
            model.addAttribute("course", course);
            return "course_new";
        } else {
            return "404";
        }
    }

    @PostMapping("/course/new")
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public String createNewCourse(
        @Valid @ModelAttribute("course") Course course,
        BindingResult result,
        Model model
    ){
        if (result.hasErrors()) {
            model.addAttribute("course", course);
            return "course_new";
        }
        courseService.saveCourse(course);
        return "redirect:/course/new?success";
    }
}