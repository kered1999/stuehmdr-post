package com.cybr406.post.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("post").stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .requestMatchers().antMatchers("/posts/**")
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/posts/**").permitAll()
                .antMatchers("/profiles/**", "/profiles", "/posts", "/posts/**")
                .access("#oauth2.isOAuth() and hasAnyRole('ROLE_BLOGGER', 'ROLE_ADMIN')");
        // @formatter:on
    }
}