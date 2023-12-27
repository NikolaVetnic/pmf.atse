package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.LectureEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.LectureRegisterDto;

public interface LectureService {

    LectureEntity connectSubjectWithTeacher(Integer subjectId, Integer teacherId, LectureRegisterDto lectureDTO, BindingResult result);
    LectureEntity connectStudentWithLecture(Integer studentId, Integer lectureId);
    LectureEntity disconnectSubjectWithTeacher(Integer subjectId, Integer teacherId);
}
