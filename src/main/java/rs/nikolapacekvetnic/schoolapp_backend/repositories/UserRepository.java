package rs.nikolapacekvetnic.schoolapp_backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
	
	Optional<UserEntity> findByUsername(String username);
}
