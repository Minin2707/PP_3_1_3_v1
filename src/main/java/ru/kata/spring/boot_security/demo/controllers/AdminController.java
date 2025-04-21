package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;


    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("allUsers", users);
        return "all-user";
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("actionUrl", "/admin/save");
        return "add-user";
    }


    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") @Valid User user,
                                   BindingResult bindingResult,
                                   Model model) {
        boolean isNew = user.getId() == null;

        User existingUser = userService.findByUserName(user.getEmail());

        if (existingUser != null && (isNew || !existingUser.getId().equals(user.getId()))) {
            bindingResult.rejectValue("email", "error.user", "Такой e-mail уже зарегистрирован");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            bindingResult.rejectValue("password", "error.user", "Пароль обязателен");
        } else if (user.getPassword().length() < 5) {
            bindingResult.rejectValue("password", "error.user", "Пароль должен быть не менее 5 символов");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "add-user";
        }
        userService.saveUser(user);
        return "redirect:/admin";
    }



    @GetMapping("/updateUser")
    public String updateUserForm(@RequestParam Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("actionUrl", "/admin/update");
        return "add-user";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             Model model) {
        User existingUser = userService.findByUserName(user.getEmail());

        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            bindingResult.rejectValue("email", "error.user", "Такой e-mail уже зарегистрирован");
        }

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            if (user.getPassword().length() < 5) {
                bindingResult.rejectValue("password", "error.user", "пароль должен быть не менее 5 символов");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "add-user";
        }

        userService.updateUser(user);

        return "redirect:/admin";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}



