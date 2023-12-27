package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.UserLoginDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/project/login")
public class UserLoginController {


	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final UserLoginService userLoginService;


	public UserLoginController(UserLoginService userLoginService) {
		this.userLoginService = userLoginService;
	}


	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
		Optional<UserLoginDto> userLoginDTOOptional = userLoginService.getUserLoginDto(username, password);

		if (userLoginDTOOptional.isPresent()) {
			UserLoginDto userLoginDTO = userLoginDTOOptional.get();
			logger.info(userLoginDTO.getUsername() + " : logged in.");

			return new ResponseEntity<>(userLoginDTO, HttpStatus.OK);
		}
		
		return new ResponseEntity<>("Username and password do not match", HttpStatus.UNAUTHORIZED);
	}
}
