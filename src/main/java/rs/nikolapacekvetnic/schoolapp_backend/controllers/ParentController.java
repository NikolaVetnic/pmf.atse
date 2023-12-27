package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.RESTError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.ParentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.ParentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.ParentService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/v1/project/parents")
public class ParentController {
	
	private final UserLoginService userLoginService;
	private final ParentService parentService;

	public ParentController(UserLoginService userLoginService, ParentService parentService) {
		this.userLoginService = userLoginService;
		this.parentService = parentService;
	}

	@GetMapping
	public ResponseEntity<?> getParent() {
		try {
			String username = userLoginService.getLoggedInUsername()
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."));
			ParentEntity parent = parentService.getParent(username);

			return new ResponseEntity<>(parent, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}

	@GetMapping("/grades")
	public ResponseEntity<?> getGradeCards() {
		try {
			String username = userLoginService.getLoggedInUsername()
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."));
			List<Set<GradeCardEntity>> gradeCards = parentService.getGradeCards(username);

			return new ResponseEntity<>(gradeCards, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}

	@GetMapping("/grades/{subjectId}")
	public ResponseEntity<?> getGradeCardsForSubject(@PathVariable Integer subjectId) {
		try {
			String username = userLoginService.getLoggedInUsername()
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."));
			List<GradeCardEntity> gradeCards = parentService.getGradeCardsForSubject(username, subjectId);

			return new ResponseEntity<>(gradeCards, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody ParentRegisterDto parentDTO, BindingResult result) {
		try {
			ParentEntity updatedParent = parentService.updateParent(id, parentDTO, result);
			return new ResponseEntity<>(updatedParent, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (ValidationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}

	@PutMapping("/insert/{studentId}/into/{parentId}")
	public ResponseEntity<?> connectStudentWithParent(@PathVariable Integer studentId, @PathVariable Integer parentId) {
		try {
			ParentEntity parent = parentService.connectStudentWithParent(studentId, parentId);
			return new ResponseEntity<>(parent, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(
					new RESTError(e.getStatus().value(), e.getReason()),
					e.getStatus());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		try {
			parentService.deleteParent(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(
					new RESTError(e.getStatus().value(), e.getReason()),
					e.getStatus());
		}
	}
}
