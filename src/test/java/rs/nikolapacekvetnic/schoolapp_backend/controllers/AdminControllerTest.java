package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.AdminRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.AdminService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }


    @Test
    public void whenGetAllUsers_thenReturnUserList() throws Exception {
        List<UserEntity> users = new ArrayList<>();
        users.add(new UserEntity());

        when(adminService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/project/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(users.size())));
    }

    @Test
    public void whenGetLogs_thenReturnLogs() throws Exception {
        String logs = "Log content";
        when(adminService.getLogs()).thenReturn(logs);

        mockMvc.perform(get("/api/v1/project/admin/logs"))
                .andExpect(status().isOk())
                .andExpect(content().string(logs));
    }

    @Test
    void whenUpdateAdmin_thenAdminUpdated() throws Exception {
        Integer adminId = 1;
        AdminRegisterDto adminDTO = new AdminRegisterDto(); // Set up DTO
        adminDTO.setUsername("admin.user");
        adminDTO.setPassword("passW0RD!");
        adminDTO.setConfirmPassword("passW0RD!");
        AdminEntity updatedAdmin = new AdminEntity(); // Set up updated entity

        when(adminService.updateAdmin(eq(adminId), any(AdminRegisterDto.class), any(BindingResult.class))).thenReturn(updatedAdmin);

        mockMvc.perform(put("/api/v1/project/admin/update/" + adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.convertToJson(adminDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeleteAdmin_thenAdminDeleted() throws Exception {
        Integer adminId = 1;
        doNothing().when(adminService).deleteAdmin(adminId);

        mockMvc.perform(delete("/api/v1/project/admin/" + adminId))
                .andExpect(status().isOk());
    }
}

