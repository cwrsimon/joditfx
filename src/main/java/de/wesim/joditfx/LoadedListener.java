package de.wesim.joditfx;

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

    public LoadedListener(JoditFx caller, String editorContent) {
        this.backReference = caller;
        this.editorContent = editorContent;
        this.webview = caller.getWebView();
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (newValue != Worker.State.SUCCEEDED) {
            return;
        }

        final JSObject window = (JSObject) this.webview.getEngine().executeScript("window");
        window.setMember("app", backReference);
        
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
