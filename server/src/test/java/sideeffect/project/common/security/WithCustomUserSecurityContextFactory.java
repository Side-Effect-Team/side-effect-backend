package sideeffect.project.common.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import sideeffect.project.domain.user.User;
import sideeffect.project.security.UserDetailsImpl;

public class WithCustomUserSecurityContextFactory  implements WithSecurityContextFactory<WithCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = User.builder()
                .id(annotation.id())
                .email(annotation.email())
                .password(annotation.password())
                .nickname(annotation.nickname())
                .userRoleType(annotation.role())
                .build();

        UserDetailsImpl userDetails = UserDetailsImpl.of(user);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        context.setAuthentication(token);

        return context;
    }
}
