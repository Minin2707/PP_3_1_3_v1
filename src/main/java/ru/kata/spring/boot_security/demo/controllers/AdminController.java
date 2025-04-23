package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.customExeption.BusinessValidationExeption;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.security.Principal;
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
    public String getAllUsers(Model model, Principal principal) {
        User user = userService.findByUserName(principal.getName());
        model.addAttribute("currentUser", user);
        List<User> listUsers = userService.getAllUsers();
        model.addAttribute("allUsers", listUsers);
        model.addAttribute("allRoles", roleService.getAllRoles());
        if(!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "admin-page";
    }


    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") @Valid User user,
                                   BindingResult bindingResult,
                                   Model model, Authentication authentication) {

        if (bindingResult.hasErrors()) {
            prepareModelWithErrorData(model,authentication,user);
            return "admin-page";
        }
        try{
            userService.saveUser(user);
            return "redirect:/admin";
        }catch (BusinessValidationExeption ex){
            bindingResult.rejectValue(ex.getField(), "error.user", ex.getMessage());
            prepareModelWithErrorData(model,authentication,user);
            return "admin-page";
        }
    }


    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             Model model, Authentication authentication) {

        if (bindingResult.hasErrors()) {
            prepareModelWithErrorData(model,authentication,user);
            return "admin-page";
        }

        try {
            userService.updateUser(user);
            return "redirect:/admin";
        } catch (BusinessValidationExeption ex) {
            bindingResult.rejectValue(ex.getField(), "", ex.getMessage());
            prepareModelWithErrorData(model,authentication,user);
            return "admin-page";
        }
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }




    private void prepareModelWithErrorData(Model model, Authentication authentication, User user) {
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("editMode", true);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", user);
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("openEditModalId", user.getId());
        model.addAttribute("activeTab", "newUser");
    }

}



