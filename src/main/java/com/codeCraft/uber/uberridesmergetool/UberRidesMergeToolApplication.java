package com.codeCraft.uber.uberridesmergetool;

import com.codeCraft.uber.uberridesmergetool.ui.MainScreen;
import com.formdev.flatlaf.FlatLightLaf;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class UberRidesMergeToolApplication {
    public static void main(String[] args) {

        FlatLightLaf.setup();
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(UberRidesMergeToolApplication.class)
                .headless(false)
                .run(args);
        MainScreen mainScreen = ctx.getBean(MainScreen.class);
        mainScreen.setVisible(true);
    }

}


