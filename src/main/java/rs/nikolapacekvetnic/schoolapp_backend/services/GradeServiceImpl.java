package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.*;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.*;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.EmailService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.GradeService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class GradeServiceImpl implements GradeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GradeRepository gradeRepository;
    private final GradeCardRepository gradeCardRepository;
    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;
    private final EmailService emailService;
    private final UserLoginService userLoginService;

    public GradeServiceImpl(GradeRepository gradeRepository, GradeCardRepository gradeCardRepository, LectureRepository lectureRepository, StudentRepository studentRepository, EmailService emailService, UserLoginService userLoginService) {
        this.gradeRepository = gradeRepository;
        this.gradeCardRepository = gradeCardRepository;
        this.lectureRepository = lectureRepository;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
        this.userLoginService = userLoginService;
    }

    @Override
    public LectureEntity gradeStudentInLecture(Integer studentId, Integer lectureId, Integer grade) {
        if (!(0 < grade && grade <= 5)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid grade.");
        }

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
        LectureEntity lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture not found."));

        // check if the current user is authorized
        boolean isAdmin = userLoginService.isAuthorizedAs(EUserRole.ADMIN);
        boolean isTeacher = userLoginService.getLoggedInUsername().isPresent() &&
                userLoginService.getLoggedInUsername().get().equals(lecture.getTeacher().getUsername());

        if (!isAdmin && !isTeacher) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized request.");
        }

        GradeCardEntity gradeCard = getOrCreateGradeCard(student, lecture);
        createAndSaveGrade(grade, gradeCard);
        sendGradeNotification(student, gradeCard, grade);

        logger.info("Lecture #" + lecture.getId() + " : student " + studentId + " graded " + grade);

        return lecture;
    }

    private GradeCardEntity getOrCreateGradeCard(StudentEntity student, LectureEntity lecture) {
        return gradeCardRepository.findByLectureAndStudent(lecture, student)
                .orElseGet(() -> {
                    GradeCardEntity newGradeCard = new GradeCardEntity();
                    newGradeCard.setLecture(lecture);
                    newGradeCard.setStudent(student);
                    gradeCardRepository.save(newGradeCard);

                    student.getGradeCards().add(newGradeCard);
                    studentRepository.save(student);

                    lecture.getGradeCards().add(newGradeCard);
                    lectureRepository.save(lecture);

                    return newGradeCard;
                });
    }

    private void createAndSaveGrade(Integer gradeValue, GradeCardEntity gradeCard) {
        GradeEntity newGrade = new GradeEntity();
        newGrade.setDate(LocalDate.now());
        newGrade.setGrade(gradeValue);
        newGrade.setGradeCard(gradeCard);
        gradeRepository.save(newGrade);

        gradeCard.getGrades().add(newGrade);
        gradeCardRepository.save(gradeCard);
    }

    private void sendGradeNotification(StudentEntity student, GradeCardEntity gradeCard, Integer grade) {
        Optional<ParentEntity> parentOpt = student.getParents().stream().findFirst();
        if (parentOpt.isPresent()) {
            EmailObject emailObject = new EmailObject();
            emailObject.setTo(parentOpt.get().getEmail());
            emailObject.setSubject(String.format("%s %s - new grade in subject '%s'",
                    student.getLastName(), student.getFirstName(),
                    gradeCard.getLecture().getSubject().getName()));
            emailObject.setText(String.format("Your child was graded %d today.", grade));

            emailService.sendSimpleMessage(emailObject);
            logger.info("Lecture #" + gradeCard.getLecture().getId() + " : parent notified.");
        } else {
            logger.info("Lecture #" + gradeCard.getLecture().getId() + " : parent not found.");
        }
    }

    @Override
    public GradeEntity updateGrade(Integer id, Integer grade) {
        if (!(0 < grade && grade <= 5)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid grade.");
        }

        GradeEntity gradeEntity = gradeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found."));

        boolean isAdmin = userLoginService.isAuthorizedAs(EUserRole.ADMIN);
        boolean isTeacher = userLoginService.getLoggedInUsername().isPresent() &&
                userLoginService.getLoggedInUsername().get().equals(gradeEntity.getGradeCard().getLecture().getTeacher().getUsername());

        if (!isAdmin && !isTeacher) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized request.");
        }

        gradeEntity.setGrade(grade);
        gradeRepository.save(gradeEntity);

        logger.info("Grade #" + gradeEntity.getId() + " : updated.");

        return gradeEntity;
    }

    @Override
    public void deleteGrade(Integer id) {
        GradeEntity grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found."));

        GradeCardEntity gradeCard = grade.getGradeCard();
        gradeCard.getGrades().remove(grade);
        gradeCardRepository.save(gradeCard);

        gradeRepository.delete(grade);

        logger.info("Grade #" + grade.getId() + " : deleted.");
    }
}
