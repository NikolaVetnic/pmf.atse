package rs.nikolapacekvetnic.schoolapp_backend.utils;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SchoolClassRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SchoolClassEntity;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.SchoolClassRepository;

@Component
public class SchoolClassCustomValidator implements Validator {
	
	@Autowired
    SchoolClassRepository schoolClassRepository;
	
	@Override
	public boolean supports(Class<?> myClass) {
		return SchoolClassRegisterDto.class.equals(myClass);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		
		SchoolClassRegisterDto schoolClassDTO = (SchoolClassRegisterDto) target;
		
		Optional<SchoolClassEntity> result = schoolClassRepository.findByClassNoAndSectionNoAndGeneration(
				schoolClassDTO.getClassNo(), schoolClassDTO.getSectionNo(), schoolClassDTO.getGeneration());
		
		if (result.isPresent())
			errors.reject("400", "Such class already exists.");
	}
}
