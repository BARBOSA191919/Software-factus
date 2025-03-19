package com.gestion.prestamos;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Override
	@Bean
	protected UserDetailsService userDetailsService() {
		UserDetails admin = User
				.withUsername("admin")
				.password(passwordEncoder().encode("admin"))
				.roles("ADMIN")
				.build();

		UserDetails contador = User
				.withUsername("cobrador1")
				.password(passwordEncoder().encode("cobrador1"))
				.roles("COBRADOR1")
				.build();

		UserDetails analista = User
				.withUsername("cobrador2")
				.password(passwordEncoder().encode("cobrador2"))
				.roles("COBRADOR2")
				.build();

		return new InMemoryUserDetailsManager(admin, contador, analista);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().sameOrigin(); // Permitir iframes del mismo origen
		http
				.authorizeRequests()
				.antMatchers("/", "/login").permitAll() // Permite acceso público al login
				.antMatchers("/api/productos/**").hasAnyRole("ADMIN", "COBRADOR1", "COBRADOR2") // Acceso a productos
				.antMatchers("/api/clientes/**").hasRole("ADMIN") // Solo ADMIN puede acceder a clientes
				.antMatchers("/facturas/**").hasAnyRole("ADMIN", "COBRADOR1", "COBRADOR2") // Permite acceso a las vistas de facturas
				.antMatchers("/api/facturas/**").hasAnyRole("ADMIN", "COBRADOR1", "COBRADOR2") // Acceso a facturas
				.anyRequest().authenticated() // Cualquier otra ruta requiere autenticación
				.and()
				.formLogin()
				.loginPage("/login") // Página de login personalizada
				.permitAll()
				.and()
				.logout()
				.permitAll()
				.and()
				.csrf().disable(); // Desactiva CSRF para simplificar (no recomendado en producción)
	}
}