package com.egyptianbanks.ipn.uberridesmergetool.config;

import com.egyptianbanks.ipn.uberridesmergetool.StatusLogger;
import com.egyptianbanks.ipn.uberridesmergetool.service.UberMergeManager;
import com.egyptianbanks.ipn.uberridesmergetool.ui.MainFrame;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;
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