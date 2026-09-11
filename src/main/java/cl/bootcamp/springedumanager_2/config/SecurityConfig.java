package cl.bootcamp.springedumanager_2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/*
 * =====================================================
 * SPRING SECURITY
 * =====================================================
 *
 * Configura la autenticacion (quien eres) y la
 * autorizacion (que puedes hacer) de la aplicacion.
 *
 * Dos usuarios en memoria, definidos en
 * application.properties:
 *
 * - admin: puede crear, editar y eliminar cursos.
 * - usuario: solo puede navegar y consultar datos.
 */
@Configuration
public class SecurityConfig {

    @Value("${app.security.admin.username}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Value("${app.security.user.username}")
    private String userUsername;

    @Value("${app.security.user.password}")
    private String userPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        var admin = User.withUsername(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        var usuario = User.withUsername(userUsername)
                .password(passwordEncoder.encode(userPassword))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, usuario);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                // Recursos publicos: login y assets estaticos.
                .requestMatchers("/login", "/js/**", "/css/**").permitAll()

                // Solo ADMIN puede crear/editar/eliminar cursos.
                .requestMatchers("/cursos/guardar", "/cursos/eliminar/**").hasRole("ADMIN")

                // El resto de la aplicacion requiere estar autenticado
                // (ADMIN o USER).
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("correo")
                .passwordParameter("password")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                // El enlace de logout del navbar es un <a href="/logout">
                // (GET), por eso se habilita explicitamente ese metodo.
                .logoutRequestMatcher(PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
