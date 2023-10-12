package unipi.fotistsiou.eduverse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipi.fotistsiou.eduverse.entity.Chapter;
import unipi.fotistsiou.eduverse.repository.ChapterRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {
    private final ChapterRepository chapterRepository;

    @Autowired
    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    public Optional<Chapter> findChapterById(Long id) {
        return chapterRepository.findById(id);
    }

    public List<Chapter> findAllChapter() {
        return chapterRepository.findAll();
    }

    public void saveChapter(Chapter chapter) {
        chapterRepository.save(chapter);
    }

    public void deleteChapter(Chapter chapter) {
        chapterRepository.delete(chapter);
    }
}