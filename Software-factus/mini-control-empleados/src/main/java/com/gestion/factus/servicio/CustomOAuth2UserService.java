package com.gestion.factus.servicio;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 🔍 DEBUG: imprimir atributos del usuario
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println("🔐 Usuario autenticado con Google:");
        attributes.forEach((key, value) -> System.out.println(key + ": " + value));

        // Aquí puedes extraer datos si los necesitas, como:
        String email = (String) attributes.get("email");
        String nombre = (String) attributes.get("name");
        System.out.println("✅ Email: " + email + ", Nombre: " + nombre);

        return oAuth2User;
    }
}
