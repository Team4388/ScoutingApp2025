package com.ridgebotics.ridgescout.utility.ollama.types;
// Credit to https://github.com/DataDropp/OllamaDroid

import java.util.ArrayList;

public class Messages {
    public ArrayList<Message> messages = new ArrayList<>();
    public static class Message {
        public String role;
        public String content;
        public Message(String role, String string) {
            this.role = role;
            this.content = string;

        }
    }
}