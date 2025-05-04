package com.egyptianbanks.ipn.uberridesmergetool;

import com.egyptianbanks.ipn.uberridesmergetool.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class UberRidesMergeToolApplication {

    public static void main(String[] args) {
       // SpringApplication.run(UberRidesMergeToolApplication.class, args);

        FlatLightLaf.setup();

        // Create Spring context
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(UberRidesMergeToolApplication.class)
                .headless(false) // Important for Swing apps
                .run(args);

        // Get and show the main frame from Spring context
        MainFrame mainFrame = ctx.getBean(MainFrame.class);
        mainFrame.setVisible(true);
    }

}


