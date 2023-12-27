package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.*;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserFactory;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.DuplicateEmailException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.DuplicateJmbgException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.ParentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.*;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserRegistrationService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserCustomValidator;

import javax.validation.ValidationException;
import java.util.stream.Collectors;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    final AdminRepository adminRepository;
    final ParentRepository parentRepository;
    final StudentRepository studentRepository;
    final TeacherRepository teacherRepository;
    final UserRepository userRepository;

    final UserLoginService userLoginService;
    final UserCustomValidator userValidator;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserRegistrationServiceImpl(AdminRepository adminRepository, ParentRepository parentRepository, StudentRepository studentRepository, TeacherRepository teacherRepository, UserRepository userRepository, UserLoginService userLoginService, UserCustomValidator userValidator) {
        this.adminRepository = adminRepository;
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.userLoginService = userLoginService;
        this.userValidator = userValidator;
    }

    @Override
    public AdminEntity addNewAdmin(AdminRegisterDto adminRegisterDto, BindingResult result) {

        ensureRoleIsAdmin();

        if (result.hasErrors())
            throw new ValidationException(createErrorMessage(result));

        userValidator.validate(adminRegisterDto, result);
        if (result.hasErrors())
            throw new ValidationException(createErrorMessage(result));

        AdminEntity admin = (AdminEntity) UserFactory.createUser(adminRegisterDto);
        userRepository.save(admin);

        logger.info(admin + " : created.");

        return admin;
    }

    @Override
    public TeacherEntity addNewTeacher(TeacherRegisterDto teacherRegisterDto, BindingResult result) {

        ensureRoleIsAdmin();

        if (teacherRepository.findByEmail(teacherRegisterDto.getEmail()).isPresent())
            throw new DuplicateEmailException("Email must be unique.");

        if (result.hasErrors())
            throw new ValidationException(createErrorMessage(result));

        userValidator.validate(teacherRegisterDto, result);
        if (result.hasErrors())
            throw new ValidationException(createErrorMessage(result));

        TeacherEntity teacher = (TeacherEntity) UserFactory.createUser(teacherRegisterDto);
        userRepository.save(teacher);

        logger.info(teacher + " : created.");

        return teacher;
    }

    @Override
    public ParentEntity addNewParent(ParentRegisterDto parentRegisterDto, BindingResult result) {

        ensureRoleIsAdmin();

        if (parentRepository.findByEmail(parentRegisterDto.getEmail()).isPresent())
            throw new DuplicateEmailException("Email must be unique.");

        if (result.hasErrors())
            throw new ValidationException(createErrorMessage(result));

        userValidator.validate(parentRegisterDto, result);
        if (result.hasErrors())
            throw new ValidationException(createErrorMessage(result));

        ParentEntity parent = (ParentEntity) UserFactory.createUser(parentRegisterDto);
        userRepository.save(parent);

        logger.info(parent + " : created.");

        return parent;
    }

    @Override
    public StudentEntity addNewStudent(StudentRegisterDto studentRegisterDto, BindingResult result) {

        ensureRoleIsAdmin();

        if (studentRepository.findByJmbg(studentRegisterDto.getJmbg()).isPresent())
            throw new DuplicateJmbgException("Personal ID number must be unique.");

        if (result.hasErrors())
            throw new ValidationException(createErrorMessage(result));

        userValidator.validate(studentRegisterDto, result);
        if (result.hasErrors())
            throw new ValidationException(createErrorMessage(result));

        StudentEntity student = (StudentEntity) UserFactory.createUser(studentRegisterDto);
        userRepository.save(student);

        logger.info(student + " : created.");

        return student;
    }

    private void ensureRoleIsAdmin() {
        if (!userLoginService.isAuthorizedAs(EUserRole.ADMIN))
            throw new UnauthorizedException("Unauthorized request.");
    }

    private String createErrorMessage(BindingResult result) {
        return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n"));
    }
}
