package ru.kata.spring.boot_security.demo.services;


import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> getAllUsers();

    User findUserById(Long id);
    User findByUserName(String username);

    void saveUser(User user);

    void deleteById(Long id);
}
