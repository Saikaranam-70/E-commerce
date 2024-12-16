package com.SaGa.Project.config;

import com.SaGa.Project.jwtToken.JwtRequestFilter;
import com.SaGa.Project.model.User;
import com.SaGa.Project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;


    public WebSecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    private static final String[] WHITE_LIST_URLS = {
            "/user/register",
            "/user/admin/register",
            "/authentication/login",
            "/user/confirm",
            "/user/forgot-password",
            "/user/reset-password",
//            "/user/getUser/{userId}",
//            "/cartItem/add",
//            "/cartItem/get/**",
//            "/cartItem/getAll",
//            "/cartItem/delete/**",
//            "/cartItem/update/**",
//            "/cart/add/**",
//            "/cart/**",
//            "/cart/remove/**",
//            "/cart/clear/**",
//            "/address/**",
//            "/address/getAddress/**",
//            "/orders/placeOrder",
//            "/rating/**",
            "/rating/average-Rating/**",
//            "/review/**",
//            "/product/getProductById/**",
//            "/product/getAllProducts",
//            "/product/getProductByCategory/**",
//            "/product/search",

    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(corf -> corf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/product/getProductById/**", "/product/getAllProducts", "/product/getProductByCategory/**", "/product/search", "/product/categories", "/product/top-discounts", "/product/images/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/confirm").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/reset-password").permitAll()
                        .requestMatchers(WHITE_LIST_URLS).permitAll()
                        .requestMatchers("/product/add").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(username -> {
            Optional<User> user = userService.findUserByEmail(username);
            if(user.isEmpty()){
                throw new UsernameNotFoundException("User not found with Email :"+ username);
            }
            return (UserDetails) user.get();
        });


        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

}
