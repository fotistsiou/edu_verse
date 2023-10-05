package unipi.fotistsiou.eduverse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unipi.fotistsiou.eduverse.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {}
