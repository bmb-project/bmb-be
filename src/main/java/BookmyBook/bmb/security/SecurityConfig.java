package BookmyBook.bmb.security;

import BookmyBook.bmb.exception.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@PropertySource(value = "classpath:env.yml", factory = YamlPropertySourceFactory.class)
public class SecurityConfig {

    @Autowired
    private final JwtUtil jwtUtil;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtUtil jwtUtil, CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtUtil = jwtUtil;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/user/signin").permitAll() // 로그인 API는 인증 없이 접근 가능
                                .requestMatchers("/user/signup").permitAll() // 회원가입 API
                                .requestMatchers("/user/loan").authenticated() //회원별도서목록조회
                                .requestMatchers("/user/wish").authenticated() //회원별좋아요목록조회
                                .requestMatchers("/books").authenticated() //도서목록조회
                                .requestMatchers("/books/{isbn}/wish").authenticated() //도서별좋아요목록조회
                                .requestMatchers("/loan").authenticated() //도서대여
                                .requestMatchers("/loan").authenticated() //도서반납
                                .requestMatchers("/admin/books").hasRole("ADMIN") //admin도서목록
                                .anyRequest().authenticated() // 그 외의 요청은 인증 필요
                )
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://bmb-project.vercel.app")); // 허용할 출처
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 메소드
        config.setAllowedHeaders(List.of("*")); // 허용할 헤더
        config.setAllowCredentials(true); // 자격 증명(쿠키 등)을 허용
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}