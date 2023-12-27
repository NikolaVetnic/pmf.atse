package rs.nikolapacekvetnic.schoolapp_backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.LectureEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SubjectEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;

public interface LectureRepository extends CrudRepository<LectureEntity, Integer> {
	
	Optional<LectureEntity> findBySubjectAndTeacher(SubjectEntity subject, TeacherEntity teacher);
}
