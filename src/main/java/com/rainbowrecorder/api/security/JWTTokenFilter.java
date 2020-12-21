package com.rainbowrecorder.api.security;

import com.rainbowrecorder.api.repositories.UserRepository;
import com.rainbowrecorder.api.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JWTTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    private String header = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        getTokenString(request.getHeader(header)).ifPresent(token -> {
            jwtService.getIdFromToken(token).ifPresent(id -> {
                if(SecurityContextHolder.getContext().getAuthentication() == null) {
                    userRepository.findById(id).ifPresent(user -> {
                        String generated = jwtService.createToken(user).toString();
                        //TODO: needs an Authentication object
                        //SecurityContextHolder.getContext().setAuthentication(generated);
                    });
                }
            });
        });
    }

    private Optional<String> getTokenString(String header) {
        if (header == null) {
            return Optional.empty();
        } else {
            String[] split = header.split("");
            if (split.length < 2) {
                return Optional.empty();
            } else {
                return Optional.ofNullable(split[1]);
            }
        }
    }
}
