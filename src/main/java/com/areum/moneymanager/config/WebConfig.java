package com.areum.moneymanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${image.accountBook.resourcePath}")
    private String resourcePath;
    @Value("${image.accountBook.connectPath}")
    private String connectPath;

    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry ) {
        registry.addResourceHandler(connectPath).addResourceLocations(resourcePath);
    }

}
