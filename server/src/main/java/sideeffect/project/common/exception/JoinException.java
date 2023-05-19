package sideeffect.project.common.exception;

import sideeffect.project.domain.user.ProviderType;

public class JoinException extends RuntimeException{

    private String email;
    private ProviderType providerType;

    public JoinException(String email) {
        super();
        this.email = email;
    }

    public JoinException(String email, ProviderType providerType) {
        super();
        this.email = email;
        this.providerType = providerType;
    }

    public String getEmail() {
        return email;
    }

    public ProviderType getProviderType() {
        return providerType;
    }
}
