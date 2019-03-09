package de.wesim.summernotefx;

import java.util.Map;
import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

    public class SummernoteWhenLoadedListener implements ChangeListener<Worker.State> {

        private final SummerNoteEditor backReference;
        private final HostServices hostServices;
        //private Configuration configuration;
        private final String editorContent;
        private WebView webview;
        private final Map<String, String> cssProperties;

        public SummernoteWhenLoadedListener(SummerNoteEditor caller, HostServices hostServices,
                String editorContent, Map<String, String> cssProperties) {
            this.backReference = caller;
            this.hostServices = hostServices;
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
            if (cssProperties != null && !cssProperties.isEmpty()) {
                for (String cssProperty : cssProperties.keySet()) {
                    this.backReference.setCssStyle(cssProperty, this.cssProperties.get(cssProperty));
                }
            }
            this.backReference.setHtmlText(this.editorContent);
            // TODO Konsolidieren
            // zweiten Click-Listener implementieren
            var nodeList = this.webview.getEngine().getDocument().getElementsByTagName("div");
            Element foundElement = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element curElem = (Element) nodeList.item(i);
                String classValue = curElem.getAttribute("class");
                if (classValue == null) {
                    continue;
                }
                if (classValue.contains("note-editable")) { // note-link-popover
                    foundElement = curElem;
                    break;
                }
            }

            final EventTarget eventTarget = (EventTarget) foundElement;
            eventTarget.addEventListener("click", evt -> {

                final EventTarget target = evt.getTarget();
                if (!(target instanceof HTMLAnchorElement)) {
                    return;
                }

                final HTMLAnchorElement anchorElement = (HTMLAnchorElement) target;
                final String url = anchorElement.getHref();
                final String targetAttr = anchorElement.getTarget();
                if (targetAttr.equals("_blank")) {
                    evt.preventDefault();
                }
            }, false);

            nodeList = this.webview.getEngine().getDocument().getElementsByTagName("div");
            foundElement = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element curElem = (Element) nodeList.item(i);
                String classValue = curElem.getAttribute("class");
                if (classValue == null) {
                    continue;
                }
                if (classValue.contains("note-link-popover")) { // note-link-popover
                    foundElement = curElem;
                    break;
                }
            }

            var eventTarget2 = (EventTarget) foundElement;
            eventTarget2.addEventListener("click", evt -> {

                final EventTarget target = evt.getTarget();
                if (!(target instanceof HTMLAnchorElement)) {
                    return;
                }

                final HTMLAnchorElement anchorElement = (HTMLAnchorElement) target;
                final String url = anchorElement.getHref();
                final String targetAttr = anchorElement.getTarget();
                if (targetAttr.equals("_blank")) {
                    evt.preventDefault();
                }
                // open url with host system's default
                if (url.length() > 0 && hostServices != null) {
                    hostServices.showDocument(url);
                }
            }, false);
        }
    }

