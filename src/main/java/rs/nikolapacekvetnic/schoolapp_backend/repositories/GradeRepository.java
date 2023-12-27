package rs.nikolapacekvetnic.schoolapp_backend.repositories;

import org.springframework.data.repository.CrudRepository;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeEntity;

public interface GradeRepository extends CrudRepository<GradeEntity, Integer> {
	
}
