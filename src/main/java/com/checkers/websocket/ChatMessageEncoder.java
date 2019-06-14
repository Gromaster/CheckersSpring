package com.checkers.websocket;

import com.checkers.model.messages.ChatMessage;
import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ChatMessageEncoder implements Encoder.Text<ChatMessage>  {

    private static Gson gson = new Gson();

    @Override
    public String encode(ChatMessage chatMessage) throws EncodeException {
        return gson.toJson(chatMessage);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
