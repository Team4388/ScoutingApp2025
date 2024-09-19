package com.ridgebotics.ridgescout.utility.ollama.types;
// Credit to https://github.com/DataDropp/OllamaDroid



import java.util.ArrayList;

public class ChatRequest {
    public String model;
    public ArrayList<Messages.Message> messages;
    public boolean stream;
    public ChatRequest(String model, ArrayList<Messages.Message> message, boolean stream){
        this.model = model;
        this.messages = message;
        this.stream = stream;
    }
    public String toJson(){
        return "{\n" +
                "  \"model\": \"llama3\",\n" +
                "  \"prompt\": \"Why is the sky blue?\"\n" +
                "}";
    }
}