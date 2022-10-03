package com.tlcn.demo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tlcn.demo.model.Users;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class Utils {
    public static Long getIdCurrentUser(){
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }
    public static String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }
    public static String jwtProvide(Users user, HttpServletRequest request){
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        return JWT.create()
                .withSubject(user.getId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis()+10*60*1000*6*24*15))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("role",user.getRole())
                .sign(algorithm);
    }
}
