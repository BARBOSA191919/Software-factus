package com.gestion.factus.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2Controller {

    @GetMapping("/oauth2/success")
    public String oauth2Success() {
        return "oauth2-success"; // Devuelve la plantilla oauth2-success.html
    }
}