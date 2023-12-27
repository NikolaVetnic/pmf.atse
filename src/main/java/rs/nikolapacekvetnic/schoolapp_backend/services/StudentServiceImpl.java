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
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.*;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.StudentService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserCustomValidator;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GradeCardRepository gradeCardRepository;
    private final GradeRepository gradeRepository;
    private final LectureRepository lectureRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final UserLoginService userLoginService;
    private final UserCustomValidator userValidator;

    public StudentServiceImpl(GradeCardRepository gradeCardRepository, GradeRepository gradeRepository, LectureRepository lectureRepository, ParentRepository parentRepository, StudentRepository studentRepository, UserRepository userRepository, UserLoginService userLoginService, UserCustomValidator userValidator) {
        this.gradeCardRepository = gradeCardRepository;
        this.gradeRepository = gradeRepository;
        this.lectureRepository = lectureRepository;
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.userLoginService = userLoginService;
        this.userValidator = userValidator;
    }

    @Override
    public StudentEntity getStudent(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."));
        if (user.getRole() != EUserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a student.");
        }

        logger.info(userLoginService.getLoggedInUsername() + " : viewed own profile.");

        return (StudentEntity) user;
    }

    @Override
    public Set<GradeCardEntity> getGradeCards(String username) {
        StudentEntity student = getStudent(username);
        logger.info(userLoginService.getLoggedInUsername() + " : viewed own grades.");

        return student.getGradeCards();
    }

    @Override
    public List<GradeCardEntity> getGradeCardsForSubject(String username, Integer subjectId) {
        StudentEntity student = getStudent(username);
        logger.info(userLoginService.getLoggedInUsername() + " : viewed own grades.");

        return student.getGradeCards().stream()
                .filter(g -> g.getLecture().getSubject().getId().equals(subjectId))
                .collect(Collectors.toList());
    }

    @Override
    public StudentEntity updateStudent(Integer id, StudentRegisterDto studentDto, BindingResult result) {

        ensureRoleIsAdmin();

        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        userValidator.validate(studentDto, result);
        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));

        updateStudentEntity(student, studentDto);
        studentRepository.save(student);

        logger.info(userLoginService.getLoggedInUsername() + " : updated student " + student.getUsername());

        return student;
    }

    private void updateStudentEntity(StudentEntity student, StudentRegisterDto studentDto) {
        student.setJmbg(studentDto.getJmbg());
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setPassword(studentDto.getPassword());
        student.setUsername(studentDto.getUsername());
    }

    private String createErrorMessage(BindingResult result) {
        return result.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public void deleteStudent(Integer id) {
        if (!userLoginService.isAuthorizedAs(EUserRole.ADMIN)) {
            throw new UnauthorizedException("Unauthorized request.");
        }

        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));

        removeStudentAssociations(student);
        studentRepository.delete(student);

        logger.info(userLoginService.getLoggedInUsername() + " : deleted student " + student.getUsername());
    }

    private void removeStudentAssociations(StudentEntity student) {
        student.getParents().forEach(parent -> {
            parent.getStudents().remove(student);
            parentRepository.save(parent);
        });

        student.getGradeCards().forEach(gradeCard -> {
            gradeCard.getLecture().getGradeCards().remove(gradeCard);
            lectureRepository.save(gradeCard.getLecture());

            gradeRepository.deleteAll(gradeCard.getGrades());
            gradeCardRepository.delete(gradeCard);
        });
    }

    private void ensureRoleIsAdmin() {
        if (!userLoginService.isAuthorizedAs(EUserRole.ADMIN))
            throw new UnauthorizedException("Unauthorized request.");
    }
}
