package sideeffect.project.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.JoinException;
import sideeffect.project.domain.user.User;
import sideeffect.project.repository.UserRepository;
import sideeffect.project.security.UserDetailsImpl;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("UserDetailsServiceImpl 진입");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new JoinException(email));

        return UserDetailsImpl.of(user);
    }
}
