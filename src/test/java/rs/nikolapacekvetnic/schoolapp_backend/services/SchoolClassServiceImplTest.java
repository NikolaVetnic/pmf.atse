package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SchoolClassEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SchoolClassRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.SchoolClassRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.StudentRepository;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.SchoolClassCustomValidator;

import javax.validation.ValidationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class SchoolClassServiceImplTest {

    @Mock private SchoolClassRepository schoolClassRepository;
    @Mock private SchoolClassCustomValidator schoolClassValidator;
    @Mock private StudentRepository studentRepository;
    @Mock private UserLoginService userLoginService;
    @InjectMocks private SchoolClassServiceImpl schoolClassService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void whenAddNewSchoolClass_ValidInput_thenSaveSchoolClass() {
        // given
        SchoolClassRegisterDto schoolClassDTO = new SchoolClassRegisterDto()
                .setClassNo(1)
                .setSectionNo(1)
                .setGeneration(2022);
        BindingResult bindingResult = new BeanPropertyBindingResult(schoolClassDTO, "schoolClassDTO");

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        when(schoolClassRepository.save(any(SchoolClassEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SchoolClassEntity schoolClass = schoolClassService.addNewSchoolClass(schoolClassDTO, bindingResult);

        // then
        assertNotNull(schoolClass);
        assertEquals(1, schoolClass.getClassNo());
        assertEquals(1, schoolClass.getSectionNo());
        assertEquals(2022, schoolClass.getGeneration());
        verify(schoolClassRepository).save(schoolClass);
    }

    @Test
    void whenAddNewSchoolClass_InvalidInput_thenThrowValidationException() {
        // given
        SchoolClassRegisterDto schoolClassDTO = new SchoolClassRegisterDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(schoolClassDTO, "schoolClassDTO");
        bindingResult.reject("error");

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        // when / then
        assertThrows(ValidationException.class, () -> schoolClassService.addNewSchoolClass(schoolClassDTO, bindingResult));
    }

    @Test
    void whenAddNewSchoolClass_Unauthorized_thenThrowException() {
        // given
        SchoolClassRegisterDto schoolClassDTO = new SchoolClassRegisterDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(schoolClassDTO, "schoolClassDTO");

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(false);

        // when / then
        assertThrows(UnauthorizedException.class, () -> schoolClassService.addNewSchoolClass(schoolClassDTO, bindingResult));
    }

    @Test
    void whenConnectStudentWithClass_ValidData_thenStudentConnected() {
        // given
        Integer studentId = 1, schoolClassId = 1;
        StudentEntity student = new StudentEntity();
        student.setId(studentId);
        student.setUsername("testStudent");

        SchoolClassEntity schoolClass = new SchoolClassEntity();
        schoolClass.setId(schoolClassId);
        schoolClass.setClassNo(10);
        schoolClass.setSectionNo(1);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(schoolClassRepository.findById(schoolClassId)).thenReturn(Optional.of(schoolClass));
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        // when
        StudentEntity connectedStudent = schoolClassService.connectStudentWithClass(studentId, schoolClassId);

        // then
        assertNotNull(connectedStudent);
        assertEquals(schoolClass, connectedStudent.getSchoolClass());
        verify(studentRepository).save(connectedStudent);
        verify(schoolClassRepository).save(schoolClass);
    }

    @Test
    void whenConnectStudentWithClass_StudentNotFound_thenThrowException() {
        // given
        Integer studentId = 99, schoolClassId = 1;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        // when / then
        assertThrows(ResponseStatusException.class, () -> schoolClassService.connectStudentWithClass(studentId, schoolClassId));
    }

    @Test
    void whenConnectStudentWithClass_Unauthorized_thenThrowException() {
        // given
        Integer studentId = 1, schoolClassId = 1;
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(false);

        // when / then
        assertThrows(UnauthorizedException.class, () -> schoolClassService.connectStudentWithClass(studentId, schoolClassId));
    }
}
