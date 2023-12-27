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
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.LectureEntity;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.GradeService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.LectureService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.JsonUtil;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LectureControllerTest {

    private MockMvc mockMvc;
    @Mock private LectureService lectureService;
    @Mock private GradeService gradeService;
    @InjectMocks private LectureController lectureController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(lectureController).build();
    }

    @Test
    void whenConnectSubjectWithTeacher_thenLectureReturned() throws Exception {
        LectureEntity lecture = new LectureEntity();
        when(lectureService.connectSubjectWithTeacher(anyInt(), anyInt(), any(), any())).thenReturn(lecture);

        mockMvc.perform(put("/api/v1/project/lectures/register/{subjectId}/into/{teacherId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(JsonUtil.convertToJson(lecture))))
                .andExpect(status().isOk());
    }

    @Test
    public void whenConnectStudentWithLecture_thenReturnLecture() throws Exception {
        Integer studentId = 1, lectureId = 1;
        LectureEntity mockLecture = new LectureEntity();

        when(lectureService.connectStudentWithLecture(studentId, lectureId)).thenReturn(mockLecture);

        mockMvc.perform(put("/api/v1/project/lectures/insert/" + studentId + "/into/" + lectureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockLecture.getId()));
    }

    @Test
    public void whenGradeStudentInLecture_thenReturnLecture() throws Exception {
        Integer studentId = 1, lectureId = 1, grade = 4;
        LectureEntity mockLecture = new LectureEntity();

        when(gradeService.gradeStudentInLecture(studentId, lectureId, grade)).thenReturn(mockLecture);

        mockMvc.perform(put("/api/v1/project/lectures/grade/" + studentId + "/in/" + lectureId + "/with/" + grade))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockLecture.getId()));
    }

    @Test
    public void whenDisconnectSubjectWithTeacher_thenReturnLecture() throws Exception {
        Integer subjectId = 1, teacherId = 1;
        LectureEntity mockLecture = new LectureEntity();

        when(lectureService.disconnectSubjectWithTeacher(subjectId, teacherId)).thenReturn(mockLecture);

        mockMvc.perform(put("/api/v1/project/lectures/unregister/" + subjectId + "/into/" + teacherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockLecture.getId()));
    }
}
