package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SchoolClassEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SchoolClassRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.SchoolClassRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.StudentRepository;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.SchoolClassService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.SchoolClassCustomValidator;

import javax.validation.ValidationException;
import java.util.stream.Collectors;

@Service
public class SchoolClassServiceImpl implements SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final SchoolClassCustomValidator schoolClassValidator;
    private final StudentRepository studentRepository;
    private final UserLoginService userLoginService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SchoolClassServiceImpl(SchoolClassRepository schoolClassRepository,
                                  SchoolClassCustomValidator schoolClassValidator,
                                  UserLoginService userLoginService, StudentRepository studentRepository) {
        this.schoolClassRepository = schoolClassRepository;
        this.schoolClassValidator = schoolClassValidator;
        this.userLoginService = userLoginService;
        this.studentRepository = studentRepository;
    }

    @Override
    public SchoolClassEntity addNewSchoolClass(SchoolClassRegisterDto schoolClassDTO, BindingResult result) {

        ensureRoleIsAdmin();

        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        schoolClassValidator.validate(schoolClassDTO, result);
        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        SchoolClassEntity schoolClass = new SchoolClassEntity();
        schoolClass.setClassNo(schoolClassDTO.getClassNo());
        schoolClass.setSectionNo(schoolClassDTO.getSectionNo());
        schoolClass.setGeneration(schoolClassDTO.getGeneration());

        schoolClassRepository.save(schoolClass);
        logger.info("Registered class " + schoolClass.getClassNo() + "-" + schoolClass.getSectionNo());

        return schoolClass;
    }

    private String createErrorMessage(BindingResult result) {
        return result.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public StudentEntity connectStudentWithClass(Integer studentId, Integer schoolClassId) {

        ensureRoleIsAdmin();

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
        SchoolClassEntity schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found."));

        student.setSchoolClass(schoolClass);
        studentRepository.save(student);

        schoolClass.getStudents().add(student);
        schoolClassRepository.save(schoolClass);

        logger.info("Added student " + student.getUsername() + " to class " +
                schoolClass.getClassNo() + "-" + schoolClass.getSectionNo());

        return student;
    }

    private void ensureRoleIsAdmin() {
        if (!userLoginService.isAuthorizedAs(EUserRole.ADMIN))
            throw new UnauthorizedException("Unauthorized request.");
    }
}

