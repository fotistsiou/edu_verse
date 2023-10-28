package unipi.fotistsiou.eduverse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipi.fotistsiou.eduverse.entity.Question;
import unipi.fotistsiou.eduverse.repository.QuestionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }

    public Optional<Question> findQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public void deleteQuestion(Question question) {
        questionRepository.delete(question);
    }

    public void saveQuestion(Question question) {
        questionRepository.save(question);
    }

    public List<Question> findAllChapterQuestions(Long chapterId) {
        List<Question> chapterQuestions = new ArrayList<>();
        List<Question> questions = questionRepository.findAll();
        for (Question question:questions) {
            if (question.getChapter().getId().equals(chapterId)) {
                chapterQuestions.add(question);
            }
        }
        return chapterQuestions;
    }
}