package de.wesim.summernotefx;

import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

// TODO Integrate content update
public class DemoApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        final SummerNoteEditor editor = new SummerNoteEditor(getHostServices(), "Hello World!", null);

        final TextArea area = new TextArea();
        area.setPrefRowCount(5);
        final MenuBar menuBar = new MenuBar();
        final Menu menu = new Menu("Public API methods");
        final MenuItem getHtmlText = new MenuItem("getHtmlText()");
        final MenuItem setHtmlText = new MenuItem("setHtmlText(String content)");
        final MenuItem find = new MenuItem("findItems()");
        final MenuItem quit = new MenuItem("Quit");
        menu.getItems().addAll(getHtmlText, setHtmlText, find, new SeparatorMenuItem(), quit);
        menuBar.getMenus().add(menu);
        quit.setOnAction(event -> {
                primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        find.setOnAction(e -> {
            final TextInputDialog dialog
                    = new TextInputDialog("Find this text ...");
            final Optional<String> result = dialog.showAndWait();
            if (!result.isPresent()) {
                return;
            }
            final String entered = result.get();
            editor.findItems(entered);
        });
        getHtmlText.setOnAction(e-> {
            final String htmlText = editor.getHtmlText();
            area.setText(htmlText);
        });
        setHtmlText.setOnAction( e-> {
            final String plainText = area.getText();
            editor.setHtmlText(plainText);
        });

        final BorderPane mainPane = new BorderPane();

        final Scene scene = new Scene(mainPane, 600, 400);
        mainPane.setCenter(editor);
        mainPane.setTop(menuBar);
        mainPane.setBottom(area);
        
        primaryStage.setTitle("SummernoteFX Demo Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /* 
    	If we include the main() method, then we will need to call Application.launch()
    	for launching the JavaFX application
    */
    public static void main(String[] args) {
        launch(args);
    }
}