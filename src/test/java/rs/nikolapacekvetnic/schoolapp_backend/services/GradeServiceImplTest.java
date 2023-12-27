package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.*;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.GradeCardRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.GradeRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.LectureRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.StudentRepository;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.EmailService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GradeServiceImplTest {

    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private GradeCardRepository gradeCardRepository;
    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private UserLoginService userLoginService;

    @InjectMocks private GradeServiceImpl gradeService;

    @Test
    public void gradeStudentInLecture_ValidGrade() {
        Integer studentId = 1, lectureId = 1, grade = 4;
        StudentEntity student = new StudentEntity();
        student.setId(1);
        LectureEntity lecture = new LectureEntity();
        lecture.setId(1);
        lecture.setGradeCards(new HashSet<>());
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        when(gradeCardRepository.findByLectureAndStudent(lecture, student)).thenReturn(Optional.empty());

        LectureEntity gradedLecture = gradeService.gradeStudentInLecture(studentId, lectureId, grade);

        verify(gradeRepository).save(any(GradeEntity.class));
        verify(gradeCardRepository, times(2)).save(any(GradeCardEntity.class));
        assertNotNull(gradedLecture);
    }

    @Test
    public void gradeStudentInLecture_InvalidGrade() {
        assertThrows(ResponseStatusException.class, () ->
                gradeService.gradeStudentInLecture(1, 1, 6));
    }

    @Test
    public void gradeStudentInLecture_StudentNotFound() {
        when(studentRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () ->
                gradeService.gradeStudentInLecture(1, 1, 4));
    }

    @Test
    public void gradeStudentInLecture_LectureNotFound() {
        when(studentRepository.findById(1)).thenReturn(Optional.of(new StudentEntity()));
        when(lectureRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () ->
                gradeService.gradeStudentInLecture(1, 1, 4));
    }

    @Test
    public void gradeStudentInLecture_Unauthorized() {
        assertThrows(ResponseStatusException.class, () ->
                gradeService.gradeStudentInLecture(1, 1, 4));
    }

    @Test
    void updateGrade_ValidGrade_Success() {
        Integer gradeId = 1;
        Integer newGradeValue = 4;

        GradeEntity existingGrade = new GradeEntity();
        existingGrade.setId(gradeId);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existingGrade));
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        GradeEntity updatedGrade = gradeService.updateGrade(gradeId, newGradeValue);

        assertEquals(newGradeValue, updatedGrade.getGrade());
        verify(gradeRepository).save(updatedGrade);
    }

    @Test
    void updateGrade_InvalidGrade_ThrowsBadRequest() {
        Integer gradeId = 1;
        Integer invalidGradeValue = 6;

        assertThrows(ResponseStatusException.class, () -> {
            gradeService.updateGrade(gradeId, invalidGradeValue);
        });
    }
}

