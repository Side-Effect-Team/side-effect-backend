package sideeffect.project.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import sideeffect.project.domain.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class UserDetailsImpl implements UserDetails, OAuth2User {

    private User user;
    private Map<String, Object> attributes;

    public User getUser() {
        return user;
    }

    public UserDetailsImpl(User user){
        this.user = user;
    }

    public UserDetailsImpl(User user, Map<String, Object> attributes){
        this.user = user;
        this.attributes = attributes;
    }

    public static UserDetailsImpl of(User user){
        return new UserDetailsImpl(user);
    }
    public static UserDetailsImpl of(User user, Map<String, Object> attributes){
        return new UserDetailsImpl(user, attributes);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(user.getUserRoleType().name()));
        return auth;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return attributes.get("email").toString();
    }
}
