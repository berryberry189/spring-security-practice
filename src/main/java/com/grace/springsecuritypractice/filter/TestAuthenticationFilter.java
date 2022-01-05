package com.grace.springsecuritypractice.filter;

import com.grace.springsecuritypractice.user.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 테스트 유저인 경우에는 어드민과 유저 권한을 모두 준다
 */
public class TestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public TestAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Authentication authentication = super.attemptAuthentication(request, response);
        User user = (User) authentication.getPrincipal();
        if(user.getUsername().startsWith("tester")){
            return new UsernamePasswordAuthenticationToken(
                    user, null,
                    Stream.of("ROLE_ADMIN", "ROLE_USER")
                    .map(authority -> (GrantedAuthority) () -> authority)
                    .collect(Collectors.toList())
            );
        }
        return authentication;
    }
}
