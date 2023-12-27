package rs.nikolapacekvetnic.schoolapp_backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EmailObject;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock private JavaMailSender emailSender;
    @InjectMocks private EmailServiceImpl emailService;

    @Test
    public void whenSendSimpleMessage_thenEmailIsSent() {
        EmailObject emailObject = new EmailObject("to@example.com", "Subject", "Text");
        emailService.sendSimpleMessage(emailObject);
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void whenSendTemplateMessage_thenEmailIsSent() throws Exception {
        MimeMessage mockMimeMessage = new MimeMessage((Session) null);
        when(emailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        EmailObject emailObject = new EmailObject("to@example.com", "Subject", "Text");
        emailService.sendTemplateMessage(emailObject);

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }


    @Test
    public void whenSendMessageWithAttachment_thenEmailIsSent() throws Exception {
        MimeMessage mockMimeMessage = new MimeMessage((Session) null);
        when(emailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        EmailObject emailObject = new EmailObject("to@example.com", "Subject", "Text");
        String pathToAttachment = "/path/to/attachment";

        emailService.sendMessageWithAttachment(emailObject, pathToAttachment);

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }
}

