package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import rs.nikolapacekvetnic.schoolapp_backend.domain.dto.UserLoginDto;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EUserRole;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.UserEntity;
import rs.nikolapacekvetnic.schoolapp_backend.repositories.UserRepository;
import rs.nikolapacekvetnic.schoolapp_backend.utils.Encryption;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceImplTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserLoginServiceImpl userLoginService;

    @Test
    public void whenUserIsAuthorizedAsRole_thenReturnsTrue() {
        // mock authentication and security context
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("username");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // mock userRepository response
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(EUserRole.ADMIN);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(userEntity));

        assertTrue(userLoginService.isAuthorizedAs(EUserRole.ADMIN));
    }

    @Test
    public void whenValidCredentials_thenReturnsUserLoginDto() {
        // manually set the secret key
        ReflectionTestUtils.setField(userLoginService, "secretKey", "YourSecretKeyForTesting");

        // create and setup UserEntity
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("username");
        userEntity.setPassword(Encryption.getPassEncoded("password"));
        userEntity.setRole(EUserRole.STUDENT);

        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(userEntity);

        when(userRepository.findAll()).thenReturn(userEntities);

        Optional<UserLoginDto> result = userLoginService.getUserLoginDto("username", "password");

        assertTrue(result.isPresent());
        assertEquals("username", result.get().getUsername());
        assertNotNull(result.get().getToken());
    }

    @Test
    public void whenUserIsLoggedIn_thenReturnsUsername() {
        String expectedUsername = "testUser";
        Authentication authentication = new UsernamePasswordAuthenticationToken(expectedUsername, "password");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Optional<String> loggedInUsername = userLoginService.getLoggedInUsername();

        assertTrue(loggedInUsername.isPresent());
        assertEquals(expectedUsername, loggedInUsername.get());
    }

    @Test
    public void whenUserIsValid_thenReturnsJwtToken() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("username");
        userEntity.setRole(EUserRole.STUDENT);

        // manually set the secret key
        ReflectionTestUtils.setField(userLoginService, "secretKey", "YourSecretKeyForTesting");

        String token = userLoginService.getJwtToken(userEntity);

        assertNotNull(token);
    }
}
