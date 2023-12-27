package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.RESTError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SubjectEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SubjectRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.SubjectService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.SubjectCustomValidator;

import javax.validation.Valid;
import javax.validation.ValidationException;

@RestController
@RequestMapping(path = "/api/v1/project/subjects")
public class SubjectController {

	private final SubjectService subjectService;
	final SubjectCustomValidator subjectValidator;

	public SubjectController(SubjectService subjectService, SubjectCustomValidator subjectValidator) {
		this.subjectService = subjectService;
		this.subjectValidator = subjectValidator;
	}

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(subjectValidator);
	}

	@PostMapping("/register")
	public ResponseEntity<?> addNewSubject(@Valid @RequestBody SubjectRegisterDto subjectDTO, BindingResult result) {
		try {
			SubjectEntity subject = subjectService.addNewSubject(subjectDTO, result);
			return new ResponseEntity<>(subject, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		} catch (ValidationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody SubjectRegisterDto subjectDTO, BindingResult result) {
		try {
			SubjectEntity updatedSubject = subjectService.updateSubject(id, subjectDTO, result);
			return new ResponseEntity<>(updatedSubject, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		} catch (ValidationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(
					new RESTError(e.getStatus().value(), e.getReason()),
					e.getStatus());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		try {
			subjectService.deleteSubject(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(
					new RESTError(e.getStatus().value(), e.getReason()),
					e.getStatus());
		}
	}
}
