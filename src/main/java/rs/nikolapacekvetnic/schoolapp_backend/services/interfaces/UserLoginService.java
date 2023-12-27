package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import java.util.Optional;

import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.UserLoginDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;

public interface UserLoginService {

	boolean isAuthorizedAs(EUserRole role);
	Optional<UserLoginDto> getUserLoginDto(String username, String password);
	Optional<String> getLoggedInUsername();
	String getJwtToken(UserEntity user);
}
