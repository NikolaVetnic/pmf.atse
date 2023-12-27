package rs.nikolapacekvetnic.schoolapp_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions.RESTError;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.LectureEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.LectureRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.*;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.GradeService;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.LectureService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/project/lectures")
public class LectureController {

	final GradeRepository gradeRepository;
	final GradeCardRepository gradeCardRepository;
	final LectureRepository lectureRepository;
	final StudentRepository studentRepository;
	final SubjectRepository subjectRepository;
	final TeacherRepository teacherRepository;

	private final LectureService lectureService;
	private final GradeService gradeService;

	public LectureController(GradeRepository gradeRepository, GradeCardRepository gradeCardRepository, LectureRepository lectureRepository, StudentRepository studentRepository, SubjectRepository subjectRepository, TeacherRepository teacherRepository, LectureService lectureService, GradeService gradeService) {
		this.gradeRepository = gradeRepository;
		this.gradeCardRepository = gradeCardRepository;
		this.lectureRepository = lectureRepository;
		this.studentRepository = studentRepository;
		this.subjectRepository = subjectRepository;
		this.teacherRepository = teacherRepository;
		this.lectureService = lectureService;
		this.gradeService = gradeService;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/register/{subjectId}/into/{teacherId}")
	public ResponseEntity<?> connectSubjectWithTeacher(@PathVariable Integer subjectId, @PathVariable Integer teacherId, @Valid @RequestBody LectureRegisterDto lectureDTO, BindingResult result) {
		try {
			LectureEntity lecture = lectureService.connectSubjectWithTeacher(subjectId, teacherId, lectureDTO, result);
			return new ResponseEntity<>(lecture, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/insert/{studentId}/into/{lectureId}")
	public ResponseEntity<?> connectStudentWithLecture(@PathVariable Integer studentId, @PathVariable Integer lectureId) {
		try {
			LectureEntity lecture = lectureService.connectStudentWithLecture(studentId, lectureId);
			return new ResponseEntity<>(lecture, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}

	@PutMapping("/grade/{studentId}/in/{lectureId}/with/{grade}")
	public ResponseEntity<?> gradeStudentInLecture(
			@PathVariable Integer studentId, @PathVariable Integer lectureId, @PathVariable Integer grade) {
		try {
			LectureEntity lecture = gradeService.gradeStudentInLecture(studentId, lectureId, grade);
			return new ResponseEntity<>(lecture, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		}
	}

	@PutMapping("/unregister/{subjectId}/into/{teacherId}")
	public ResponseEntity<?> disconnectSubjectWithTeacher(@PathVariable Integer subjectId, @PathVariable Integer teacherId) {
		try {
			LectureEntity lecture = lectureService.disconnectSubjectWithTeacher(subjectId, teacherId);
			return new ResponseEntity<>(lecture, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(new RESTError(e.getStatus().value(), e.getReason()), e.getStatus());
		} catch (Exception e) {
			return new ResponseEntity<>(new RESTError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error. Error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
