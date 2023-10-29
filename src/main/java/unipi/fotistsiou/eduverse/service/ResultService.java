package unipi.fotistsiou.eduverse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipi.fotistsiou.eduverse.entity.Question;
import unipi.fotistsiou.eduverse.entity.Quiz;
import unipi.fotistsiou.eduverse.entity.Result;
import unipi.fotistsiou.eduverse.repository.ResultRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResultService {
    private final ResultRepository resultRepository;

    @Autowired
    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public Optional<Result> getResultById(Long id) {
        return resultRepository.findById(id);
    }

    public void saveResult(Result result) {
        resultRepository.save(result);
    }

    public int getCorrects(Quiz quiz) {
        int corrects = 0;
        for(Question answer:quiz.getQuestions()) {
            if (answer.getAnswer() == answer.getChoice()) {
                corrects++;
            }
        }
        return corrects;
    }

    public int getWrongs(Quiz quiz) {
        int wrongs = 0;
        for(Question answer:quiz.getQuestions()) {
            if (answer.getAnswer() != answer.getChoice()) {
                wrongs++;
            }
        }
        return wrongs;
    }

    public List<Result> getStudentResults(Long userId) {
        List<Result> userResults = new ArrayList<>();
        List<Result> results = resultRepository.findAll();
        for (Result result:results) {
            if (result.getStudent().getId().equals(userId)) {
                userResults.add(result);
            }
        }
        return userResults;
    }
}