package sideeffect.project.common.exception;

public class JoinException extends RuntimeException{

    private final String email;
    public JoinException(String email) {
        super();
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
