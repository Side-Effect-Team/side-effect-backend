package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.dto.user.TokenInfo;
import sideeffect.project.dto.user.UserJoinRequest;
import sideeffect.project.repository.UrlRepository;
import sideeffect.project.repository.UserRepository;
import sideeffect.project.security.JwtTokenProvider;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public void join(UserJoinRequest request){

        //중복체크
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new RuntimeException(request.getEmail() + "는 이미 있습니다");
        });
        User user = request.toUser();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setUserRoleType(UserRoleType.ROLE_USER);
        userRepository.save(user);
    }
}
