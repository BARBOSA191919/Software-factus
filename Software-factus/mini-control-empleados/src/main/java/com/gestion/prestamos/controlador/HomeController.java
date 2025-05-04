package com.gestion.prestamos.controlador;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/login")
    public String login() {
        return "login"; // Devuelve la plantilla login.html
    }


}
