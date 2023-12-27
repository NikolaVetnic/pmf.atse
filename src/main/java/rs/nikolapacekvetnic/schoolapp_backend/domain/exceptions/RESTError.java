package rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RESTError {

	private Integer code;
	private String message;
}
