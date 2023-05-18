package sideeffect.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sideeffect.project.common.converter.ApplicantStatusRequestConverter;
import sideeffect.project.common.converter.StackTypeRequestConverter;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PATCH", "DELETE")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StackTypeRequestConverter());
        registry.addConverter(new ApplicantStatusRequestConverter());
    }
}
