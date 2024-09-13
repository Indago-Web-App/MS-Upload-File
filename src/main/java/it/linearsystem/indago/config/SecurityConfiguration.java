package it.linearsystem.indago.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * DA PROVARE CON JWT : https://medium.com/@bharatrajmeriyala/spring-cloud-security-with-netflix-zuul-2ef04a1dcfb
 * <p>
 * SpringBootWebSecurityConfiguration.SecurityFilterChainConfiguration:
 * Did not match:
 * - AllNestedConditions 1 matched 1 did not; NestedCondition on DefaultWebSecurityCondition.Beans @ConditionalOnMissingBean (types: org.springframework.security.web.SecurityFilterChain; SearchStrategy: all) found beans of type 'org.springframework.security.web.SecurityFilterChain' bookFilterChain; NestedCondition on DefaultWebSecurityCondition.Classes @ConditionalOnClass found required classes 'org.springframework.security.web.SecurityFilterChain', 'org.springframework.security.config.annotation.web.builders.HttpSecurity' (DefaultWebSecurityCondition)
 */
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true) // DEPRECATA
//@EnableConfigurationProperties(BasicAuthProperties.class)
//@EnableMethodSecurity // https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
//@EnableMethodSecurity(securedEnabled = true) // PER USARE @Secured
//@EnableMethodSecurity(jsr250Enabled = true) // PER USARE @RolesAllowed, @PermitAll, @DenyAll
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
//              .roles("ADMIN", "USER")                
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(request -> request.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf().disable() // Invalid CSRF token found for
//                   .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http.csrf().disable()
//                .authorizeHttpRequests()
//                .requestMatchers("/template/welcome").permitAll()
//                .and()
//                .authorizeHttpRequests().requestMatchers("/template/user/**").authenticated()
//                .and()
//                .authorizeHttpRequests().requestMatchers("/template/admin/**").authenticated()
////                .and().formLogin()
//                .and().build();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
//      return new BCryptPasswordEncoder();    	
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder;
    }

//    @Bean
//    public SecurityFilterChain bookFilterChain(HttpSecurity http) throws Exception {
//        http
////                .authorizeRequests()
////                .antMatchers(HttpMethod.GET, "/library/info").permitAll()
////                .antMatchers(HttpMethod.GET, "/library/books").hasRole("USER")
////                .antMatchers(HttpMethod.GET, "/library/books/all").hasRole("ADMIN");
//        
////      .authorizeHttpRequests().anyRequest().permitAll();
////      .authorizeHttpRequests().requestMatchers("/**").permitAll();
////      .authorizeHttpRequests().requestMatchers(HttpMethod.POST, "/template/**").permitAll();
////      .authorizeHttpRequests().requestMatchers(HttpMethod.POST, "/template/method-post-with-json").permitAll();
////      .authorizeHttpRequests().requestMatchers(HttpMethod.POST, "/template/method-post-with-json/**").permitAll();
//        
////      .authorizeHttpRequests().anyRequest().authenticated();
////      .authorizeHttpRequests().requestMatchers("/**").authenticated();
////      .authorizeHttpRequests().requestMatchers(HttpMethod.POST, "/template/**").authenticated();
////      .authorizeHttpRequests().requestMatchers(HttpMethod.POST, "/template/method-post-with-json").authenticated();
////      .authorizeHttpRequests().requestMatchers(HttpMethod.POST, "/template/method-post-with-json/**").authenticated();
//        
////        .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers("/**").permitAll() ); //("admin") );
////        .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers("/**").permitAll() ); //("admin") );
//        
////        http.httpBasic();
//        
////        .authorizeRequests(request -> 
////        request.antMatchers(new String[]{""}).permitAll()
////                .anyRequest().authenticated());
//        
////        .authorizeHttpRequests()
////        .requestMatchers("/template/**")
////        .permitAll()
////        .requestMatchers(HttpMethod.POST, "/template/**").permitAll()
////        .anyRequest().authenticated()
////        .and()
////        .httpBasic();
//        
////        .csrf().disable()
//////                .authorizeHttpRequests().requestMatchers("/template/**").permitAll()
//////                .and()
////                .authorizeHttpRequests().requestMatchers("/template/method-post-with-json").authenticated()
////                .and()
////                .authorizeHttpRequests().requestMatchers("/template/**").permitAll()
//////                .authorizeHttpRequests().requestMatchers("/auth/admin/**").authenticated()
//////                .and().formLogin()
////                ;
//        
//        .csrf(csrf -> {
//            csrf.disable();
//        })
//        .cors(cors -> cors.disable())
//        .authorizeHttpRequests(auth -> {
////            auth.requestMatchers("/template/**").permitAll();
//            auth.requestMatchers("/template/method-post-with-json").permitAll();
//            auth.anyRequest().authenticated();
//        });
//        
//        return http.build();
//    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers("/**");
//    }

//	@Bean
//	public WebSecurityCustomizer webSecurityCustomizer() {
//		return (web) -> web.ignoring() // FA PASSARE LA SOLA GET CON LO USER
//									  .requestMatchers("/template/method-get-with-parameter/**") // FA PASSARE LA SOLA GET SIA CON, SIA SENZA LO USER
//									  .requestMatchers("/template/method-post-with-json") // FA PASSARE LA SOLA POST SIA CON, SIA SENZA LO USER
//									  ;
//	}

}
