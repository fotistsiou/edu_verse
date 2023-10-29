package unipi.fotistsiou.eduverse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import unipi.fotistsiou.eduverse.entity.*;
import unipi.fotistsiou.eduverse.service.ChapterService;
import unipi.fotistsiou.eduverse.service.QuestionService;
import unipi.fotistsiou.eduverse.service.ResultService;
import unipi.fotistsiou.eduverse.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class QuizController {
    private final QuestionService questionService;
    private final UserService userService;
    private final ChapterService chapterService;
    private final ResultService resultService;

    @Autowired
    public QuizController(
        QuestionService questionService,
        UserService userService,
        ChapterService chapterService,
        ResultService resultService
    ){
        this.questionService = questionService;
        this.userService = userService;
        this.chapterService = chapterService;
        this.resultService = resultService;
    }

    @GetMapping("/quiz/{chapterId}/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getQuiz (
        @PathVariable Long chapterId,
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
                Optional<Chapter> optionalChapter = chapterService.findChapterById(chapterId);
                if (optionalChapter.isPresent()) {
                    Chapter chapter = optionalChapter.get();
                    if (chapter.getCourse().getStudents().contains(user)) {
                        Quiz quiz = questionService.getQuestionsByChapter(chapterId);
                        model.addAttribute("quiz", quiz);
                        model.addAttribute("chapter", chapter);
                        model.addAttribute("userId", userId);
                        return "quiz/quiz";
                    }
                    return "error/error_403";
                }
                return "error/error_404";
            }
            return "error/error_403";
        }
        return "error/error_404";
    }

    @PostMapping("/quiz/{chapterId}/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String submitQuiz (
        @PathVariable Long chapterId,
        @PathVariable Long userId,
        @ModelAttribute Quiz quiz,
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
                Optional<Chapter> optionalChapter = chapterService.findChapterById(chapterId);
                if (optionalChapter.isPresent()) {
                    Chapter chapter = optionalChapter.get();
                    if (chapter.getCourse().getStudents().contains(user)) {
                        int corrects = resultService.getCorrects(quiz);
                        int wrongs = resultService.getWrongs(quiz);
                        // TODO: Create resultService method for create feedback according to percentage of corrects
                        String feedback = (wrongs == 0) ? "Πέρασες" : "Δεν Πέρασες";
                        Result result = new Result();
                        result.setCorrect(corrects);
                        result.setWrong(wrongs);
                        result.setFeedback(feedback);
                        // TODO: Create new entity QuizQuestions
                        result.setQuiz(quiz.toString());
                        result.setChapter(chapter);
                        result.setStudent(user);
                        resultService.saveResult(result);
                        return String.format("redirect:/quiz/result/%d/%d?success_submit_quiz", result.getId(), userId);
                    }
                    return "error/error_403";
                }
                return "error/error_404";
            }
            return "error/error_403";
        }
        return "error/error_404";
    }

    @GetMapping("/quiz/result/{quizId}/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getQuizResult(
        @PathVariable Long quizId,
        @PathVariable Long userId,
        Model model,
        Principal principal
    ) {
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getEmail().equals(authUsername)) {
                Optional<Result> optionalResult = resultService.getResultById(quizId);
                if (optionalResult.isPresent()) {
                    Result result = optionalResult.get();
                    if (result.getStudent().getEmail().equals(authUsername)) {
                        model.addAttribute("result", result);
                        model.addAttribute("userId", userId);
                        model.addAttribute("courseId", result.getChapter().getCourse().getId());
                        return "quiz/quiz_result";
                    }
                    return "error/error_403";
                }
                return "error/error_404";
            }
            return "error/error_403";
        }
        return "error/error_404";
    }

    @GetMapping("/quiz/result/all/{userId}")
    @PreAuthorize("isAuthenticated()")
    public String getQuizResultAll(
            @PathVariable Long userId,
            Model model,
            Principal principal
    ) {
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }
        Optional<User> optionalUser = userService.findUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getEmail().equals(authUsername)) {
                List<Result> results = resultService.getStudentResults(userId);
                model.addAttribute("results", results);
                return "quiz/quiz_result_all";
            }
            return "error/error_403";
        }
        return "error/error_404";
    }
}