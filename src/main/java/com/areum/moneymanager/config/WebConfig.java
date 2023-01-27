package com.areum.moneymanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${image.accountBook.resourcePath}")
    private String accountResourcePath;
    @Value("${image.accountBook.connectPath}")
    private String accountConnectPath;
    @Value("${image.profile.resourcePath}")
    private String profileResourcePath;
    @Value("${image.profile.connectPath}")
    private String profileConnectPath;

    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry ) {
        registry.addResourceHandler(accountConnectPath).addResourceLocations(accountResourcePath);
        registry.addResourceHandler(profileConnectPath).addResourceLocations(profileResourcePath);
    }

}
