/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wesim.summernotefx;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DemoApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        SummerNoteEditor editor = new SummerNoteEditor(getHostServices(), "");
        Scene scene = new Scene(editor, 600, 400);

        primaryStage.setTitle("SummernoteFX Demo Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /* 
    	If we include the main() method then we need to call Application.launch()
    	for launching the JavaFX application
    */
    public static void main(String[] args) {
        launch(args);
    }
}