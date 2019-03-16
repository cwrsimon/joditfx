package de.wesim.summernotefx;

import de.wesim.summernotefx.DemoApp;
import java.io.IOException;

// Run this class from within your IDE.
public class RunDemoAppInIDE {

    public static void main(String[] args) throws IOException {
        // improved font rendering with anti aliasing under Linux
        //System.setProperty("prism.lcdtext", "false");
        //System.setProperty("prism.subpixeltext", "false");

        //System.setProperty("user.language", "ko");
      //  System.setProperty("user.country", "KR");
//        System.setProperty("user.variant", "Latin");


        // call actual JavaFX main app
        // thanks to https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing
        DemoApp.main(args);
    }
}
