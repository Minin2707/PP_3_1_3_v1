package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.customExeption.BusinessValidationExeption;
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

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "add-user";
        }
        try{
            userService.saveUser(user);
            return "redirect:/admin";
        }catch (BusinessValidationExeption ex){
            bindingResult.rejectValue(ex.getField(), "", ex.getMessage());
            return "add-user";
        }

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
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "add-user";
        }

        try {
            userService.updateUser(user);
            return "redirect:/admin";
        } catch (BusinessValidationExeption ex) {
            bindingResult.rejectValue(ex.getField(), "", ex.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "add-user";
        }
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}



