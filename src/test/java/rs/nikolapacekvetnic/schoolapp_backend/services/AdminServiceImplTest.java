package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.UnauthorizedException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.AdminRepository;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.UserRepository;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.UserCustomValidator;

import javax.validation.ValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;
    @Mock private BindingResult bindingResult;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserLoginService userLoginService;
    @Mock
    private UserCustomValidator userValidator;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void getAllUsersTest() {
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(new UserEntity());
        when(userRepository.findAll()).thenReturn(userEntities);

        List<UserEntity> result = adminService.getAllUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(userRepository).findAll();
        verify(userLoginService).isAuthorizedAs(EUserRole.ADMIN);
    }

    @Test
    void getLogsTest() throws IOException {
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        BufferedReader mockedReader = mock(BufferedReader.class);
        when(mockedReader.readLine()).thenReturn("Log line 1", "Log line 2", null);
        adminService = spy(adminService);
        doReturn(mockedReader).when(adminService).getBufferedReader(anyString());

        String logs = adminService.getLogs();

        assertNotNull(logs);
        assertTrue(logs.contains("Log line 1"));
        assertTrue(logs.contains("Log line 2"));
        verify(userLoginService).isAuthorizedAs(EUserRole.ADMIN);
    }

    @Test
    void updateAdminTest_Success() {
        Integer adminId = 1;
        AdminRegisterDto adminDto = new AdminRegisterDto();
        adminDto.setUsername("newAdmin");
        adminDto.setPassword("newPassword");

        AdminEntity existingAdmin = new AdminEntity();
        existingAdmin.setId(adminId);
        existingAdmin.setUsername("oldAdmin");
        existingAdmin.setPassword("oldPassword");

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(existingAdmin));
        when(bindingResult.hasErrors()).thenReturn(false);

        AdminEntity updatedAdmin = adminService.updateAdmin(adminId, adminDto, bindingResult);

        assertNotNull(updatedAdmin);
        assertEquals("newAdmin", updatedAdmin.getUsername());
        assertEquals("newPassword", updatedAdmin.getPassword());
        verify(adminRepository).save(updatedAdmin);
    }

    @Test
    void updateAdminTest_AdminNotFound() {
        Integer adminId = 99;
        AdminRegisterDto adminDto = new AdminRegisterDto();

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        when(adminRepository.findById(adminId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> adminService.updateAdmin(adminId, adminDto, bindingResult));
    }

    @Test
    void updateAdminTest_ValidationErrors() {
        Integer adminId = 1;
        AdminRegisterDto adminDto = new AdminRegisterDto();

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> adminService.updateAdmin(adminId, adminDto, bindingResult));
    }

    @Test
    void deleteAdminTest_Success() {
        Integer adminId = 1;
        AdminEntity existingAdmin = new AdminEntity();
        existingAdmin.setId(adminId);
        existingAdmin.setUsername("adminUsername");

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(existingAdmin));

        adminService.deleteAdmin(adminId);

        verify(adminRepository).delete(existingAdmin);
    }

    @Test
    void deleteAdminTest_AdminNotFound() {
        Integer adminId = 99;

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        when(adminRepository.findById(adminId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> adminService.deleteAdmin(adminId));
    }

    @Test
    void deleteAdminTest_Unauthorized() {
        Integer adminId = 1;

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> adminService.deleteAdmin(adminId));
    }
}

