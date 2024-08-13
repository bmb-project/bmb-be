package BookmyBook.bmb.Controller;

import BookmyBook.bmb.domain.Address;
import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new UserForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid UserForm form, BindingResult result){

        if(result.hasErrors()){
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        User user = new User();
        user.setNickname(form.getName());
       // user.setAddress(address);

        userService.join(user);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model){
        List<User> users = userService.findUsers();
        model.addAttribute("members", users);
        return "/members/memberList";
    }
}