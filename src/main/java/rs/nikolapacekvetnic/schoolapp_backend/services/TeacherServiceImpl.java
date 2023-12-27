package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.*;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.*;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.TeacherService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserCustomValidator;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final GradeCardRepository gradeCardRepository;
    private final LectureRepository lectureRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final UserLoginServiceImpl userLoginService;
    private final UserCustomValidator userValidator;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TeacherServiceImpl(GradeCardRepository gradeCardRepository, LectureRepository lectureRepository, SubjectRepository subjectRepository, TeacherRepository teacherRepository, UserRepository userRepository, UserLoginServiceImpl userLoginService, UserCustomValidator userValidator) {
        this.gradeCardRepository = gradeCardRepository;
        this.lectureRepository = lectureRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.userLoginService = userLoginService;
        this.userValidator = userValidator;
    }

    @Override
    public TeacherEntity getTeacher(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."));

        if (user.getRole() != EUserRole.TEACHER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a teacher.");
        }

        logger.info(userLoginService.getLoggedInUsername() + " : viewed own profile.");

        return (TeacherEntity) user;
    }

    @Override
    public List<GradeCardEntity> getTeacherGradeCards(TeacherEntity teacher) {
        logger.info(userLoginService.getLoggedInUsername() + " : viewed own students' grades.");

        return teacher.getLectures().stream()
                .flatMap(l -> l.getGradeCards().stream())
                .collect(Collectors.toList());
    }

    @Override
    public TeacherEntity updateTeacher(Integer id, TeacherRegisterDto teacherDto, BindingResult result) {
        if (!userLoginService.isAuthorizedAs(EUserRole.ADMIN)) {
            throw new UnauthorizedException("Unauthorized request.");
        }

        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        userValidator.validate(teacherDto, result);
        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        TeacherEntity teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found."));

        updateTeacherEntity(teacher, teacherDto);
        teacherRepository.save(teacher);

        logger.info(userLoginService.getLoggedInUsername() + " : updated teacher " + teacher.getUsername());

        return teacher;
    }

    private void updateTeacherEntity(TeacherEntity teacher, TeacherRegisterDto teacherDto) {
        teacher.setEmail(teacherDto.getEmail());
        teacher.setFirstName(teacherDto.getFirstName());
        teacher.setLastName(teacherDto.getLastName());
        teacher.setPassword(teacherDto.getPassword());
        teacher.setUsername(teacherDto.getUsername());
    }

    private String createErrorMessage(BindingResult result) {
        return result.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public void deleteTeacher(Integer id) {
        if (!userLoginService.isAuthorizedAs(EUserRole.ADMIN)) {
            throw new UnauthorizedException("Unauthorized request.");
        }

        TeacherEntity teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found."));

        removeTeacherAssociations(teacher);
        teacherRepository.delete(teacher);

        logger.info(userLoginService.getLoggedInUsername() + " : deleted teacher " + teacher.getUsername());
    }

    private void removeTeacherAssociations(TeacherEntity teacher) {
        for (LectureEntity lecture : teacher.getLectures()) {
            SubjectEntity currSubject = lecture.getSubject();
            currSubject.getLectures().remove(lecture);
            subjectRepository.save(currSubject);

            lecture.getGradeCards().forEach(gc -> {
                gc.setLecture(null);
                gradeCardRepository.save(gc);
            });

            lectureRepository.delete(lecture);
        }
    }
}
