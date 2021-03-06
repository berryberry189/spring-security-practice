package com.grace.springsecuritypractice.config;

import com.grace.springsecuritypractice.filter.StopwatchFilter;
import com.grace.springsecuritypractice.filter.TestAuthenticationFilter;
import com.grace.springsecuritypractice.jwt.JwtAuthenticationFilter;
import com.grace.springsecuritypractice.jwt.JwtAuthorizationFilter;
import com.grace.springsecuritypractice.jwt.JwtProperties;
import com.grace.springsecuritypractice.user.User;
import com.grace.springsecuritypractice.user.UserRepository;
import com.grace.springsecuritypractice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // stopwatch filter ??????
        // stopwatch filter??? ?????? ?????? ???????????? ?????????
        // WebAsyncManagerIntegrationFilter ?????? ?????? ?????? (addFilterBefore)
        //http.addFilterBefore(new StopwatchFilter(), WebAsyncManagerIntegrationFilter.class);

        // tester authentication filter ??????
        // UsernamePasswordAuthenticationFilter ?????? ?????? ??????????????? ???
        //http.addFilterBefore(new TestAuthenticationFilter(this.authenticationManager()),
        //        UsernamePasswordAuthenticationFilter.class);

        // basic authentication filter disable
        http.httpBasic().disable();

        // csrf
        http.csrf();

        // rememberMeAuthenticationFilter
        http.rememberMe();

        // stateless
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // jwt filter
        http.addFilterBefore(
                new JwtAuthenticationFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class
        ).addFilterBefore(
                new JwtAuthorizationFilter(userRepository),
                BasicAuthenticationFilter.class
        );


        // authorization ????????? ?????? ??????
        http.authorizeRequests()
                // /, /home, /signup => ????????? ?????? ??????
                .antMatchers("/", "/home", "/signup").permitAll()
                // user ????????? ????????????
                .antMatchers("/note").hasRole("USER")
                // admin ????????? ????????????
                .antMatchers("/admin").hasRole("ADMIN")
                // /notice ????????? POST, DELETE ????????? admin ????????? ????????????
                .antMatchers(HttpMethod.POST, "/notice").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/notice").hasRole("ADMIN")
                .anyRequest().authenticated();

        // login
        http.formLogin()
                // ????????? ????????? ??????
                .loginPage("/login")
                // ????????? ???????????? ??????
                .defaultSuccessUrl("/")
                .permitAll();

        // logout
        http.logout()
                // ???????????? ?????? ??????
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies(JwtProperties.COOKIE_NAME);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                // ?????? ???????????? ???????????? ????????? ?????? ignoring
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        // User ???????????? UserDetails??? ???????????? ???????????? User ?????? ??????
        return username -> {
            User user = userService.findByUsername(username);
            if(user != null){
                throw new UsernameNotFoundException(username);
            }
            return user;
        };
    }

}
