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
import unipi.fotistsiou.eduverse.entity.Chapter;
import unipi.fotistsiou.eduverse.entity.Course;
import unipi.fotistsiou.eduverse.entity.User;
import unipi.fotistsiou.eduverse.service.ChapterService;
import unipi.fotistsiou.eduverse.service.CourseService;
import unipi.fotistsiou.eduverse.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class CourseController {
    private final CourseService courseService;
    private final UserService userService;
    private final ChapterService chapterService;

    @Autowired
    public CourseController(
        CourseService courseService,
        UserService userService,
        ChapterService chapterService
    ){
        this.courseService = courseService;
        this.userService = userService;
        this.chapterService = chapterService;
    }

    @GetMapping("/course/new/{userId}")
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public String createNewCourseForm(
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
                Course course = new Course();
                course.setProfessor(user);
                model.addAttribute("course", course);
                return "course/course_new";
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }

    @PostMapping("/course/new/{userId}")
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public String createNewCourse(
        @PathVariable Long userId,
        @Valid @ModelAttribute("course") Course course,
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
            User user = optionalUser.get();
            if (user.getEmail().equals(authUsername)) {
                Optional<Course> optionalCourse = courseService.findCourseByCode(course.getCode());
                if (optionalCourse.isPresent()) {
                    result.rejectValue("code", "error.code", "Υπάρχει ήδη μάθημα με τον συγκεκριμένο κωδικό.");
                }
                if (result.hasErrors()) {
                    model.addAttribute("course", course);
                    return "course/course_new";
                }
                courseService.saveCourse(course);
                return String.format("redirect:/course/my/%d?success", course.getProfessor().getId());
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }

    @GetMapping("/course/my/{userId}")
    @PreAuthorize("isAuthenticated()")
    public String myCourses(
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
                String role = user.getRoles().toString();
                List<Course> courses = courseService.getMyCourses(userId, role);
                model.addAttribute("role", role);
                model.addAttribute("courses", courses);
                return "course/course_my";
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
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
        Optional<User> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getEmail().equals(authUsername)) {
                Optional<Course> optionalCourse = courseService.findCourseById(courseId);
                if (optionalCourse.isPresent()) {
                    Course course = optionalCourse.get();
                    courseService.deleteCourse(course);
                    return String.format("redirect:/course/my/%d?success_delete", userId);
                }
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }

    @GetMapping("/course/register/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getAllCourses(
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
                List<Course> courses = courseService.findAvailableCourses(userId);
                model.addAttribute("courses", courses);
                return "course/course_register";
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
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
        Optional<User> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getEmail().equals(authUsername)) {
                Optional<Course> optionalCourse = courseService.findCourseById(courseId);
                if (optionalCourse.isPresent()) {
                    Course course = optionalCourse.get();
                    course.getStudents().add(user);
                    courseService.saveCourse(course);
                    return String.format("redirect:/course/my/%d?success_register", userId);
                }
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
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
        Optional<User> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getEmail().equals(authUsername)) {
                Optional<Course> optionalCourse = courseService.findCourseById(courseId);
                if (optionalCourse.isPresent()) {
                    Course course = optionalCourse.get();
                    course.getStudents().remove(user);
                    courseService.saveCourse(course);
                    return String.format("redirect:/course/my/%d?success_remove", userId);
                }
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }

    @GetMapping("/course/view/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public String getCourse(
            @PathVariable Long courseId,
            Model model
    ){
        Optional<Course> optionalCourse = courseService.findCourseById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            List<Chapter> chapters = chapterService.findAllCourseChapters(courseId);
            model.addAttribute("course", course);
            model.addAttribute("chapters", chapters);
            return "course/course_view";
        }
        return "redirect:/exception_404";
    }

    @GetMapping("/course/edit/{courseId}/{userId}")
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public String editCourseForm(
            @PathVariable Long courseId,
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
                Optional<Course> optionalCourse = courseService.findCourseById(courseId);
                if (optionalCourse.isPresent()) {
                    Course course = optionalCourse.get();
                    if (course.getProfessor().getId().equals(userId)) {
                        model.addAttribute("course", course);
                        return "course/course_edit";
                    }
                }
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }

    @PostMapping("/course/edit/{courseId}/{userId}")
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    public String editCourse(
            @PathVariable Long courseId,
            @PathVariable Long userId,
            @Valid @ModelAttribute("course") Course course,
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
            User user = optionalUser.get();
            if (user.getEmail().equals(authUsername)) {
                Optional<Course> optionalCourse = courseService.findCourseById(courseId);
                if (optionalCourse.isPresent()) {
                    Course existingCourse = optionalCourse.get();
                    if (existingCourse.getProfessor().getId().equals(userId)) {
                        if (!course.getCode().equals(existingCourse.getCode())) {
                            Optional<Course> optCourse = courseService.findCourseByCode(course.getCode());
                            if (optCourse.isPresent()) {
                                result.rejectValue("code", "error.code", "Υπάρχει ήδη μάθημα με τον συγκεκριμένο κωδικό.");
                            }
                        }
                        if (result.hasErrors()) {
                            model.addAttribute("courseId", courseId);
                            model.addAttribute("userId", userId);
                            model.addAttribute("course", course);
                            return "course/course_edit";
                        }
                        existingCourse.setCode(course.getCode());
                        existingCourse.setTitle(course.getTitle());
                        existingCourse.setDescription(course.getDescription());
                        courseService.saveCourse(existingCourse);
                        return String.format("redirect:/course/my/%d?success_edit", userId);
                    }
                }
            }
            return "redirect:/exception_403";
        }
        return "redirect:/exception_404";
    }
}