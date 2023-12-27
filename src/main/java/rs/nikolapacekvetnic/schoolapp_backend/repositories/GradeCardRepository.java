package rs.nikolapacekvetnic.schoolapp_backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.LectureEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;

public interface GradeCardRepository extends CrudRepository<GradeCardEntity, Integer> {
	
	Optional<GradeCardEntity> findByLectureAndStudent(LectureEntity lecture, StudentEntity student);
}
