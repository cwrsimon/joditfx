package de.wesim.summernotefx;

import com.sun.javafx.webkit.WebConsoleListener;
import org.apache.commons.text.StringEscapeUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import javafx.application.HostServices;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummerNoteEditor extends StackPane {

    final WebView webview = new WebView();

    private final HostServices hostServices;

    public void findItems(String entered) {
        final String content_js = StringEscapeUtils.escapeEcmaScript(entered);
        webview.getEngine().executeScript("findOccurrences('" + content_js + "');");
    }

    private String getHTMLContent() {
        final String editingCode = (String) webview.getEngine().executeScript("getEditorContent();");
        return editingCode;
    }

    public String getHtmlText() {
        return this.getHTMLContent();
    }

    private void setHTMLContent(String content) {
        final String content_js = StringEscapeUtils.escapeEcmaScript(content);
        getLogger().debug("Setting content : {}", content_js);
        // FIXME
        //webview.getEngine().executeScript("setEditorContent('" + content_js + "');");
        setContentUpdate(false);
    }

    public void setHtmlText(String content) {
        this.setHTMLContent(content);
    }

    private final BooleanProperty contentUpdate = new SimpleBooleanProperty(false);

    public final BooleanProperty contentUpdateProperty() {
        return contentUpdate;
    }

    public void openURL(String url) {
        if (url.length() > 0) {
            hostServices.showDocument(url);
        }
    }

    public final void setContentUpdate(boolean newValue) {
        contentUpdate.set(newValue);
    }

    public final boolean getContentUpdate() {
        return contentUpdate.get();
    }

    public void setCssStyle(String styleName, String value) {
        webview.getEngine().executeScript("setInlineStyle('"
                + styleName + "','" + value + "');");
    }

    
    public SummerNoteEditor(HostServices hostServices, String editorContent, Map<String, String> extraCSSProperties) {
        this.hostServices = hostServices;

        WebConsoleListener.setDefaultListener((WebView wv, String msg, int i, String source) -> {
            getLogger().info("JS Console [{}, {}]: {}", source, i, msg);
        });

        final SummerNoteEditor backReference = this;
        webview.getEngine().getLoadWorker().stateProperty().addListener(
                new SummernoteWhenLoadedListener(backReference, editorContent, extraCSSProperties));
        webview.getEngine().getLoadWorker().exceptionProperty().addListener((ObservableValue<? extends Throwable> ov, Throwable t, Throwable t1) -> {
            getLogger().error("SummernoteEditorFX Webview exception, ov: {}", ov.getValue().getLocalizedMessage(), ov.getValue());
            getLogger().error("SummernoteEditorFX Webview exception, t: {}", t.getLocalizedMessage(), t);
            getLogger().error("SummernoteEditorFX Webview exception, t1: {}", t1.getLocalizedMessage(), t1);
        });

        webview.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        webview.getEngine().setOnError(e -> {
            WebErrorEvent event = (WebErrorEvent) e;
            getLogger().error("{}", event.getException());
        });

        var htmlSource = prepareHtmlSource();
//        htmlSource = addI18NSupport(htmlSource);
        // TODO make this a debug feature with a source target
        // dump generated HTML source when you need it
         getLogger().info(htmlSource);
       //  webview.getEngine().setUserStyleSheetLocation("file:///C:/msys64/home/cwrsi/summernotefx/target/classes/jodit.min.css");
        webview.getEngine().loadContent(htmlSource);
        this.getChildren().add(webview);
    }

    public Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    WebView getWebView() {
        return this.webview;
    }

    private String prepareHtmlSource() {
        final URL summernoteLiteJS = SummerNoteEditor.class.getResource("/jodit.min.js");
        final URL summernoteCSSResource = SummerNoteEditor.class.getResource("/jodit.es2018.min.css");
        final URL summernoteHTMLResource = SummerNoteEditor.class.getResource("/de/wesim/summernotefx/summernote.html");
        
        var htmlSource = "ERROR";
        if ( summernoteLiteJS == null 
                || summernoteCSSResource == null
                || summernoteHTMLResource == null) {
            return htmlSource;
        }
        try (InputStream summernoteHTMLIS = summernoteHTMLResource.openStream()) {
            htmlSource = new String(summernoteHTMLIS.readAllBytes(), "UTF-8");
        } catch (IOException ex) {
            getLogger().error("Loading HTML source failed: {}", ex.getLocalizedMessage(), ex);
            return htmlSource;
        }
        final String summernoteLiteJSURL = summernoteLiteJS.toExternalForm();
        final String summernoteCSSURL = summernoteCSSResource.toExternalForm();
        htmlSource = htmlSource.replace("%SUMMERNOTE_LITE_JS_URL%", summernoteLiteJSURL);
        htmlSource = htmlSource.replace("%SUMMERNOTE_LITE_CSS_URL%", summernoteCSSURL);

        return htmlSource;
    }

    private String addI18NSupport(String htmlSource) {
        String locale = System.getProperty("user.language") + "-" + System.getProperty("user.country");
        final String variant = System.getProperty("user.variant");
        if (variant != null) {
            locale = locale + "-" + variant;
        }
        var i18NFile = SummerNoteEditor.class.getResource("/lang/summernote-" + locale + ".min.js");
        if (i18NFile == null) {
            return htmlSource;
        }
        final String i18NURL = i18NFile.toExternalForm();
        htmlSource = htmlSource.replace("%I18N_URL%", i18NURL);
        htmlSource = htmlSource.replace("en-US", locale);
        return htmlSource;
    }
}
