package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.customExeption.BusinessValidationExeption;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }


    @Override
    @Transactional(readOnly = true)
    public User findByUserName(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        if (user.getId() != null && userRepository.existsById(user.getId())) {
            throw new EntityExistsException("User exists");
        }

        if(userRepository.findByEmail(user.getEmail()) != null) {
            throw new BusinessValidationExeption("email","Такой e-mail уже зарегистрирован");
        }

        if(user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BusinessValidationExeption("password","Пароль обязателен");
        }else if(user.getPassword().length() < 5) {
            throw new BusinessValidationExeption("password","Пароль должен быть не менее 5 символов");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!existingUser.getEmail().equals(user.getEmail())) {
            User userWithSameEmail = userRepository.findByEmail(user.getEmail());
            if (userWithSameEmail != null && !userWithSameEmail.getId().equals(user.getId())) {
                throw new BusinessValidationExeption("email", "Такой e-mail уже зарегистрирован");
            }
        }

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            if (user.getPassword().length() < 5) {
                throw new BusinessValidationExeption("password", "Пароль должен быть не менее 5 символов");
            }
        }

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAge(user.getAge());
        existingUser.setEmail(user.getEmail());
        existingUser.setRoles(user.getRoles());

        String rawPassword = user.getPassword();
        if (rawPassword != null && !rawPassword.isBlank()) {
            if (!rawPassword.matches("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}")) {
                existingUser.setPassword(passwordEncoder.encode(rawPassword));
            } else {
                existingUser.setPassword(rawPassword);
            }
        }
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
    }

}
