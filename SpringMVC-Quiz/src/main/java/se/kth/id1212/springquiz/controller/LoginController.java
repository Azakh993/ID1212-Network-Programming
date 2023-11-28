package se.kth.id1212.springquiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se.kth.id1212.springquiz.model.User;
import se.kth.id1212.springquiz.dao.UserDAO;
import se.kth.id1212.springquiz.util.UnauthorizedException;

@Controller
@RequestMapping("/login")
@SessionAttributes("USER_ID")
public class LoginController {
    private final UserDAO userDAO;

    @Autowired
    public LoginController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping
    public String showLoginForm() {
        return "login";
    }

    @PostMapping
    public String processLoginForm(@RequestParam("username") String username,
                                   @RequestParam("password") String password, Model model) {

        String userID = login(username, password);

        if (userID == null) {
            throw new UnauthorizedException("Invalid username or password");
        } else {
            model.addAttribute("USER_ID", userID);
            return "redirect:/dashboard";
        }
    }

    private String login(String username, String password) {
        User user = userDAO.getUser(username, password);
        return user == null ? null : user.getId().toString();
    }
}
