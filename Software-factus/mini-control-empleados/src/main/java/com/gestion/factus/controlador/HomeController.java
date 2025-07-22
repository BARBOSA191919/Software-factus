package com.gestion.factus.controlador;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            // Inicio de sesión con Google
            var oauth2 = (OAuth2AuthenticationToken) authentication;
            var attributes = oauth2.getPrincipal().getAttributes();

            String name = (String) attributes.get("name");
            String email = (String) attributes.get("email");
            String picture = (String) attributes.get("picture");

            model.addAttribute("userName", name);
            model.addAttribute("userEmail", email);
            model.addAttribute("userPicture", picture);
        } else if (authentication != null && authentication.isAuthenticated()) {
            // Inicio de sesión normal con usuario y contraseña
            User user = (User) authentication.getPrincipal();
            String username = user.getUsername();

            model.addAttribute("userName", username);
            model.addAttribute("userEmail", "UserFactus@gmail.com");
            model.addAttribute("userPicture", "https://cdn-icons-png.flaticon.com/128/4526/4526169.png"); // imagen por defecto
        }

        return "Facturas/Dashboard";
    }
}
