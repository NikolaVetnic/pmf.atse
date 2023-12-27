package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.DuplicateEmailException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.TeacherRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.UserRepository;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserCustomValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationServiceImplTest {

    @Mock private TeacherRepository teacherRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserLoginService userLoginService;
    @Mock private UserCustomValidator userValidator;
    @InjectMocks private UserRegistrationServiceImpl userRegistrationService;

    @Test
    public void whenAddNewAdmin_thenSaveAdmin() {
        doNothing().when(userValidator).validate(any(), any(BindingResult.class));

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        AdminRegisterDto adminDto = createAdminRegisterDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(adminDto, "adminDto");

        userRegistrationService.addNewAdmin(adminDto, bindingResult);

        verify(userRepository, times(1)).save(any(AdminEntity.class));
    }

    private AdminRegisterDto createAdminRegisterDto()
    {
        AdminRegisterDto adminDto = new AdminRegisterDto();
        adminDto.setUsername("username");
        adminDto.setPassword("passW0RD!");
        adminDto.setConfirmPassword("passW0RD!");
        adminDto.setRole(EUserRole.ADMIN);

        return adminDto;
    }

    @Test
    public void whenAddNewTeacherWithExistingEmail_thenThrowDuplicateEmailException() {
        String email = "random@mail.com";

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        when(teacherRepository.findByEmail(email)).thenReturn(Optional.of(new TeacherEntity()));

        TeacherRegisterDto teacherDto = new TeacherRegisterDto(); // Set fields accordingly
        teacherDto.setEmail(email);
        BindingResult bindingResult = new BeanPropertyBindingResult(teacherDto, "teacherDto");

        assertThrows(DuplicateEmailException.class, () -> userRegistrationService.addNewTeacher(teacherDto, bindingResult));
    }

    @Test
    public void whenAddNewStudentUnauthorized_thenThrowUnauthorizedException() {
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(false);
        StudentRegisterDto studentDto = new StudentRegisterDto(); // Set fields accordingly
        BindingResult bindingResult = new BeanPropertyBindingResult(studentDto, "studentDto");

        assertThrows(UnauthorizedException.class, () -> userRegistrationService.addNewStudent(studentDto, bindingResult));
    }
}

