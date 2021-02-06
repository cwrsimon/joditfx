package de.wesim.joditfx;

import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

public class LoadedListener implements ChangeListener<Worker.State> {

    private final JoditFx backReference;
    private final String editorContent;
    private final WebView webview;
    private final Map<String, String> cssProperties;

    public LoadedListener(JoditFx caller,
            String editorContent, Map<String, String> cssProperties) {
        this.backReference = caller;
        this.editorContent = editorContent;
        this.cssProperties = cssProperties;
        this.webview = caller.getWebView();
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (newValue != Worker.State.SUCCEEDED) {
            return;
        }

        final JSObject window = (JSObject) this.webview.getEngine().executeScript("window");
        window.setMember("app", backReference);
        // TODO Do we still need this?
        if (cssProperties != null && !cssProperties.isEmpty()) {
            for (String cssProperty : cssProperties.keySet()) {
                // FIXME
                //   this.backReference.setCssStyle(cssProperty, this.cssProperties.get(cssProperty));
            }
        }
        this.backReference.setHtmlText(this.editorContent);
        // unfortunately, we don't have any class selectors in JavaFx ...
        var nodeList = this.webview.getEngine().getDocument().getElementsByTagName("div");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element curElem = (Element) nodeList.item(i);
            final String classValue = curElem.getAttribute("class");
            if (classValue == null) {
                continue;
            }
            if (classValue.contains("jodit-wysiwyg")) {
                // clicks here are not supposed to open any urls
                var eventTarget = (EventTarget) curElem;
                eventTarget.addEventListener("click", new HandleAnchorClicksEventListener(), false);
            }
        }
    }

    private class HandleAnchorClicksEventListener implements EventListener {

        HandleAnchorClicksEventListener() {
        }

        @Override
        public void handleEvent(Event event) {
            final EventTarget target = event.getTarget();
            if (!(target instanceof HTMLAnchorElement)) {
                return;
            }
            event.preventDefault();
        }
    }
}
