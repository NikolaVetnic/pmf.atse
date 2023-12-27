package rs.nikolapacekvetnic.schoolapp_backend.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.*;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.LectureRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.*;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;

import java.util.HashSet;
import java.util.Optional;

class LectureServiceImplTest {

    @Mock private GradeCardRepository gradeCardRepository;
    @Mock private LectureRepository lectureRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private TeacherRepository teacherRepository;
    @Mock private UserLoginService userLoginService;

    @InjectMocks private LectureServiceImpl lectureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void connectSubjectWithTeacher_SuccessfulCreation() {
        Integer subjectId = 1, teacherId = 1;
        SubjectEntity subject = new SubjectEntity();
        TeacherEntity teacher = new TeacherEntity();

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(lectureRepository.findBySubjectAndTeacher(subject, teacher)).thenReturn(Optional.empty());

        LectureRegisterDto lectureDTO = new LectureRegisterDto();

        LectureEntity createdLecture = lectureService.connectSubjectWithTeacher(subjectId, teacherId, lectureDTO, null);

        assertNotNull(createdLecture);
        verify(lectureRepository).save(any(LectureEntity.class));
        verify(subjectRepository).save(subject);
        verify(teacherRepository).save(teacher);
    }

    @Test
    void connectSubjectWithTeacher_SubjectOrTeacherNotFound() {
        Integer subjectId = 1, teacherId = 1;
        LectureRegisterDto lectureDTO = new LectureRegisterDto();

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                lectureService.connectSubjectWithTeacher(subjectId, teacherId, lectureDTO, null));
    }

    @Test
    void connectStudentWithLecture_SuccessfulConnection() {
        Integer studentId = 1, lectureId = 1;
        StudentEntity student = new StudentEntity();
        LectureEntity lecture = new LectureEntity();
        lecture.setGradeCards(new HashSet<>());

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(gradeCardRepository.findByLectureAndStudent(lecture, student)).thenReturn(Optional.empty());

        LectureEntity connectedLecture = lectureService.connectStudentWithLecture(studentId, lectureId);

        assertNotNull(connectedLecture);
        verify(gradeCardRepository).save(any(GradeCardEntity.class));
        verify(studentRepository).save(student);
        verify(lectureRepository).save(lecture);
    }

    @Test
    void connectStudentWithLecture_StudentOrLectureNotFound() {
        Integer studentId = 99, lectureId = 99;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                lectureService.connectStudentWithLecture(studentId, lectureId));
    }

    @Test
    void connectStudentWithLecture_GradeCardAlreadyExists() {
        Integer studentId = 1, lectureId = 1;
        StudentEntity student = new StudentEntity();
        LectureEntity lecture = new LectureEntity();
        GradeCardEntity gradeCard = new GradeCardEntity();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(gradeCardRepository.findByLectureAndStudent(lecture, student)).thenReturn(Optional.of(gradeCard));

        assertThrows(ResponseStatusException.class, () ->
                lectureService.connectStudentWithLecture(studentId, lectureId));
    }

    @Test
    void disconnectSubjectWithTeacher_SuccessfulDisconnection() {
        Integer subjectId = 1, teacherId = 1;
        SubjectEntity subject = new SubjectEntity();
        TeacherEntity teacher = new TeacherEntity();
        LectureEntity lecture = new LectureEntity();

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(lectureRepository.findBySubjectAndTeacher(subject, teacher)).thenReturn(Optional.of(lecture));
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        LectureEntity disconnectedLecture = lectureService.disconnectSubjectWithTeacher(subjectId, teacherId);

        assertNotNull(disconnectedLecture);
        verify(subjectRepository).save(subject);
        verify(teacherRepository).save(teacher);
        verify(lectureRepository).delete(lecture);
    }

    @Test
    void disconnectSubjectWithTeacher_SubjectOrTeacherNotFound() {
        Integer subjectId = 99, teacherId = 99;

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () ->
                lectureService.disconnectSubjectWithTeacher(subjectId, teacherId));
    }

    @Test
    void disconnectSubjectWithTeacher_LectureNotFound() {
        Integer subjectId = 1, teacherId = 1;
        SubjectEntity subject = new SubjectEntity();
        TeacherEntity teacher = new TeacherEntity();

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(lectureRepository.findBySubjectAndTeacher(subject, teacher)).thenReturn(Optional.empty());
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () ->
                lectureService.disconnectSubjectWithTeacher(subjectId, teacherId));
    }
}
