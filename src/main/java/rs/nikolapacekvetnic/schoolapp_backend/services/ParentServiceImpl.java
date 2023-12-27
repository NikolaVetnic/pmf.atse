package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.*;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.ParentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.ParentRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.StudentRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.UserRepository;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.ParentService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserCustomValidator;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParentServiceImpl implements ParentService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final UserCustomValidator userValidator;
    private final UserLoginService userLoginService;
    private final UserRepository userRepository;

    @Autowired
    public ParentServiceImpl(ParentRepository parentRepository, UserRepository userRepository, UserLoginService userLoginService, UserCustomValidator userValidator, StudentRepository studentRepository) {
        this.parentRepository = parentRepository;
        this.userRepository = userRepository;
        this.userLoginService = userLoginService;
        this.userValidator = userValidator;
        this.studentRepository = studentRepository;
    }

    public ParentEntity getParent(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."));
        if (user.getRole() != EUserRole.PARENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a parent.");
        }

        logger.info(username + " : viewed own profile.");
        return (ParentEntity) user;
    }

    public List<Set<GradeCardEntity>> getGradeCards(String username) {
        ParentEntity parent = getParent(username);
        logger.info(username + " : viewed children's grades.");

        return parent.getStudents().stream()
                .map(StudentEntity::getGradeCards)
                .collect(Collectors.toList());
    }

    public List<GradeCardEntity> getGradeCardsForSubject(String username, Integer subjectId) {
        ParentEntity parent = getParent(username);
        logger.info(username + " : viewed children's grades for subject.");

        return parent.getStudents().stream()
                .flatMap(s -> s.getGradeCards().stream())
                .filter(g -> g.getLecture().getSubject().getId().equals(subjectId))
                .collect(Collectors.toList());
    }

    public ParentEntity updateParent(Integer id, ParentRegisterDto parentDto, BindingResult result) {
        ensureRoleIsAdmin();

        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        userValidator.validate(parentDto, result);
        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        ParentEntity parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found."));

        updateParentEntity(parent, parentDto);
        parentRepository.save(parent);

        logger.info("Updated parent " + parent.getUsername());
        return parent;
    }

    private void updateParentEntity(ParentEntity parent, ParentRegisterDto parentDto) {
        parent.setEmail(parentDto.getEmail());
        parent.setFirstName(parentDto.getFirstName());
        parent.setLastName(parentDto.getLastName());
        parent.setPassword(parentDto.getPassword());
        parent.setUsername(parentDto.getUsername());
    }

    private String createErrorMessage(BindingResult result) {
        return result.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("\n"));
    }

    public ParentEntity connectStudentWithParent(Integer studentId, Integer parentId) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
        ParentEntity parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found."));

        if (student.getParents().size() > 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student already has both parents.");
        }

        student.getParents().add(parent);
        studentRepository.save(student);

        parent.getStudents().add(student);
        parentRepository.save(parent);

        logger.info("Connected student " + student.getUsername() + " with parent " + parent.getUsername());
        return parent;
    }

    public void deleteParent(Integer id) {
        ensureRoleIsAdmin();

        ParentEntity parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found."));

        parent.getStudents().forEach(student -> {
            student.getParents().remove(parent);
            studentRepository.save(student);
        });

        parentRepository.delete(parent);
        logger.info("Deleted parent " + parent.getUsername());
    }

    private void ensureRoleIsAdmin() {
        if (!userLoginService.isAuthorizedAs(EUserRole.ADMIN))
            throw new UnauthorizedException("Unauthorized request.");
    }
}
