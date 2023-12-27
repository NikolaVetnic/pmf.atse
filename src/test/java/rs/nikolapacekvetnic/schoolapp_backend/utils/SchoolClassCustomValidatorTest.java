package rs.nikolapacekvetnic.schoolapp_backend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SchoolClassRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SchoolClassEntity;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.SchoolClassRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchoolClassCustomValidatorTest {

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @InjectMocks
    private SchoolClassCustomValidator validator;

    private SchoolClassRegisterDto schoolClassDto;
    private Errors errors;

    @BeforeEach
    public void setUp() {
        schoolClassDto = new SchoolClassRegisterDto();  // Initialize with test data
        errors = new BeanPropertyBindingResult(schoolClassDto, "schoolClassDto");
    }

    @Test
    public void whenSupportsCalledWithSupportedClass_thenReturnsTrue() {
        assertTrue(validator.supports(SchoolClassRegisterDto.class));
    }

    @Test
    public void whenSupportsCalledWithUnsupportedClass_thenReturnsFalse() {
        assertFalse(validator.supports(String.class));
    }

    @Test
    public void whenValidateCalledWithExistingSchoolClass_thenRejects() {
        schoolClassDto.setClassNo(1);
        schoolClassDto.setSectionNo(1);
        schoolClassDto.setGeneration(2022);

        when(schoolClassRepository.findByClassNoAndSectionNoAndGeneration(1, 1, 2022))
                .thenReturn(Optional.of(new SchoolClassEntity()));

        validator.validate(schoolClassDto, errors);

        assertTrue(errors.hasErrors());

        ObjectError objectError = errors.getAllErrors().get(0);
        String code = objectError.getCode();
        String message = objectError.getDefaultMessage();

        assertEquals("400", code);
        assertEquals("Such class already exists.", message);
    }

    @Test
    public void whenValidateCalledWithNonExistingSchoolClass_thenNoErrors() {
        schoolClassDto.setClassNo(2);
        schoolClassDto.setSectionNo(2);
        schoolClassDto.setGeneration(2022);

        when(schoolClassRepository.findByClassNoAndSectionNoAndGeneration(2, 2, 2022))
                .thenReturn(Optional.empty());

        validator.validate(schoolClassDto, errors);

        assertFalse(errors.hasErrors());
    }
}
