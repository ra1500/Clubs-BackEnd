package neural.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.naming.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class LoginConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomAuthenticationProvider customAuthProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthProvider);
    }

    @Bean
    public PasswordEncoder encoder() {return new BCryptPasswordEncoder();}

    // CORS bean
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // cross-site request forgery. disabled for now. need to generate a code to feed into the html login form.
                .authorizeRequests()
                .antMatchers("/api/user/userId").permitAll() // login post
                .antMatchers("/api/user/signup").permitAll() // sign up
                .antMatchers("/api/prm/sc/dc*").permitAll() // public profile stats
                .antMatchers("/api/user/pp*").permitAll() // public profile text
                .antMatchers("/api/files/pp*").permitAll() // public profile image
                .antMatchers("/api/*").permitAll() // Introduction page
                .antMatchers("/api/g/a").permitAll() // sample game
                .antMatchers("/*").permitAll() // homepage/index
                .antMatchers("*.js", "*.jsx").permitAll()
                .antMatchers("*.ico").permitAll()
                .antMatchers("/*.ico").permitAll()
                .antMatchers("/index.html").permitAll()
                .antMatchers("/keystore/keystore.p12").permitAll()
                //.antMatchers("/**").permitAll() // gives all access without authentication
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        //http
        //         .addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class); //TODO
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER);  //
        http
                .cors();
        // https SSL certificate use
        //http
        //        .requiresChannel()
        //        .anyRequest()
        //        .requiresSecure();
    }

}