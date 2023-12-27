package rs.nikolapacekvetnic.schoolapp_backend.utils;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.ParentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.UserRegisterDto;

@Component
public class UserCustomValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> myClass) {
		return UserRegisterDto.class.equals(myClass) 	||
			   AdminRegisterDto.class.equals(myClass)	||
			   TeacherRegisterDto.class.equals(myClass) ||
			   ParentRegisterDto.class.equals(myClass)	||
			   StudentRegisterDto.class.equals(myClass)	;
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		
		UserRegisterDto user = (UserRegisterDto) target;
		
		if (user.getPassword() != null && !user.getPassword().equals(user.getConfirmPassword()))
			errors.reject("400", "Passwords must be the same.");
	}
}
