package io.github.saratrajan.samplems.web;

import io.github.saratrajan.samplems.api.ApiPaths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Only the mutating order calls require a client id; GET status does not.
        registry.addInterceptor(new ClientIdInterceptor())
                .addPathPatterns(ApiPaths.ORDERS + "/*:checkout", ApiPaths.ORDERS + "/*:cancel");
    }
}
