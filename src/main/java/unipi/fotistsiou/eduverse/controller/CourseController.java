package unipi.fotistsiou.eduverse.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import unipi.fotistsiou.eduverse.entity.Course;
import unipi.fotistsiou.eduverse.entity.User;
import unipi.fotistsiou.eduverse.service.CourseService;
import unipi.fotistsiou.eduverse.service.UserService;
import java.security.Principal;
import java.util.List;
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
        Optional<User> optionalUser = userService.findUserByEmail(authUsername);
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
        Optional<Course> optionalCourse = courseService.findCourseByCode(course.getCode());
        if (optionalCourse.isPresent()) {
            result.rejectValue("code", "error.code", "Υπάρχει ήδη μάθημα με τον συγκεκριμένο κωδικό.");
        }
        if (result.hasErrors()) {
            model.addAttribute("course", course);
            return "course_new";
        }
        courseService.saveCourse(course);
        return String.format("redirect:/course/my/%d?success", course.getProfessor().getId());
    }

    @GetMapping("/course/my/{id}")
    @PreAuthorize("isAuthenticated()")
    public String myCourses(
        @PathVariable Long id,
        Model model,
        Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = this.userService.findUserById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.getEmail().equals(authUsername)) {
                return "404";
            }
            String role = user.getRoles().toString();
            Long userId = user.getId();
            List<Course> courses = courseService.getMyCourses(id, role);
            model.addAttribute("role", role);
            model.addAttribute("userId", userId);
            model.addAttribute("courses", courses);
            return "course_my";
        } else {
            return "404";
        }
    }

    @GetMapping("/course/delete/{courseId}/{userId}")
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public String deleteCourse(
        @PathVariable Long courseId,
        @PathVariable Long userId,
        Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = this.userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.getEmail().equals(authUsername)) {
                return "404";
            }
            Optional<Course> optionalCourse = courseService.findCourseById(courseId);
            if (optionalCourse.isPresent()) {
                Course course = optionalCourse.get();
                courseService.deleteCourse(course);
                return String.format("redirect:/course/my/%d?success_delete", userId);
            }
        }
        return "404";
    }

    @GetMapping("/course/register")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getAllCourses(
        Model model,
        Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = userService.findUserByEmail(authUsername);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Long userId = user.getId();
            List<Course> courses = courseService.findAvailableCourses(userId);
            model.addAttribute("userId", userId);
            model.addAttribute("courses", courses);
        }
        return "course_register";
    }

    @GetMapping("/course/register/{courseId}/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String registerCourse(
        @PathVariable Long courseId,
        @PathVariable Long userId,
        Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = this.userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.getEmail().equals(authUsername)) {
                return "404";
            }
            Optional<Course> optionalCourse = courseService.findCourseById(courseId);
            if (optionalCourse.isPresent()) {
                Course course = optionalCourse.get();
                course.getStudents().add(user);
                courseService.saveCourse(course);
                return String.format("redirect:/course/my/%d?success_register", userId);
            }
        }
        return "404";
    }

    @GetMapping("/course/remove/{courseId}/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String removeCourse(
            @PathVariable Long courseId,
            @PathVariable Long userId,
            Principal principal
    ){
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = this.userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.getEmail().equals(authUsername)) {
                return "404";
            }
            Optional<Course> optionalCourse = courseService.findCourseById(courseId);
            if (optionalCourse.isPresent()) {
                Course course = optionalCourse.get();
                course.getStudents().remove(user);
                courseService.saveCourse(course);
                return String.format("redirect:/course/my/%d?success_remove", userId);
            }
        }
        return "404";
    }
}