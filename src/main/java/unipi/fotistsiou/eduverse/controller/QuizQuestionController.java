package unipi.fotistsiou.eduverse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import unipi.fotistsiou.eduverse.entity.*;
import unipi.fotistsiou.eduverse.service.QuizQuestionService;
import unipi.fotistsiou.eduverse.service.ResultService;
import unipi.fotistsiou.eduverse.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class QuizQuestionController {
    private final QuizQuestionService quizQuestionService;
    private final UserService userService;
    private final ResultService resultService;

    @Autowired
    public QuizQuestionController(
        QuizQuestionService quizQuestionService,
        UserService userService,
        ResultService resultService
    ){
        this.quizQuestionService = quizQuestionService;
        this.userService = userService;
        this.resultService = resultService;
    }

    @GetMapping("/quiz/question/{resultId}/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getQuizQuestions(
        @PathVariable Long resultId,
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
                Optional<Result> optionalResult = resultService.findResultById(resultId);
                if (optionalResult.isPresent()) {
                    Result result = optionalResult.get();
                    System.out.println(result.getStudent().getId());
                    System.out.println(userId);
                    if (result.getStudent().getId().equals(userId)) {
                        List<QuizQuestion> quizQuestions = quizQuestionService.findQuizQuestionByResultId(resultId);
                        model.addAttribute("result", result);
                        model.addAttribute("quizQuestions", quizQuestions);
                        model.addAttribute("userId", userId);
                        return "quiz/quiz_question";
                    }
                    return "error/error_403";
                }
                return "error/error_404";
            }
            return "error/error_403";
        }
        return "error/error_404";
    }
}