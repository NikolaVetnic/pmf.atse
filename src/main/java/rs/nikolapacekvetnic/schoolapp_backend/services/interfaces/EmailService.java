package rs.nikolapacekvetnic.schoolapp_backend.services.interfaces;

import rs.nikolapacekvetnic.schoolapp_backend.domain.entities.EmailObject;

public interface EmailService {

	void sendSimpleMessage(EmailObject object);
	void sendTemplateMessage(EmailObject object) throws Exception;
	void sendMessageWithAttachment(EmailObject object, String pathToAttachment) throws Exception;
}
