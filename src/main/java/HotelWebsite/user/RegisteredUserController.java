package HotelWebsite.user;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.util.Assert;
import org.springframework.ui.Model;


import jakarta.validation.Valid;

@Controller
public class RegisteredUserController {

    private final RegisteredUserManagement registeredUserManagement;

    RegisteredUserController(RegisteredUserManagement registeredUserManagement){
        Assert.notNull(registeredUserManagement, "RegisteredUserController must not be null!");

        this.registeredUserManagement = registeredUserManagement;
    }
    
    @PostMapping("/registration")
    String registerNew(@Valid RegistrationForm form, BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
			return "registration";
		}else{
			registeredUserManagement.createRegisteredUser(form);
			return "redirect:/custom_login";
		}
    }
 
    @GetMapping("/custom_login")
	public String customLoginForm() {
        return "custom_login";
	}
    @GetMapping("/registration")
    String register(Model model, RegistrationForm form) {
        return "registration";
    }
    @GetMapping("/forgot_password")
    public String forgot_password(){
        return "forgot_password";
    }
    @GetMapping("/profile")
    public String profile(){
        return "profile";
    }
}