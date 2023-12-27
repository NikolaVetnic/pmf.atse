package rs.nikolapacekvetnic.schoolapp_backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SchoolClassEntity;

public interface SchoolClassRepository extends CrudRepository<SchoolClassEntity, Integer> {
	
	Optional<SchoolClassEntity> findByClassNoAndSectionNoAndGeneration(
			Integer classNo, Integer sectionNo, Integer generation);
}
