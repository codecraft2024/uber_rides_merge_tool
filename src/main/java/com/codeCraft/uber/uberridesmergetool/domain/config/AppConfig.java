package com.codeCraft.uber.uberridesmergetool.domain.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

@Configuration
public class AppConfig {
    @Bean
    @ConditionalOnMissingBean
    public GraphicsEnvironment graphicsEnvironment() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        return GraphicsEnvironment.getLocalGraphicsEnvironment();
    }


}