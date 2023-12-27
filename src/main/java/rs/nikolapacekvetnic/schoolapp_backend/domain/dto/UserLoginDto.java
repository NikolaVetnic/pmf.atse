package rs.nikolapacekvetnic.schoolapp_backend.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class UserLoginDto {

	private String username;
	private String token;
}
