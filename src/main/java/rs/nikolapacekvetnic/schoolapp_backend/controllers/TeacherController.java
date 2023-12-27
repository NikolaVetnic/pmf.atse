package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.RESTError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.UserLoginServiceImpl;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.TeacherService;

import javax.validation.Valid;
import javax.validation.ValidationException;

@RestController
@RequestMapping(path = "/api/v1/project/teacher")
public class TeacherController {

	private final UserLoginServiceImpl userLoginService;
	private final TeacherService teacherService;

	public TeacherController(UserLoginServiceImpl userLoginService, TeacherService teacherService) {
		this.userLoginService = userLoginService;
		this.teacherService = teacherService;
	}

	@GetMapping
	public ResponseEntity<?> getTeacher() {
		try {
			String username = userLoginService.getLoggedInUsername()
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."));
			TeacherEntity teacher = teacherService.getTeacher(username);

			return new ResponseEntity<>(teacher, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(
					new RESTError(e.getStatus().value(), e.getReason()),
					e.getStatus());
		}
	}

	@GetMapping("/grades")
	public ResponseEntity<?> getGradeCards() {
		TeacherEntity teacher = (TeacherEntity) getTeacher().getBody();

		return new ResponseEntity<>(teacherService.getTeacherGradeCards(teacher), HttpStatus.OK);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody TeacherRegisterDto teacherDTO, BindingResult result) {
		try {
			TeacherEntity updatedTeacher = teacherService.updateTeacher(id, teacherDTO, result);
			return new ResponseEntity<>(updatedTeacher, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (ValidationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(
					new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		try {
			teacherService.deleteTeacher(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(
					new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}
}
