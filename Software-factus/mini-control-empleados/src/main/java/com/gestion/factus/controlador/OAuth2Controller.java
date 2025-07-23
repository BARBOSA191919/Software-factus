package com.gestion.factus.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OAuth2Controller {
    

    @GetMapping("/oauth2/success")
    public String oauth2Success(RedirectAttributes redirectAttributes) {
        return "redirect:/dashboard";
    }


}