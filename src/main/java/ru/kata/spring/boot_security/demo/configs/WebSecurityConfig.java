package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.services.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SuccessUserHandler successUserHandler;
    private final UserService userService;
 private final UserDetailsService userDetailsService;//?
@Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler,
                             UserService userService, UserDetailsService userDetailsService) {
        this.successUserHandler = successUserHandler;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }
    //Настраиваем конфигурацию самого С Секьюрити
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()//?безопасность
                .authorizeRequests()
                .antMatchers("/", "/index").permitAll()//antMatchers указ url-адреса "/" и "/index" разрешены всем
                .antMatchers("/admin/**").hasRole("ADMIN")//В "/admin/**" могут заходить только юзеры с ролью "ADMIN"
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .and().formLogin().successHandler(successUserHandler).permitAll()
                .and()
                .logout().logoutSuccessUrl("/login");
    }
    // аутентификация inMemory
    // @Bean
    //  @Override
    // public UserDetailsService userDetailsService() {
    //     UserDetails user =
    //            User.withDefaultPasswordEncoder()
    //                   .username("user")
    //                    .password("user")
    //                   .roles("USER")
    //                   .build();

    //  return new InMemoryUserDetailsManager(user);
    // }
    //Преобразователь паролей в хэш.пароли в БД лежат в преобразованном виде.
//Чтобы сравнить введенный с формы пароль с паролем в БД, нужно преобразовать в такой же вид пароль с формы.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}