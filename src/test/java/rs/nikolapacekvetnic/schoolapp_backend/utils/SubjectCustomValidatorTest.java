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
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SubjectRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SubjectEntity;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.SubjectRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubjectCustomValidatorTest {

    @Mock private SubjectRepository subjectRepository;
    @InjectMocks private SubjectCustomValidator validator;

    private SubjectRegisterDto subjectDto;
    private Errors errors;

    @BeforeEach
    public void setUp() {
        subjectDto = new SubjectRegisterDto();
        errors = new BeanPropertyBindingResult(subjectDto, "subjectDto");
    }

    @Test
    public void whenSupportsCalledWithSupportedClass_thenReturnsTrue() {
        assertTrue(validator.supports(SubjectRegisterDto.class));
    }

    @Test
    public void whenValidateCalledWithExistingSubject_thenRejects() {
        subjectDto.setName("Math");
        subjectDto.setYearAccredited(2021);

        when(subjectRepository.findByNameAndYearAccredited("Math", 2021))
                .thenReturn(Optional.of(new SubjectEntity()));

        validator.validate(subjectDto, errors);

        assertTrue(errors.hasErrors());

        ObjectError objectError = errors.getAllErrors().get(0);
        String code = objectError.getCode();
        String message = objectError.getDefaultMessage();

        assertEquals("400", code);
        assertEquals("Such subject already exists.", message);
    }

    @Test
    public void whenValidateCalledWithNonExistingSubject_thenNoErrors() {
        subjectDto.setName("Science");
        subjectDto.setYearAccredited(2021);

        when(subjectRepository.findByNameAndYearAccredited("Science", 2021))
                .thenReturn(Optional.empty());

        validator.validate(subjectDto, errors);

        assertFalse(errors.hasErrors());
    }
}
