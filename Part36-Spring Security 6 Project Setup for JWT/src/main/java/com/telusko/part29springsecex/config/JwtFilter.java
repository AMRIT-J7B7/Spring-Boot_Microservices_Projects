package com.telusko.part29springsecex.config;

import java.io.IOException;


import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.telusko.part29springsecex.service.JWTService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.telusko.part29springsecex.service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{

	@Autowired
	private JWTService jWTService;
	
    @Autowired
    ApplicationContext context;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJOYXZpbiIsImlhdCI6MTczMTc1Mzg3OCwiZXhwIjoxNzMxNzUzOTg2fQ.1KwfPvO1OmbVgsJAUrcwXVQM5BrZBiNimoZ_W0-zszI
		String authHeader = request.getHeader("Authorization");
		String token =  null;
		String username = null;
		
		if(authHeader != null && authHeader.startsWith("Bearer ") ) {
			token = authHeader.substring(7);
			username = jWTService.extractUserName(token);
			
		}
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication()==null ) {
			//SecurityContextHolder - after the user is authenticated the object is stored in here. 
			//if it is not null then not authenticated as it should store something here and we can proceed to jwt authentication
			
			UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
			
			if(jWTService.validateToken(token, userDetails)){
				UsernamePasswordAuthenticationToken authtoken =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authtoken);
			}
			
		
			
		}
		filterChain.doFilter(request, response);
	}

}
