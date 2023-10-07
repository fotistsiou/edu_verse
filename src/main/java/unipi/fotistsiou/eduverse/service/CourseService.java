package unipi.fotistsiou.eduverse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unipi.fotistsiou.eduverse.entity.Course;
import unipi.fotistsiou.eduverse.repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    final private CourseRepository courseRepository;

    @Autowired
    public CourseService (CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Optional<Course> findCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> findCourseByCode(String code) {
        return courseRepository.findCourseByCode(code);
    }

    public void saveCourse(Course course) {
        courseRepository.save(course);
    }

    public List<Course> getMyCourses(Long userId, String role) {
        List<Course> myCourses = new ArrayList<>();
        List<Course> courses = courseRepository.findAll();
        for (Course course:courses) {
            if (role.contains("ROLE_PROFESSOR")) {
                if (course.getProfessor().getId().equals(userId)) {
                    myCourses.add(course);
                }
            }
        }
        return myCourses;
    }

    public void deleteCourse(Course course) {
        courseRepository.delete(course);
    }
}