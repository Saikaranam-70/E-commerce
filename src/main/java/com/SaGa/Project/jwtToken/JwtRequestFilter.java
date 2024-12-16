package com.SaGa.Project.jwtToken;

import com.SaGa.Project.model.User;
import com.SaGa.Project.responce.TokenResponce;
import com.SaGa.Project.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if(authorizationHeader!= null ) {
            jwt = authorizationHeader;
            try {
                username = jwtUtil.extractEmail(jwt);
            } catch (ExpiredJwtException e) {
                handleInvalidTokenResponse(response, "Token has expired");
                return;
            } catch (Exception e) {
                handleInvalidTokenResponse(response, "Invalid token");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            Optional<User> userOptional = userService.findUserByEmail(username);
            if(userOptional.isPresent()) {
                User user = userOptional.get();

                try {


                    if (jwtUtil.validToken(jwt, user.getEmail())) {
                        List<String> roles = jwtUtil.extractRoles(jwt);
                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList();
                        String userId = jwtUtil.extractUserId(jwt);

                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities()
                        );

                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        handleInvalidToken(response, "Invalid JWT token");
                        return;
                    }
                }catch (Exception e){
                    handleInvalidToken(response, "Expired or Invalid Token");
                    return;
                }
            }
            else {
                handleInvalidToken(response, "User not found for Email :"+ username);
            }
        }
        filterChain.doFilter(request, response);
    }



    private void handleInvalidToken(HttpServletResponse response, String s) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + s + "\"} ");
    }
    private void handleInvalidTokenResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        TokenResponce tokenResponce = new TokenResponce(null, null, message);
        response.getWriter().write("{\"error\": \"" + tokenResponce.getMessage() + "\"}");
    }
}
