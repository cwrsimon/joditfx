package de.wesim.summernotefx;

import com.sun.javafx.webkit.WebConsoleListener;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import javafx.application.HostServices;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO Check for updates in order to make image import work:
// https://bugs.java.com/view_bug.do?bug_id=8197790
// TODO Konsolidieren
// TODO Lokalisierung !!!
public class SummerNoteEditor extends StackPane {

    final WebView webview = new WebView();

    private final HostServices hostServices;

    void findItems(String entered) {
        final String content_js = StringEscapeUtils.escapeEcmaScript(entered);
        webview.getEngine().executeScript("findOccurrences('" + content_js + "');");
    }

    public String getHTMLContent() {
        final String editingCode = (String) webview.getEngine().executeScript("getEditorContent();");
        return editingCode;
    }

    public String getHtmlText() {
        return this.getHTMLContent();
    }

    public void setHTMLContent(String content) {
        final String content_js = StringEscapeUtils.escapeEcmaScript(content);
        getLogger().debug("Setting content...: {}", content_js);
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

    public void setCssStyle(String styleName, String value) {
        webview.getEngine().executeScript("setInlineStyle('"
                + styleName + "','" + value + "');");
    }

    // TODO CSSProperties 
    public SummerNoteEditor(HostServices hostServices, String editorContent) {
        this.hostServices = hostServices;

        WebConsoleListener.setDefaultListener((WebView wv, String msg, int i, String source) -> {
            getLogger().info("JS Console [{}, {}]: {}", source, i, msg);
        });
                
        final SummerNoteEditor backReference = this;
        webview.getEngine().getLoadWorker().stateProperty().addListener(
                new SummernoteWhenLoadedListener(backReference, editorContent, null));
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
        final String jqueryURL = SummerNoteEditor.class.getResource("/jquery.slim.min.js").toExternalForm();
        final String summernoteLiteJSURL = SummerNoteEditor.class.getResource("/summernote-lite.js").toExternalForm();
        final String summernoteCSSURL = SummerNoteEditor.class.getResource("/summernote-lite.css").toExternalForm();
        final String markJSURL = SummerNoteEditor.class.getResource("/jquery.mark.min.js").toExternalForm();
        final String i18NURL = SummerNoteEditor.class.getResource("/lang/summernote-de-DE.min.js").toExternalForm();
        String htmlSource = "ERROR";
        try {
            htmlSource = new String(SummerNoteEditor.class.getResource("/de/wesim/summernotefx/summernote.html").openStream().readAllBytes(), "UTF-8");
            htmlSource = htmlSource.replace("%SUMMERNOTE_FONT%", SummerNoteEditor.class.getResource("/font/summernote.woff").toExternalForm());
            htmlSource = htmlSource.replace("%JQUERY_URL%", jqueryURL);
            htmlSource = htmlSource.replace("%SUMMERNOTE_LITE_JS_URL%", summernoteLiteJSURL);
            htmlSource = htmlSource.replace("%SUMMERNOTE_LITE_CSS_URL%", summernoteCSSURL);
            htmlSource = htmlSource.replace("%MARK_JS_URL%", markJSURL);
            htmlSource = htmlSource.replace("%I18N_URL%", i18NURL);

            getLogger().debug(htmlSource);
        } catch (RuntimeException | IOException ex) {
            getLogger().error("Preparing HTML source failed: {}", ex.getLocalizedMessage(), ex);
        }
        return htmlSource;
    }
}
