package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.SubjectEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.SubjectRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.SubjectRepository;
import rs.nikolapacekvetnic.schoolapp_backend.utils.SubjectCustomValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubjectServiceImplTest {

    @Mock private SubjectRepository subjectRepository;
    @Mock private SubjectCustomValidator subjectValidator;
    @Mock private UserLoginServiceImpl userLoginService;
    @Mock private BindingResult bindingResult;
    @InjectMocks private SubjectServiceImpl subjectService;


    @Test
    public void whenAddNewSubject_thenReturnSubject() {
        SubjectRegisterDto subjectDTO = new SubjectRegisterDto();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);

        SubjectEntity subject = subjectService.addNewSubject(subjectDTO, bindingResult);

        assertNotNull(subject);
        assertEquals(subjectDTO.getName(), subject.getName());
        verify(subjectRepository).save(any(SubjectEntity.class));
    }

    @Test
    public void whenUpdateSubject_thenSubjectUpdated() {
        SubjectRegisterDto subjectDTO = new SubjectRegisterDto();

        SubjectEntity existingSubject = new SubjectEntity();
        existingSubject.setId(1);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        when(subjectRepository.findById(existingSubject.getId())).thenReturn(Optional.of(existingSubject));

        SubjectEntity updatedSubject = subjectService.updateSubject(existingSubject.getId(), subjectDTO, bindingResult);

        assertNotNull(updatedSubject);
        assertEquals(subjectDTO.getName(), updatedSubject.getName());
        verify(subjectRepository).save(updatedSubject);
    }

    @Test
    public void whenDeleteSubject_thenSubjectDeleted() {
        Integer id = 1;
        SubjectEntity subject = new SubjectEntity();

        when(userLoginService.isAuthorizedAs(EUserRole.ADMIN)).thenReturn(true);
        when(subjectRepository.findById(id)).thenReturn(Optional.of(subject));

        subjectService.deleteSubject(id);

        verify(subjectRepository).delete(subject);
    }
}
