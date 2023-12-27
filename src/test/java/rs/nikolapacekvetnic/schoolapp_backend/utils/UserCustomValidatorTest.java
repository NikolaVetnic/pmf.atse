package rs.nikolapacekvetnic.schoolapp_backend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserCustomValidatorTest {

    private UserCustomValidator validator;
    private UserRegisterDto userDto;
    private Errors errors;

    @BeforeEach
    public void setUp() {
        validator = new UserCustomValidator();
        userDto = new UserRegisterDto();
        errors = new BeanPropertyBindingResult(userDto, "userDto");
    }

    @Test
    public void whenSupportsCalledWithSupportedClass_thenReturnsTrue() {
        assertTrue(validator.supports(UserRegisterDto.class));
        assertTrue(validator.supports(AdminRegisterDto.class));
        assertTrue(validator.supports(TeacherRegisterDto.class));
        assertTrue(validator.supports(ParentRegisterDto.class));
        assertTrue(validator.supports(StudentRegisterDto.class));
    }

    @Test
    public void whenValidateCalledWithMatchingPasswords_thenNoErrors() {
        userDto.setPassword("Password");
        userDto.setConfirmPassword("Password");

        validator.validate(userDto, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void whenValidateCalledWithNonMatchingPasswords_thenRejects() {
        userDto.setPassword("Password");
        userDto.setConfirmPassword("Different");

        validator.validate(userDto, errors);

        assertTrue(errors.hasErrors());

        ObjectError objectError = errors.getAllErrors().get(0);
        String code = objectError.getCode();
        String message = objectError.getDefaultMessage();

        assertEquals("400", code);
        assertEquals("Passwords must be the same.", message);
    }
}

