//package njgis.opengms.portal.test.WebSocketTest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
////@Configuration
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
////        System.out.println("------------encoder.encode:" + encoder.encode("456"));
////        super.configure(auth);
//        auth.inMemoryAuthentication()
//            .withUser("admin")
//            .password(encoder.encode("123"))
//            .roles("admin")
//            .and()
//            .withUser("sang")
//            .password(encoder.encode("456"))
//            .roles("user");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        super.configure(http);
//        http.authorizeRequests()
//            .anyRequest().authenticated()
//            .and()
//            .formLogin().permitAll();
//    }
//}
