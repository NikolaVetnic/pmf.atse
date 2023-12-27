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
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.AdminRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.UserRepository;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.AdminService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserCustomValidator;

import javax.validation.ValidationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final UserLoginService userLoginService;
    private final UserCustomValidator userValidator;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, UserRepository userRepository, UserLoginService userLoginService, UserCustomValidator userValidator) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.userLoginService = userLoginService;
        this.userValidator = userValidator;
    }

    @Override
    public List<UserEntity> getAllUsers() {
        ensureRoleIsAdmin();
        logger.info(userLoginService.getLoggedInUsername() + " : viewed all users.");

        return (List<UserEntity>) userRepository.findAll();
    }

    @Override
    public String getLogs() throws IOException {

        ensureRoleIsAdmin();

        BufferedReader in = getBufferedReader("logs//spring-boot-logging.log");
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null)
            sb.append(line).append("\n");

        logger.info(userLoginService.getLoggedInUsername() + " : viewed logs.");

        return sb.toString();
    }

    protected BufferedReader getBufferedReader(String path) throws IOException {
        return new BufferedReader(new FileReader(path));
    }

    @Override
    public AdminEntity updateAdmin(Integer id, AdminRegisterDto adminDTO, BindingResult result) {
        ensureRoleIsAdmin();

        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        userValidator.validate(adminDTO, result);
        if (result.hasErrors()) {
            throw new ValidationException(createErrorMessage(result));
        }

        AdminEntity admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));

        admin.setUsername(adminDTO.getUsername());
        admin.setPassword(adminDTO.getPassword());

        adminRepository.save(admin);

        logger.info(userLoginService.getLoggedInUsername() + " : updated admin " + admin.getUsername());

        return admin;
    }

    @Override
    public void deleteAdmin(Integer id) {
        ensureRoleIsAdmin();

        AdminEntity admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found."));

        logger.info(userLoginService.getLoggedInUsername() + " : deleted admin " + admin.getUsername());
        adminRepository.delete(admin);
    }

    private String createErrorMessage(BindingResult result) {
        return result.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("\n"));
    }

    private void ensureRoleIsAdmin() {
        if (!userLoginService.isAuthorizedAs(EUserRole.ADMIN))
            throw new UnauthorizedException("Unauthorized request.");
    }
}