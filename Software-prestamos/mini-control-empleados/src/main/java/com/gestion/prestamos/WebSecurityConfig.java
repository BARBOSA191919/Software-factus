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

		return new InMemoryUserDetailsManager(admin);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().sameOrigin();
		http
				.authorizeRequests()
				.antMatchers("/", "/login").permitAll()
				.antMatchers("/api/productos/**").authenticated()
				.antMatchers("/api/clientes/**").authenticated()
				.antMatchers("/facturas/**").authenticated()
				.antMatchers("/api/facturas/**").authenticated()
				.antMatchers("/id/*").authenticated()
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/api/facturas/crear", true)
				.permitAll()
				.and()
				.oauth2Login()
				.loginPage("/login")
				.defaultSuccessUrl("/api/facturas/crear", true)
				.and()
				.logout()
				.logoutSuccessUrl("/login?logout")
				.permitAll()
				.and()
				.csrf().disable();
	}
}