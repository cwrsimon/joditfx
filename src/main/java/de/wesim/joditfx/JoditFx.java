package de.wesim.joditfx;

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

public class JoditFx extends StackPane {

    final WebView webview = new WebView();

    private final HostServices hostServices;

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
        webview.getEngine().executeScript("setEditorContent('" + content_js + "');");
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

    public JoditFx(HostServices hostServices, String editorContent, Map<String, String> extraCSSProperties) {
        this.hostServices = hostServices;

        WebConsoleListener.setDefaultListener((WebView wv, String msg, int i, String source) -> {
            getLogger().info("JS Console [{}, {}]: {}", source, i, msg);
        });

        final JoditFx backReference = this;
        webview.getEngine().getLoadWorker().stateProperty().addListener(
                new LoadedListener(backReference, editorContent));
        webview.getEngine().getLoadWorker().exceptionProperty().addListener((ObservableValue<? extends Throwable> ov, Throwable t, Throwable t1) -> {
            getLogger().error("JoditFX Webview exception, ov: {}", ov.getValue().getLocalizedMessage(), ov.getValue());
            getLogger().error("JoditFX Webview exception, t: {}", t.getLocalizedMessage(), t);
            getLogger().error("JoditFX Webview exception, t1: {}", t1.getLocalizedMessage(), t1);
        });

        webview.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        webview.getEngine().setOnError(e -> {
            WebErrorEvent event = (WebErrorEvent) e;
            getLogger().error("{}", event.getException());
        });

        var htmlSource = prepareHtmlSource();
        var cssURL = prepareCssUrl();
        htmlSource = addI18NSupport(htmlSource);
        // TODO make this a debug feature
        // dump generated HTML source when you need it
        // getLogger().info(htmlSource);
        webview.getEngine().setUserStyleSheetLocation(cssURL);
        webview.getEngine().loadContent(htmlSource);
        this.getChildren().add(webview);
    }

    public Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    WebView getWebView() {
        return this.webview;
    }

    private String prepareCssUrl() {
        final URL summernoteCSSResource = JoditFx.class.getResource("/jodit.min.css");
        return summernoteCSSResource.toExternalForm();
    }
    
    private String prepareHtmlSource() {
        final URL summernoteLiteJS = JoditFx.class.getResource("/jodit.min.js");
        final URL summernoteHTMLResource = JoditFx.class.getResource("/de/wesim/joditfx/joditfx.html");
        
        var htmlSource = "ERROR";
        if ( summernoteLiteJS == null 
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
        htmlSource = htmlSource.replace("%SUMMERNOTE_LITE_JS_URL%", summernoteLiteJSURL);

        return htmlSource;
    }

    private String addI18NSupport(String htmlSource) {
        var locale = System.getProperty("user.language");
        htmlSource = htmlSource.replace("%I18N%", locale);
        return htmlSource;
    }
}
