package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.RESTError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.AdminService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/project/admin")
public class AdminController {

	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		try {
			List<UserEntity> users = adminService.getAllUsers();
			return new ResponseEntity<>(users, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping("/logs")
	public ResponseEntity<?> getLogs() {
		try {
			String logs = adminService.getLogs();
			return new ResponseEntity<>(logs, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (IOException e) {
			return new ResponseEntity<>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error reading logs: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateAdmin(@PathVariable Integer id, @Valid @RequestBody AdminRegisterDto adminDTO, BindingResult result) {
		try {
			AdminEntity updatedAdmin = adminService.updateAdmin(id, adminDTO, result);
			return new ResponseEntity<>(updatedAdmin, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (ValidationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAdmin(@PathVariable Integer id) {
		try {
			adminService.deleteAdmin(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}
}
