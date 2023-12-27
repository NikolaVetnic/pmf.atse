package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.DuplicateEmailException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.DuplicateJmbgException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.RESTError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.ParentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.ParentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.*;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserRegistrationService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserCustomValidator;

import javax.validation.Valid;
import javax.validation.ValidationException;

@RestController
@RequestMapping(path = "/api/v1/project/registration")
public class UserRegistrationController {
	
	
	final AdminRepository adminRepository;
	final ParentRepository parentRepository;
	final StudentRepository studentRepository;
	final TeacherRepository teacherRepository;
	final UserRepository userRepository;

	final UserRegistrationService userRegistrationService;
	final UserLoginService userLoginService;
	final UserCustomValidator userValidator;


	public UserRegistrationController(AdminRepository adminRepository, ParentRepository parentRepository, StudentRepository studentRepository, TeacherRepository teacherRepository, UserRepository userRepository, UserRegistrationService userRegistrationService, UserLoginService userLoginService, UserCustomValidator userValidator) {
		this.adminRepository = adminRepository;
		this.parentRepository = parentRepository;
		this.studentRepository = studentRepository;
		this.teacherRepository = teacherRepository;
		this.userRepository = userRepository;
		this.userRegistrationService = userRegistrationService;
		this.userLoginService = userLoginService;
		this.userValidator = userValidator;
	}


	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(userValidator);
	}
	

	@RequestMapping(method = RequestMethod.POST, value = "/admins")
	public ResponseEntity<?> addNewAdmin(@Valid @RequestBody AdminRegisterDto adminDTO, BindingResult result) {
		try {
			AdminEntity admin = userRegistrationService.addNewAdmin(adminDTO, result);
			return new ResponseEntity<>(admin, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (ValidationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/teachers")
	public ResponseEntity<?> addNewTeacher(@Valid @RequestBody TeacherRegisterDto teacherDTO, BindingResult result) {
		try {
			TeacherEntity teacher = userRegistrationService.addNewTeacher(teacherDTO, result);
			return new ResponseEntity<>(teacher, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (DuplicateEmailException|ValidationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/parents")
	public ResponseEntity<?> addNewParent(@Valid @RequestBody ParentRegisterDto parentDTO, BindingResult result) {
		try {
			ParentEntity parent = userRegistrationService.addNewParent(parentDTO, result);
			return new ResponseEntity<>(parent, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (DuplicateEmailException|ValidationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/students")
	public ResponseEntity<?> addNewStudent(@Valid @RequestBody StudentRegisterDto studentDTO, BindingResult result) {
		try {
			StudentEntity student = userRegistrationService.addNewStudent(studentDTO, result);
			return new ResponseEntity<>(student, HttpStatus.OK);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(
					new RESTError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
		} catch (DuplicateJmbgException|ValidationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
