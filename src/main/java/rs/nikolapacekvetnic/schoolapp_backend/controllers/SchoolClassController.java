package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.RESTError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SchoolClassEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SchoolClassRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.SchoolClassRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.StudentRepository;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.SchoolClassService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.SchoolClassCustomValidator;

import javax.validation.Valid;
import javax.validation.ValidationException;

@RestController
@RequestMapping(path = "/api/v1/project/classes")
public class SchoolClassController {
	
	final SchoolClassCustomValidator schoolClassValidator;
	final SchoolClassRepository schoolClassRepository;
	final SchoolClassService schoolClassService;
	final StudentRepository studentRepository;

	public SchoolClassController(SchoolClassRepository schoolClassRepository, StudentRepository studentRepository, SchoolClassCustomValidator schoolClassValidator, SchoolClassService schoolClassService) {
		this.schoolClassValidator = schoolClassValidator;
		this.schoolClassRepository = schoolClassRepository;
		this.schoolClassService = schoolClassService;
		this.studentRepository = studentRepository;
	}

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(schoolClassValidator);
	}

	@PostMapping("/register")
	public ResponseEntity<?> addNewSchoolClass(@Valid @RequestBody SchoolClassRegisterDto schoolClassDTO, BindingResult result) {
		try {
			SchoolClassEntity schoolClass = schoolClassService.addNewSchoolClass(schoolClassDTO, result);
			return new ResponseEntity<>(schoolClass, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (ValidationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/insert/{studentId}/into/{schoolClassId}")
	public ResponseEntity<?> connectStudentWithClass(@PathVariable Integer studentId, @PathVariable Integer schoolClassId) {
		try {
			StudentEntity student = schoolClassService.connectStudentWithClass(studentId, schoolClassId);
			return new ResponseEntity<>(student, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}
}
