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
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeEntity;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.GradeService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.JsonUtil;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GradeControllerTest {

    private MockMvc mockMvc;

    @Mock private GradeService gradeService;
    @InjectMocks private GradeController gradeController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(gradeController).build();
    }

    @Test
    public void whenUpdateGrade_thenSuccess() throws Exception {
        Integer gradeId = 1;
        Integer newGradeValue = 4;
        GradeEntity updatedGrade = new GradeEntity();
        updatedGrade.setId(gradeId);
        updatedGrade.setGrade(newGradeValue);

        when(gradeService.updateGrade(anyInt(), anyInt())).thenReturn(updatedGrade);

        mockMvc.perform(put("/api/v1/project/grade/update/{id}/{grade}", gradeId, newGradeValue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(JsonUtil.convertToJson(updatedGrade))))
                .andExpect(status().isOk());
    }

    @Test
    public void whenDeleteGrade_thenSuccess() throws Exception {
        Integer gradeId = 1;

        mockMvc.perform(delete("/api/v1/project/grade/{id}", gradeId))
                .andExpect(status().isOk());
    }
}
