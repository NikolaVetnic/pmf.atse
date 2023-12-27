package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.LectureEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SubjectEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SubjectRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.GradeCardRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.LectureRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.SubjectRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.TeacherRepository;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.SubjectService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.SubjectCustomValidator;

import javax.validation.ValidationException;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final GradeCardRepository gradeCardRepository;
    private final LectureRepository lectureRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectCustomValidator subjectValidator;
    private final UserLoginService userLoginService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SubjectServiceImpl(SubjectRepository subjectRepository, SubjectCustomValidator subjectValidator, UserLoginServiceImpl userLoginService, GradeCardRepository gradeCardRepository, LectureRepository lectureRepository, TeacherRepository teacherRepository) {
        this.subjectRepository = subjectRepository;
        this.subjectValidator = subjectValidator;
        this.userLoginService = userLoginService;
        this.gradeCardRepository = gradeCardRepository;
        this.lectureRepository = lectureRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public SubjectEntity addNewSubject(SubjectRegisterDto subjectDTO, BindingResult result) {

        ensureRoleIsAdmin();

        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        subjectValidator.validate(subjectDTO, result);
        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        SubjectEntity subject = new SubjectEntity();
        subject.setName(subjectDTO.getName());
        subject.setTotalHours(subjectDTO.getTotalHours());
        subject.setYearAccredited(subjectDTO.getYearAccredited());

        subjectRepository.save(subject);
        logger.info(subject.getName() + " : created.");

        return subject;
    }

    private String createErrorMessage(BindingResult result) {
        return result.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("\n"));
    }

    public SubjectEntity updateSubject(Integer id, SubjectRegisterDto subjectDTO, BindingResult result) {

        ensureRoleIsAdmin();

        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        subjectValidator.validate(subjectDTO, result);
        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        SubjectEntity subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found."));

        subject.setName(subjectDTO.getName());
        subject.setTotalHours(subjectDTO.getTotalHours());
        subject.setYearAccredited(subjectDTO.getYearAccredited());

        subjectRepository.save(subject);
        logger.info(userLoginService.getLoggedInUsername() + " : updated subject " + subject.getName());

        return subject;
    }

    public void deleteSubject(Integer id) {

        ensureRoleIsAdmin();

        SubjectEntity subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found."));

        for (LectureEntity lecture : subject.getLectures()) {
            TeacherEntity currentTeacher = lecture.getTeacher();
            currentTeacher.getLectures().remove(lecture);
            teacherRepository.save(currentTeacher);

            lecture.getGradeCards().forEach(gc -> {
                gc.setLecture(null);
                gradeCardRepository.save(gc);
            });

            lectureRepository.delete(lecture);
        }

        subjectRepository.delete(subject);
        logger.info(userLoginService.getLoggedInUsername() + " : deleted subject " + subject.getName());
    }

    private void ensureRoleIsAdmin() {
        if (!userLoginService.isAuthorizedAs(EUserRole.ADMIN))
            throw new UnauthorizedException("Unauthorized request.");
    }
}
