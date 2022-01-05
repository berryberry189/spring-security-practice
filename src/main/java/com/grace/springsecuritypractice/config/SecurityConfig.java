package com.grace.springsecuritypractice.config;

import com.grace.springsecuritypractice.filter.StopwatchFilter;
import com.grace.springsecuritypractice.filter.TestAuthenticationFilter;
import com.grace.springsecuritypractice.user.User;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // stopwatch filter 추가
        // stopwatch filter를 가장 먼저 둬야하기 때문에
        // WebAsyncManagerIntegrationFilter 보다 앞에 위치 (addFilterBefore)
        http.addFilterBefore(new StopwatchFilter(), WebAsyncManagerIntegrationFilter.class);

        // tester authentication filter 추가
        // UsernamePasswordAuthenticationFilter 보다 앞에 위치하도록 함
        http.addFilterBefore(new TestAuthenticationFilter(this.authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);

        // basic authentication filter disable
        http.httpBasic().disable();

        // csrf
        http.csrf();

        // rememberMeAuthenticationFilter
        http.rememberMe();

        // authorization 경로별 권한 설정
        http.authorizeRequests()
                // /, /home, /signup => 모두가 사용 가능
                .antMatchers("/", "/home", "/signup").permitAll()
                // user 권한인 경우에만
                .antMatchers("/note").hasRole("USER")
                // admin 권한인 경우에만
                .antMatchers("/admin").hasRole("ADMIN")
                // /notice 경로의 POST, DELETE 요청은 admin 권한인 경우에만
                .antMatchers(HttpMethod.POST, "/notice").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/notice").hasRole("ADMIN")
                .anyRequest().authenticated();

        // login
        http.formLogin()
                // 로그인 페이지 설정
                .loginPage("/login")
                // 성공지 이동하는 경로
                .defaultSuccessUrl("/")
                .permitAll();

        // logout
        http.logout()
                // 로그아웃 요청 경로
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/");

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                // 정적 리소스의 일반적인 위치를 전부 ignoring
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        // User 클래스가 UserDetails을 상속받고 있으므로 User 리턴 가능
        return username -> {
            User user = userService.findByUsername(username);
            if(user != null){
                throw new UsernameNotFoundException(username);
            }
            return user;
        };
    }

}
