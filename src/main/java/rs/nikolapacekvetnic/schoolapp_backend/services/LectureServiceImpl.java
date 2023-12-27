package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.*;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.LectureRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.*;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.LectureService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;

import java.util.Optional;

@Service
public class LectureServiceImpl implements LectureService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GradeCardRepository gradeCardRepository;
    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final UserLoginService userLoginService;

    public LectureServiceImpl(GradeCardRepository gradeCardRepository, LectureRepository lectureRepository, StudentRepository studentRepository, SubjectRepository subjectRepository, TeacherRepository teacherRepository, UserLoginService userLoginService) {
        this.gradeCardRepository = gradeCardRepository;
        this.lectureRepository = lectureRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.userLoginService = userLoginService;
    }

    @Override
    public LectureEntity connectSubjectWithTeacher(Integer subjectId, Integer teacherId, LectureRegisterDto lectureDTO, BindingResult result) {
        Optional<SubjectEntity> subjectOpt = subjectRepository.findById(subjectId);
        Optional<TeacherEntity> teacherOpt = teacherRepository.findById(teacherId);

        if (!subjectOpt.isPresent() || !teacherOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject or Teacher not found.");
        }

        SubjectEntity subject = subjectOpt.get();
        TeacherEntity teacher = teacherOpt.get();

        Optional<LectureEntity> lectureOpt = lectureRepository.findBySubjectAndTeacher(subject, teacher);
        if (lectureOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lecture already exists.");
        }

        LectureEntity lecture = new LectureEntity();
        lecture.setSubject(subject);
        lecture.setTeacher(teacher);
        lecture.setYear(lectureDTO.getYear());
        lecture.setSemester(lectureDTO.getSemester());
        lectureRepository.save(lecture);

        subject.getLectures().add(lecture);
        subjectRepository.save(subject);

        teacher.getLectures().add(lecture);
        teacherRepository.save(teacher);

        logger.info("Lecture #" + lecture.getId() + " : created.");

        return lecture;
    }

    @Override
    public LectureEntity connectStudentWithLecture(Integer studentId, Integer lectureId) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        Optional<LectureEntity> lectureOpt = lectureRepository.findById(lectureId);

        if (!studentOpt.isPresent() || !lectureOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student or Lecture not found.");
        }

        StudentEntity student = studentOpt.get();
        LectureEntity lecture = lectureOpt.get();

        Optional<GradeCardEntity> gradeCardOpt = gradeCardRepository.findByLectureAndStudent(lecture, student);
        if (gradeCardOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Such grade card already exists.");
        }

        GradeCardEntity gradeCard = new GradeCardEntity();
        gradeCard.setLecture(lecture);
        gradeCard.setStudent(student);
        gradeCardRepository.save(gradeCard);

        student.getGradeCards().add(gradeCard);
        studentRepository.save(student);

        lecture.getGradeCards().add(gradeCard);
        lectureRepository.save(lecture);

        logger.info("Lecture #" + lecture.getId() + " : student " + studentId + " added.");

        return lecture;
    }

    @Override
    public LectureEntity disconnectSubjectWithTeacher(Integer subjectId, Integer teacherId) {
        ensureRoleIsAdmin();

        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found."));
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found."));

        LectureEntity lecture = lectureRepository.findBySubjectAndTeacher(subject, teacher)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Such lecture does not exist."));

        subject.getLectures().remove(lecture);
        subjectRepository.save(subject);

        teacher.getLectures().remove(lecture);
        teacherRepository.save(teacher);

        lectureRepository.delete(lecture);

        return lecture;
    }

    private void ensureRoleIsAdmin() {
        if (!userLoginService.isAuthorizedAs(EUserRole.ADMIN))
            throw new UnauthorizedException("Unauthorized request.");
    }
}

