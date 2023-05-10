package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.IllegalStateException;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.stack.StackType;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.domain.user.UserStack;
import sideeffect.project.dto.user.UserRequest;
import sideeffect.project.dto.user.UserResponse;
import sideeffect.project.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PositionService positionService;
    private final StackService stackService;
    private final BCryptPasswordEncoder encoder;

    public Long join(UserRequest request){

        validateDuplicateUser(request.getEmail());
        User user = request.toUser();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setUserRoleType(UserRoleType.ROLE_USER);
        user.updateUserStack(getUserStacks(user, request.getStacks()));
        return userRepository.save(user).getId();
    }

    private List<UserStack> getUserStacks(User user, List<StackType> stacks) {
        List<UserStack> userStacks = Collections.emptyList();

        if(stacks!=null && !stacks.isEmpty()){
            userStacks = stacks.stream()
                    .map(userStackRequest -> toUserStack(user, userStackRequest))
                    .collect(Collectors.toList());
        }
        return userStacks;
    }

    private UserStack toUserStack(User user, StackType userStackRequest) {
        Stack stack = stackService.findByStackType(userStackRequest);
        return UserStack.builder()
                .user(user)
                .stack(stack)
                .build();
    }

    public UserResponse findOne(User user, Long id){
        if(user.getId()!=id) throw new AuthException(ErrorCode.USER_UNAUTHORIZED);
        return UserResponse.of(user);
    }
    public void update(User user, Long id, UserRequest request){
        if(user.getId()!=id) throw new AuthException(ErrorCode.USER_UNAUTHORIZED);
        user.update(request.toUser());
    }

    public void delete(User user, Long id){
        if(user.getId()!=id) throw new AuthException(ErrorCode.USER_UNAUTHORIZED);
        userRepository.deleteById(id);
    }

    public void validateDuplicateUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new IllegalStateException(ErrorCode.USER_ALREADY_EXIST);
        });
    }

}
