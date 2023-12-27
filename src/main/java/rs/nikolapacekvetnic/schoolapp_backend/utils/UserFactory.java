package rs.nikolapacekvetnic.schoolapp_backend.utils;

import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.ParentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.StudentRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.TeacherRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.UserRegisterDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.AdminEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.ParentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.StudentEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.TeacherEntity;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;

public class UserFactory {

	
	public static UserEntity createUser(UserRegisterDto userDTO) {
		
		if (userDTO.getRole() == EUserRole.ADMIN) {
			
			AdminEntity admin = new AdminEntity();
			
			admin.setUsername(userDTO.getUsername());
			admin.setPassword(userDTO.getPassword());
			admin.setRole(userDTO.getRole());
			
			return admin;
			
		} else if (userDTO.getRole() == EUserRole.TEACHER) {
			
			TeacherRegisterDto teacherDTO = (TeacherRegisterDto) userDTO;
			TeacherEntity teacher = new TeacherEntity();
			
			teacher.setFirstName(teacherDTO.getFirstName());
			teacher.setLastName(teacherDTO.getLastName());
			teacher.setEmail(teacherDTO.getEmail());
			teacher.setUsername(teacherDTO.getUsername());
			teacher.setPassword(Encryption.getPassEncoded(teacherDTO.getPassword()));
			teacher.setRole(teacherDTO.getRole());
			
			return teacher;
			
		} else if (userDTO.getRole() == EUserRole.PARENT) {
			
			ParentRegisterDto parentDTO = (ParentRegisterDto) userDTO;
			ParentEntity parent = new ParentEntity();
			
			parent.setFirstName(parentDTO.getFirstName());
			parent.setLastName(parentDTO.getLastName());
			parent.setEmail(parentDTO.getEmail());
			parent.setUsername(parentDTO.getUsername());
			parent.setPassword(Encryption.getPassEncoded(parentDTO.getPassword()));
			parent.setRole(parentDTO.getRole());
			
			return parent;
			
		} else {
			
			StudentRegisterDto studentDTO = (StudentRegisterDto) userDTO;
			StudentEntity student = new StudentEntity();
			
			student.setFirstName(studentDTO.getFirstName());
			student.setLastName(studentDTO.getLastName());
			student.setJmbg(studentDTO.getJmbg());
			student.setUsername(studentDTO.getUsername());
			student.setPassword(Encryption.getPassEncoded(studentDTO.getPassword()));
			student.setRole(studentDTO.getRole());
			
			return student;
		}
	}
}
