package rs.nikolapacekvetnic.schoolapp_backend.domain.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.ESemester;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class LectureRegisterDto {
	
	@NotNull(message = "Year must be provided.")
	@Min(value = 1900, message = "Year must be greater than {value}.")
	@Max(value = 2100, message = "Year must be lesser than than {value}.")
	private Integer year;
	
	@Enumerated(EnumType.STRING)
	private ESemester semester;
}
