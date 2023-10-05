package unipi.fotistsiou.eduverse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipi.fotistsiou.eduverse.repository.CourseRepository;

@Service
public class CourseService {
    final private CourseRepository courseRepository;

    @Autowired
    public CourseService (CourseRepository courseRepository) {
        this.courseRepository =courseRepository;
    }
}
