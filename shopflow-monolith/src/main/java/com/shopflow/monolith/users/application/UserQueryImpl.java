package com.shopflow.monolith.users.application;

import com.shopflow.monolith.users.contract.UserQuery;
import com.shopflow.monolith.users.domain.UserRepository;
import org.springframework.stereotype.Component;

@Component
class UserQueryImpl implements UserQuery {

    private final UserRepository users;

    UserQueryImpl(UserRepository users) {
        this.users = users;
    }

    @Override
    public boolean exists(Long userId) {
        return users.existsById(userId);
    }
}
