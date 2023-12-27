package rs.nikolapacekvetnic.schoolapp_backend.repositories;

import org.springframework.data.repository.CrudRepository;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;

public interface AdminRepository extends CrudRepository<AdminEntity, Integer> {
	
}
