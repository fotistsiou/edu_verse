package unipi.fotistsiou.eduverse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipi.fotistsiou.eduverse.entity.Course;
import unipi.fotistsiou.eduverse.repository.CourseRepository;
import java.util.Optional;

@Service
public class CourseService {
    final private CourseRepository courseRepository;

    @Autowired
    public CourseService (CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> findOneByCode(String code) {
        return courseRepository.findOneByCode(code);
    }

    public void saveCourse(Course course) {
        courseRepository.save(course);
    }
}