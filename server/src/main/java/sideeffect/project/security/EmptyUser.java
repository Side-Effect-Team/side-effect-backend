package sideeffect.project.security;

import sideeffect.project.domain.user.User;

public class EmptyUser extends User {
    public EmptyUser() {
        super(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}
