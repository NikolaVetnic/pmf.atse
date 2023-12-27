package rs.nikolapacekvetnic.schoolapp_backend.domain.exceptions;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
