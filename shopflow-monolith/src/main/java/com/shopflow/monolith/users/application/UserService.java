package com.shopflow.monolith.users.application;

import com.shopflow.monolith.shared.NotFoundException;
import com.shopflow.monolith.users.domain.User;
import com.shopflow.monolith.users.domain.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public User create(String name, String email) {
        return users.save(new User(name, email));
    }

    public User get(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }

    public List<User> list() {
        return users.findAll();
    }
}
