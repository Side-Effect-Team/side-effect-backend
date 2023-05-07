package sideeffect.project.common.security;

import org.springframework.security.test.context.support.WithSecurityContext;
import sideeffect.project.domain.user.UserRoleType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomUserSecurityContextFactory.class)
public @interface WithCustomUser {

    long id() default 1L;
    String email() default "1111@gmail.com";
    String password() default "1234";
    String nickname() default "ABC";
    UserRoleType role() default UserRoleType.ROLE_USER;
}
