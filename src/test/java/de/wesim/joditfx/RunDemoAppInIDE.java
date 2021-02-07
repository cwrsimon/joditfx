package de.wesim.joditfx;

import java.io.IOException;

// Run this class from within your IDE.
public class RunDemoAppInIDE {

    public static void main(String[] args) throws IOException {
        System.setProperty("user.language", "de");
      //  System.setProperty("user.country", "KR");
//        System.setProperty("user.variant", "Latin");


        // call actual JavaFX main app
        // thanks to https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing
        DemoApp.main(args);
    }
}
