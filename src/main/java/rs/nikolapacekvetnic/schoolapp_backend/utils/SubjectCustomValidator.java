package rs.nikolapacekvetnic.schoolapp_backend.utils;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SubjectRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SubjectEntity;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.SubjectRepository;

@Component
public class SubjectCustomValidator implements Validator {
	
	@Autowired
    SubjectRepository subjectRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return SubjectRegisterDto.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		
		SubjectRegisterDto subjectDTO = (SubjectRegisterDto) target;
		
		Optional<SubjectEntity> result = subjectRepository.findByNameAndYearAccredited(
				subjectDTO.getName(), subjectDTO.getYearAccredited());
		
		if (result.isPresent())
			errors.reject("400", "Such subject already exists.");
	}
}
