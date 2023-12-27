package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.*;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ParentServiceImpl parentService;

    @Test
    public void whenGetParent_thenReturnParent() {
        ParentEntity expectedParent = new ParentEntity();
        expectedParent.setUsername("parentUsername");
        expectedParent.setRole(EUserRole.PARENT);

        when(userRepository.findByUsername(expectedParent.getUsername())).thenReturn(Optional.of(expectedParent));

        ParentEntity actualParent = parentService.getParent(expectedParent.getUsername());

        assertEquals(expectedParent, actualParent);
        verify(userRepository).findByUsername(expectedParent.getUsername());
    }

    @Test
    public void whenGetGradeCards_thenReturnGradeCardList() {
        ParentEntity parent = new ParentEntity();
        parent.setUsername("parentUsername");
        parent.setRole(EUserRole.PARENT);

        Set<GradeCardEntity> gradeCards = new HashSet<>();

        Set<StudentEntity> students = new HashSet<>();
        students.add(new StudentEntity().setGradeCards(gradeCards));
        parent.setStudents(students);

        when(userRepository.findByUsername(parent.getUsername())).thenReturn(Optional.of(parent));

        List<Set<GradeCardEntity>> actualGradeCards = parentService.getGradeCards(parent.getUsername());

        assertNotNull(actualGradeCards);
        assertTrue(actualGradeCards.contains(gradeCards));
        verify(userRepository).findByUsername(parent.getUsername());
    }

    @Test
    public void whenGetGradeCardsForSubject_thenReturnFilteredGradeCardList() {
        String username = "parentUsername";
        Integer subjectId = 1;
        ParentEntity parent = new ParentEntity();
        parent.setUsername(username);
        parent.setRole(EUserRole.PARENT);
        GradeCardEntity gradeCard = new GradeCardEntity();
        gradeCard.setLecture(new LectureEntity().setSubject(new SubjectEntity().setId(subjectId)));

        Set<GradeCardEntity> gradeCards = new HashSet<>();
        gradeCards.add(gradeCard);
        Set<StudentEntity> students = new HashSet<>();
        students.add(new StudentEntity().setGradeCards(gradeCards));
        parent.setStudents(students);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(parent));

        List<GradeCardEntity> actualGradeCards = parentService.getGradeCardsForSubject(username, subjectId);

        assertNotNull(actualGradeCards);
        assertTrue(actualGradeCards.contains(gradeCard));
        verify(userRepository).findByUsername(username);
    }
}

