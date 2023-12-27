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
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeCardEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.ParentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.services.UserLoginServiceImpl;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.ParentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ParentControllerTest {

    private MockMvc mockMvc;

    @Mock private UserLoginServiceImpl userLoginService;
    @Mock private ParentService parentService;
    @InjectMocks private ParentController parentController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(parentController).build();
    }

    @Test
    public void whenGetParent_thenReturnParentEntity() throws Exception {
        String username = "parent.username";
        ParentEntity parent = new ParentEntity();

        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of(username));
        when(parentService.getParent(username)).thenReturn(parent);

        mockMvc.perform(get("/api/v1/project/parents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(parent.getId()));
    }

    @Test
    public void whenGetGradeCards_thenReturnGradeCardList() throws Exception {
        String username = "parent.username";
        List<Set<GradeCardEntity>> gradeCardsList = new ArrayList<>();

        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of(username));
        when(parentService.getGradeCards(username)).thenReturn(gradeCardsList);

        mockMvc.perform(get("/api/v1/project/parents/grades"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenGetGradeCardsForSubject_thenReturnFilteredGradeCardList() throws Exception {
        String username = "parent.username";
        Integer subjectId = 1;
        List<GradeCardEntity> gradeCards = new ArrayList<>();

        when(userLoginService.getLoggedInUsername()).thenReturn(Optional.of(username));
        when(parentService.getGradeCardsForSubject(username, subjectId)).thenReturn(gradeCards);

        mockMvc.perform(get("/api/v1/project/parents/grades/" + subjectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
