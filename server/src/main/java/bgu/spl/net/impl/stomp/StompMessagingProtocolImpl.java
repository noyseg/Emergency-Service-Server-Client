package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.Connections;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocol;

public class StompMessagingProtocolImpl implements StompMessagingProtocol<Frame> {
    private boolean shouldTerminate = false;
    private Connections<Frame> connections;
    int connectionId;

    @Override
    public void start(int connectionId, Connections<Frame> connections) {
        this.connections = connections;
        this.connectionId = connectionId;
    }

    @Override
    public void process(Frame message) {
        Frame response = processingMsg(message);
        /// change
        if (response != null) {
            connections.send(connectionId, response);
        }
        Frame frame = new Frame("RECEIPT");
        frame.addHeader("receipt", ((Frame) message).getHeader("receipt"));
        connections.send(connectionId, frame);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public Frame processingMsg(Frame msg){
        if(((Frame)msg).getType().equals("SUBSCRIBE") | (msg.getType().equals("UNSUBSCRIBE") | (msg.getType().equals("DISCONNECT")){
            return null;
        }
        if(((Frame)msg).getType().equals("SEND")){
            Frame frame = new Frame(("MESSAGE"), msg.getMessageBody());
            return handleSend(msg);
        }
    }

    public Frame handleSend(Frame msg) {
        Frame frame = new Frame("SEND", msg.getMessageBody());

    }

    public String CheckForError(Frame msg) {
        if (msg.getType().equals("SEND")) {
            if (msg.getHeader("destination") == null | msg.getHeader("destination").equals("")) {
                return "Missing destination header";
            }
            if (msg.getMessageBody() == null) {
                return "ERROR\nmessage:body is missing\n\n^@";
            }
        }
        if (msg.getType().equals("SUBSCRIBE")) {
            if (msg.getHeader("id") == null) {
                return "ERROR\nmessage:id header is missing\n\n^@";
            }
            if (msg.getHeader("destination") == null) {
                return "ERROR\nmessage:destination header is missing\n\n^@";
            }
        }
        if (msg.getType().equals("UNSUBSCRIBE")) {
            if (msg.getHeader("id") == null) {
                return "ERROR\nmessage:id header is missing\n\n^@";
            }
        }
        if (msg.getType().equals("DISCONNECT")) {
            if (msg.getHeader("receipt") == null) {
                return "ERROR\nmessage:receipt header is missing\n\n^@";
            }
        }
        return null;
    }
}
