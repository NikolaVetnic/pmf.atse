package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SubjectEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SubjectRegisterDto;

public interface SubjectService {

    SubjectEntity addNewSubject(SubjectRegisterDto subjectDTO, BindingResult result);
    SubjectEntity updateSubject(Integer id, SubjectRegisterDto subjectDTO, BindingResult result);
    void deleteSubject(Integer id);
}
