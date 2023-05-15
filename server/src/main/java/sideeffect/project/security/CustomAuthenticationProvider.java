package sideeffect.project.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("CustomAuthenticationProvider 진입");
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);

        if(userDetails.getPassword()!="" && userDetails.getPassword()!=null){
            if(!bCryptPasswordEncoder.matches(password, userDetails.getPassword())){
                throw new AuthException(ErrorCode.USER_UNAUTHENTICATION);
            }
        }

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication==UsernamePasswordAuthenticationToken.class;
    }
}
