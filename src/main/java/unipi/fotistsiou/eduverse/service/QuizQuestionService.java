package unipi.fotistsiou.eduverse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipi.fotistsiou.eduverse.entity.*;
import unipi.fotistsiou.eduverse.repository.QuizQuestionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizQuestionService {
    private final QuizQuestionRepository quizQuestionRepository;

    @Autowired
    public QuizQuestionService(QuizQuestionRepository quizQuestionRepository) {
        this.quizQuestionRepository = quizQuestionRepository;
    }

    public Optional<QuizQuestion> findQuizQuestionById(Long id) {
        return quizQuestionRepository.findById(id);
    }

    public void saveQuizQuestion(List<Question> questions, Result result, User student) {
        for (Question question:questions) {
            QuizQuestion quizQuestion = new QuizQuestion();
            quizQuestion.setTitle(question.getTitle());
            quizQuestion.setAnswer(question.getAnswer());
            quizQuestion.setChoice(question.getChoice());
            quizQuestion.setChapter(question.getChapter());
            quizQuestion.setResult(result);
            quizQuestion.setStudent(student);
            quizQuestionRepository.save(quizQuestion);
        }
    }

    public List<QuizQuestion> findQuizQuestionByResultId(Long resultId) {
        List<QuizQuestion> resultQuizQuestions = new ArrayList<>();
        List<QuizQuestion> quizQuestions = quizQuestionRepository.findAll();
        for (QuizQuestion quizQuestion:quizQuestions) {
            if (quizQuestion.getResult().getId().equals(resultId)) {
                resultQuizQuestions.add(quizQuestion);
            }
        }
        return resultQuizQuestions;
    }
}
