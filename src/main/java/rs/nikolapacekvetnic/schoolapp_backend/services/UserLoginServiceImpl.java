package rs.nikolapacekvetnic.schoolapp_backend.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.UserLoginDto;
import rs.nikolapacekvetnic.schoolapp_backend.services.interfaces.UserLoginService;
import rs.nikolapacekvetnic.schoolapp_backend.utils.Encryption;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.UserRepository;

@Service
public class UserLoginServiceImpl implements UserLoginService {
	
	@Autowired
	private UserRepository userRepository;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Value("${spring.security.secret-key}")
	private String secretKey;

	@Value("${spring.security.token-duration}")
	private Integer duration;

	@Override
	public boolean isAuthorizedAs(EUserRole role) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!(authentication instanceof AnonymousAuthenticationToken)) {
		    
		    UserEntity currentUser = userRepository.findByUsername(authentication.getName()).get();
		    return currentUser.getRole() == role;
		}
		
		return false;
	}

	@Override
	public Optional<UserLoginDto> getUserLoginDto(String username, String password) {
		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();

		for (UserEntity userEntity : users) {
			if (userEntity.getUsername().equals(username) && Encryption.validatePassword(password, userEntity.getPassword())) {
				String token = getJwtToken(userEntity);

				UserLoginDto userLoginDTO = new UserLoginDto();
				userLoginDTO.setUsername(username);
				userLoginDTO.setToken(token);

				logger.info(userLoginDTO.getUsername() + " : logged in.");

				return Optional.of(userLoginDTO);
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional<String> getLoggedInUsername() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUsername = null;
		
		if (!(authentication instanceof AnonymousAuthenticationToken))
			loggedInUsername = authentication.getName();
		
		return Optional.ofNullable(loggedInUsername);
	}

	@Override
	public String getJwtToken(UserEntity user) {
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole().name());

		String token = Jwts.builder().setId("softtekJWT").setSubject(user.getUsername())
				.claim("authorities",
						grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 600000))
				.signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();

		logger.info(user + " : JWTToken granted.");

		return token;
	}
}
