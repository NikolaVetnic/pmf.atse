package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.LectureEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.TeacherRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.UserRepository;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserCustomValidator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceImplTest {

    @Mock private TeacherRepository teacherRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserLoginServiceImpl userLoginService;
    @Mock private BindingResult bindingResult;
    @Mock private UserCustomValidator userValidator;

    @InjectMocks private TeacherServiceImpl teacherService;

    @Test
    public void whenGetTeacher_ValidUser_thenReturnTeacher() {
        String username = "teacherUsername";
        TeacherEntity expectedTeacher = new TeacherEntity();
        expectedTeacher.setRole(EUserRole.TEACHER);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedTeacher));

        TeacherEntity actualTeacher = teacherService.getTeacher(username);

        assertEquals(expectedTeacher, actualTeacher);
    }

    @Test
    public void whenGetTeacher_UserNotFound_thenThrowException() {
        String username = "invalidUsername";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> teacherService.getTeacher(username));
    }

    @Test
    public void whenGetTeacherGradeCards_thenReturnGradeCardsList() {
        TeacherEntity teacher = mock(TeacherEntity.class);
        List<LectureEntity> lectureList = new ArrayList<>();

        LectureEntity lecture = mock(LectureEntity.class);
        GradeCardEntity gradeCard = mock(GradeCardEntity.class);
        lectureList.add(lecture);

        Set<GradeCardEntity> gradeCardList = new HashSet<>();
        gradeCardList.add(gradeCard);
        when(lecture.getGradeCards()).thenReturn(gradeCardList);

        when(teacher.getLectures()).thenReturn(lectureList);

        List<GradeCardEntity> expectedGradeCards = new ArrayList<>(gradeCardList);
        List<GradeCardEntity> actualGradeCards = teacherService.getTeacherGradeCards(teacher);

        assertEquals(expectedGradeCards, actualGradeCards);
    }


    @Test
    public void whenUpdateTeacher_ValidData_thenUpdateTeacher() {
        doNothing().when(userValidator).validate(any(), any(BindingResult.class));

        TeacherEntity existingTeacher = new TeacherEntity();
        existingTeacher.setId(1);
        existingTeacher.setFirstName("John");
        existingTeacher.setLastName("Olson");

        TeacherRegisterDto teacherDto = new TeacherRegisterDto();
        teacherDto.setFirstName("Mike");
        teacherDto.setLastName("Newton");

        when(teacherRepository.findById(existingTeacher.getId())).thenReturn(Optional.of(existingTeacher));
        when(bindingResult.hasErrors()).thenReturn(false);

        // mock userLoginService to authorize as ADMIN
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        TeacherEntity updatedTeacher = teacherService.updateTeacher(existingTeacher.getId(), teacherDto, bindingResult);

        verify(teacherRepository, times(1)).save(any(TeacherEntity.class));
        assertEquals("Mike", updatedTeacher.getFirstName());
        assertEquals("Newton", updatedTeacher.getLastName());
    }

    @Test
    public void whenUpdateTeacher_TeacherNotFound_thenThrowException() {
        doNothing().when(userValidator).validate(any(), any(BindingResult.class));

        Integer id = 99;
        TeacherRegisterDto teacherDto = new TeacherRegisterDto();
        when(teacherRepository.findById(id)).thenReturn(Optional.empty());
        when(bindingResult.hasErrors()).thenReturn(false);

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> teacherService.updateTeacher(id, teacherDto, bindingResult));
    }

    @Test
    public void whenDeleteTeacher_ValidId_thenDeleteTeacher() {
        Integer id = 1;
        TeacherEntity teacher = new TeacherEntity();
        when(teacherRepository.findById(id)).thenReturn(Optional.of(teacher));

        // mock userLoginService to authorize as ADMIN
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        teacherService.deleteTeacher(id);

        verify(teacherRepository).delete(teacher);
    }

    @Test
    public void whenDeleteTeacher_TeacherNotFound_thenThrowException() {
        Integer nonExistingId = 99;
        when(teacherRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // mock userLoginService to authorize as ADMIN
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> teacherService.deleteTeacher(nonExistingId));
    }
}

