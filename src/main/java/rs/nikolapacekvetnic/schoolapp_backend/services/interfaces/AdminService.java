package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;

import java.io.IOException;
import java.util.List;

public interface AdminService {

    List<UserEntity> getAllUsers();
    String getLogs() throws IOException;
    AdminEntity updateAdmin(Integer id, AdminRegisterDto adminDTO, BindingResult result);
    void deleteAdmin(Integer id);
}
