package sideeffect.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sideeffect.project.domain.position.Position;
import sideeffect.project.domain.stack.Stack;
import sideeffect.project.domain.user.User;
import sideeffect.project.domain.user.UserPosition;
import sideeffect.project.domain.user.UserRoleType;
import sideeffect.project.domain.user.UserStack;
import sideeffect.project.dto.user.UserPositionRequest;
import sideeffect.project.dto.user.UserRequest;
import sideeffect.project.dto.user.UserResponse;
import sideeffect.project.dto.user.UserStackRequest;
import sideeffect.project.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
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

    public void join(UserRequest request){

        validateDuplicateUser(request.getEmail());
        User user = request.toUser();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setUserRoleType(UserRoleType.ROLE_USER);
        user.updateUserPosition(getUserPositions(user, request.getPositions()));
        user.updateUserStack(getUserStacks(user, request.getStacks()));
        userRepository.save(user);
    }

    private List<UserStack> getUserStacks(User user, List<UserStackRequest> stacks) {
        List<UserStack> userStacks = Collections.emptyList();

        if(stacks!=null && !stacks.isEmpty()){
            userStacks = stacks.stream()
                    .map(userStackRequest -> toUserStack(user, userStackRequest))
                    .collect(Collectors.toList());
        }
        return userStacks;
    }

    private UserStack toUserStack(User user, UserStackRequest userStackRequest) {
        Stack stack = stackService.findByStackType(userStackRequest.getStackType());
        return userStackRequest.toUserStack(user, stack);
    }

    private List<UserPosition> getUserPositions(User user, List<UserPositionRequest> positions) {
        List<UserPosition> userPositions = Collections.emptyList();

        if(positions!=null && !positions.isEmpty()){
            userPositions = positions.stream()
                    .map(userPositionRequest -> toUserPosition(user, userPositionRequest))
                    .collect(Collectors.toList());
        }

        return userPositions;
    }

    private UserPosition toUserPosition(User user, UserPositionRequest userPositionRequest) {
        Position position = positionService.findByPositionType(userPositionRequest.getPositionType());
        return userPositionRequest.toUserPosition(user, position);
    }

    public UserResponse findOne(User user, Long id){
        User finduser = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        validateOwner(user.getId(), finduser);
        return UserResponse.of(user);
    }
    public void update(User user, Long id, UserRequest request){
        User finduser = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        validateOwner(user.getId(), finduser);
        user.update(request.toUser());

    }

    public void delete(User user, Long id){
        User finduser = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        validateOwner(user.getId(), finduser);
        userRepository.deleteById(id);
    }

    public void validateDuplicateUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new RuntimeException(email + "는 이미 있습니다");
        });
    }

    private void validateOwner(Long id, User user) {
        if (!id.equals(user.getId())) {
            throw new IllegalArgumentException("접근하려는 계정의 주인이 아닙니다.");
        }
    }

}
