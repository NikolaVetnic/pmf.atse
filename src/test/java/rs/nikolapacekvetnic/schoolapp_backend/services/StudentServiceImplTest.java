package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.*;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.*;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserCustomValidator;

import javax.validation.ValidationException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock private BindingResult bindingResult;
    @Mock private GradeCardRepository gradeCardRepository;
    @Mock private GradeRepository gradeRepository;
    @Mock private LectureRepository lectureRepository;
    @Mock private ParentRepository parentRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserLoginService userLoginService;
    @Mock private UserCustomValidator userValidator;
    @InjectMocks private StudentServiceImpl studentService;

    @Test
    void whenGetStudent_thenReturnStudent() {
        String username = "studentUsername";
        StudentEntity expectedStudent = new StudentEntity();
        expectedStudent.setUsername(username);
        expectedStudent.setRole(EUserRole.STUDENT);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedStudent));
        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of(username));

        StudentEntity actualStudent = studentService.getStudent(username);

        assertEquals(expectedStudent, actualStudent);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void whenGetGradeCards_thenReturnGradeCards() {
        String username = "studentUsername";
        StudentEntity student = new StudentEntity();
        student.setUsername(username);
        student.setRole(EUserRole.STUDENT);
        Set<GradeCardEntity> expectedGradeCards = new HashSet<>();
        expectedGradeCards.add(new GradeCardEntity()); // Add some mock grade cards

        student.setGradeCards(expectedGradeCards);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(student));
        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of(username));

        Set<GradeCardEntity> actualGradeCards = studentService.getGradeCards(username);

        assertEquals(expectedGradeCards, actualGradeCards);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void whenGetGradeCardsForSubject_thenReturnFilteredGradeCards() {
        String username = "studentUsername";
        Integer subjectId = 1;
        StudentEntity student = new StudentEntity();
        student.setUsername(username);
        student.setRole(EUserRole.STUDENT);

        GradeCardEntity gradeCard1 = new GradeCardEntity(); // Create grade card for subjectId
        LectureEntity lecture1 = new LectureEntity();
        SubjectEntity subject1 = new SubjectEntity();
        subject1.setId(subjectId);
        lecture1.setSubject(subject1);
        gradeCard1.setLecture(lecture1);

        GradeCardEntity gradeCard2 = new GradeCardEntity(); // Create grade card for another subject
        LectureEntity lecture2 = new LectureEntity();
        SubjectEntity subject2 = new SubjectEntity();
        subject2.setId(subjectId + 1);
        lecture2.setSubject(subject2);
        gradeCard2.setLecture(lecture2);

        Set<GradeCardEntity> gradeCards = new HashSet<>(Arrays.asList(gradeCard1, gradeCard2));
        student.setGradeCards(gradeCards);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(student));
        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of(username));

        List<GradeCardEntity> actualGradeCards = studentService.getGradeCardsForSubject(username, subjectId);

        assertTrue(actualGradeCards.contains(gradeCard1));
        assertFalse(actualGradeCards.contains(gradeCard2));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void whenUpdateStudent_ValidData_thenUpdateStudent() {
        Integer id = 1;
        StudentRegisterDto studentDto = new StudentRegisterDto();
        studentDto.setJmbg("1234567890123");
        studentDto.setFirstName("John");
        studentDto.setLastName("Doe");
        studentDto.setUsername("john.doe");
        studentDto.setPassword("password");

        StudentEntity existingStudent = new StudentEntity();
        existingStudent.setId(id);
        existingStudent.setJmbg("1234567890123");
        existingStudent.setFirstName("Jane");
        existingStudent.setLastName("Doe");
        existingStudent.setUsername("jane.doe");

        when(studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        StudentEntity updatedStudent = studentService.updateStudent(id, studentDto, bindingResult);

        assertNotNull(updatedStudent);
        assertEquals(studentDto.getFirstName(), updatedStudent.getFirstName());
        assertEquals(studentDto.getLastName(), updatedStudent.getLastName());
        assertEquals(studentDto.getUsername(), updatedStudent.getUsername());
        verify(studentRepository).save(updatedStudent);
    }

    @Test
    void whenUpdateStudent_InvalidData_thenThrowValidationException() {
        Integer id = 1;
        StudentRegisterDto studentDto = new StudentRegisterDto();
        // Populate studentDto with invalid data

        when(bindingResult.hasErrors()).thenReturn(true);
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        assertThrows(ValidationException.class, () -> studentService.updateStudent(id, studentDto, bindingResult));
    }

    @Test
    void whenUpdateStudent_UnauthorizedUser_thenThrowUnauthorizedException() {
        Integer id = 1;
        StudentRegisterDto studentDto = new StudentRegisterDto();
        // Populate studentDto with valid data

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> studentService.updateStudent(id, studentDto, bindingResult));
    }

    @Test
    void whenDeleteStudent_ValidStudent_thenStudentDeleted() {
        StudentEntity student = createStudent();

        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        studentService.deleteStudent(student.getId());

        for (ParentEntity parent : student.getParents())
            verify(parentRepository).save(parent);

        verify(gradeCardRepository, times(2)).delete(any());
        verify(gradeRepository, times(2)).deleteAll(any());
        verify(studentRepository, times(1)).delete(student);
    }

    private StudentEntity createStudent() {
        StudentEntity student = new StudentEntity();

        // mock basic data
        student.setId(1);
        student.setFirstName("Not");
        student.setLastName("Important");
        student.setUsername("not.important");

        // mock grades and grade cards
        GradeEntity gradeEntity1 = new GradeEntity();
        GradeEntity gradeEntity2 = new GradeEntity();

        Set<GradeEntity> gradeEntities = new HashSet<>();
        gradeEntities.add(gradeEntity1);
        gradeEntities.add(gradeEntity2);

        GradeCardEntity gradeCardEntity1 = new GradeCardEntity();
        gradeCardEntity1.setLecture(new LectureEntity());
        gradeCardEntity1.setGrades(gradeEntities);

        GradeCardEntity gradeCardEntity2 = new GradeCardEntity();
        gradeCardEntity2.setLecture(new LectureEntity());

        Set<GradeCardEntity> gradeCards = new HashSet<>();
        gradeCards.add(gradeCardEntity1);
        gradeCards.add(gradeCardEntity2);

        student.setGradeCards(gradeCards);

        // mock parents
        ParentEntity parent1 = new ParentEntity();
        ParentEntity parent2 = new ParentEntity();

        Set<ParentEntity> parents = new HashSet<>(Arrays.asList(parent1, parent2));
        student.setParents(parents);

        return student;
    }


    @Test
    void whenDeleteStudent_StudentNotFound_thenThrowNotFoundException() {
        Integer id = 99;

        when(studentRepository.findById(id)).thenReturn(Optional.empty());
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> studentService.deleteStudent(id));
    }

    @Test
    void whenDeleteStudent_UnauthorizedUser_thenThrowUnauthorizedException() {
        Integer id = 1;

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> studentService.deleteStudent(id));
    }
}
