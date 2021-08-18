package com.example.codefellowship;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@Controller
public class ApplicationUserController {

    @Autowired
    ApplicationUserRepository applicationUserRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/signup")
    public String getSignUpPage() {
        return "signup.html";
    }

    @GetMapping("/login")
    public String getSignInPage() {
        return "login.html";
    }

    @PostMapping("/signup")
    public RedirectView signUp(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password, @RequestParam(value = "firstName") String firstName,
                               @RequestParam(value = "lastName") String lastName, @RequestParam(value = "dateOfBirth") String dateOfBirth, @RequestParam(value = "bio") String bio) {
        ApplicationUser newUser = new ApplicationUser(username, bCryptPasswordEncoder.encode(password), firstName, lastName, dateOfBirth, bio);
        applicationUserRepository.save(newUser);
        return new RedirectView("/login");
    }


    @GetMapping("/users/{id}")
    public String albumContent(@PathVariable("id") Integer id, Model m) {
        ApplicationUser user = applicationUserRepository.findById(id).get();
        m.addAttribute("user", user);
        return "user.html";
    }

    @PostMapping("/follow/{id}")
    public RedirectView followers(Principal p, @PathVariable(value = "id") Integer id) {
        ApplicationUser user = applicationUserRepository.findById(id).get();
        ApplicationUser theFollower = applicationUserRepository.findByUsername(p.getName());
        theFollower.followUser(user);
        applicationUserRepository.save(theFollower);
        return new RedirectView("/users/" + id);
    }

    @GetMapping("/feed")
    public String showFollowedUsersPosts (Principal p,Model m){
        ApplicationUser user = applicationUserRepository.findByUsername(p.getName());
        m.addAttribute("users",user.getFollower());
        return "feed.html";
    }

}