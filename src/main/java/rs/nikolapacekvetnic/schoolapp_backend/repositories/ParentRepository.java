package rs.nikolapacekvetnic.schoolapp_backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.ParentEntity;

public interface ParentRepository extends CrudRepository<ParentEntity, Integer> {
	
	Optional<ParentEntity> findByEmail(String email);
}
