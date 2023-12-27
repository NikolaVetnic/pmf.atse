package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.GradeEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.LectureEntity;

public interface GradeService {

    LectureEntity gradeStudentInLecture(Integer studentId, Integer lectureId, Integer grade);
    GradeEntity updateGrade(Integer id, Integer grade);
    void deleteGrade(Integer id);
}
