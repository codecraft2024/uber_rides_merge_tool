package com.egyptianbanks.ipn.uberridesmergetool.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

@Configuration
public class AppConfig {



    @Bean
    @ConditionalOnMissingBean
    public GraphicsEnvironment graphicsEnvironment() {
        // Better font rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        return GraphicsEnvironment.getLocalGraphicsEnvironment();
    }
}