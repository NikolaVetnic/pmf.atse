package rs.nikolapacekvetnic.schoolapp_backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;

public interface TeacherRepository extends CrudRepository<TeacherEntity, Integer> {
	
	Optional<TeacherEntity> findByEmail(String email);
}
