package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.RESTError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeEntity;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.GradeService;

@RestController
@RequestMapping(path = "/api/v1/project/grade")
public class GradeController {

	private final GradeService gradeService;

	public GradeController(GradeService gradeService) {
		this.gradeService = gradeService;
	}

	@PutMapping("/update/{id}/{grade}")
	public ResponseEntity<?> update(@PathVariable Integer id, @PathVariable Integer grade) {
		try {
			GradeEntity updatedGrade = gradeService.updateGrade(id, grade);
			return new ResponseEntity<>(updatedGrade, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		try {
			gradeService.deleteGrade(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}
}
