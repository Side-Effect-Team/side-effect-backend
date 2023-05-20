package sideeffect.project.common.exception;

import lombok.Getter;
import sideeffect.project.domain.user.ProviderType;

@Getter
public class JoinException extends RuntimeException{

    private String email;
    private String imgUrl;
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

    public JoinException(String email, String imgUrl, ProviderType providerType) {
        super();
        this.email = email;
        this.imgUrl = imgUrl;
        this.providerType = providerType;
    }
}
