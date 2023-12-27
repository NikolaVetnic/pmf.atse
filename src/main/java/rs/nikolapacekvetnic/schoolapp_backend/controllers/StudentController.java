package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.RESTError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.StudentService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/v1/project/student")
public class StudentController {

	private final UserLoginService userLoginService;
	private final StudentService studentService;

	public StudentController(UserLoginService userLoginService, StudentService studentService) {
		this.userLoginService = userLoginService;
		this.studentService = studentService;
	}

	@GetMapping
	public ResponseEntity<?> getStudent() {
		try {
			String username = userLoginService.getLoggedInUsername()
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."));
			StudentEntity student = studentService.getStudent(username);
			return new ResponseEntity<>(student, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(
					new RESTError(e.getStatus().value(), e.getReason()),
					e.getStatus());
		}
	}


	@GetMapping("/grades")
	public ResponseEntity<?> getGradeCards() {
		try {
			String username = userLoginService.getLoggedInUsername()
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."));
			Set<GradeCardEntity> gradeCards = studentService.getGradeCards(username);

			return new ResponseEntity<>(gradeCards, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(
					new RESTError(e.getStatus().value(), e.getReason()),
					e.getStatus());
		}
	}

	@GetMapping("/grades/{subjectId}")
	public ResponseEntity<?> getGradeCardsForSubject(@PathVariable Integer subjectId) {
		try {
			String username = userLoginService.getLoggedInUsername()
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."));
			List<GradeCardEntity> gradeCards = studentService.getGradeCardsForSubject(username, subjectId);

			return new ResponseEntity<>(gradeCards, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(
					new RESTError(e.getStatus().value(), e.getReason()),
					e.getStatus());
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody StudentRegisterDto studentDTO, BindingResult result) {
		try {
			StudentEntity updatedStudent = studentService.updateStudent(id, studentDTO, result);
			return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
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
			studentService.deleteStudent(id);
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