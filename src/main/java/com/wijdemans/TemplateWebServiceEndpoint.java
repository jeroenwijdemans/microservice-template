package com.wijdemans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/echo")
public class TemplateWebServiceEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(TemplateWebServiceEndpoint.class);
    private Session session;

    public TemplateWebServiceEndpoint() {
        logger.info("Created new upload resource endpoint");
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session; //.getBasicRemote().sendText("onOpen");
    }

    @OnMessage
    public String echo(String msg) {
        return msg + " (from your server)";
    }

    @OnError
    public void onError(Throwable t) {
        logger.debug("Error on websocket connection", t);
    }

    @OnClose
    public void onClose(Session session) {

    }
}
