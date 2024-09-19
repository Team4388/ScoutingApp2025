package com.ridgebotics.ridgescout.utility.ollama;

import androidx.annotation.NonNull;

import com.ridgebotics.ridgescout.utility.AlertManager;
import com.ridgebotics.ridgescout.utility.ollama.types.ChatMessage;
import com.ridgebotics.ridgescout.utility.ollama.types.ChatRequest;
import com.ridgebotics.ridgescout.utility.ollama.types.ChatResponse;
import com.ridgebotics.ridgescout.utility.ollama.types.Messages;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.OkHttpClient;

public class OllamaTest {
    private static final String MODEL_KEY = "llama3";
    private static final String OLLAMA_URL_KEY = "http://199.204.135.71:11434";

    private static final String GENERATE_PATH = "/api/generate";

    public interface ollamaListener {
        void onResponse(String response);
        void onComplete();
    }

    private static String promptToJson(String prompt){

        try {
            JSONObject jobj = new JSONObject();
            jobj.put("model", "llama3");
            jobj.put("prompt", prompt);
            return jobj.toString();
        } catch (JSONException e){
            AlertManager.error(e);
        }
        return "{}";
    }

    public static void run(String prompt, ollamaListener listener){
        final Messages llamaMessages = new Messages();
        llamaMessages.messages.add(new Messages.Message("user", "Test!"));
//        ChatRequest chatRequest = new ChatRequest(MODEL_KEY, llamaMessages.messages,true);

        RequestBody body = RequestBody.create(promptToJson(prompt), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(OLLAMA_URL_KEY+GENERATE_PATH)
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                AlertManager.error(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                responseStreamer(response, listener);
            }
        });
    }


    private static void responseStreamer(Response r, ollamaListener listener){
        String line;
        ResponseBody body = r.body();
        if(body == null)
            return;
        try {
            while ((line = body.source().readUtf8Line()) != null) {
//                System.out.println(line);
                JSONObject json = new JSONObject(line);

                listener.onResponse(json.getString("response"));
                if(json.getBoolean("done"))
                    listener.onComplete();
            }
        } catch (IOException | NumberFormatException | IllegalStateException | JSONException e) {
//            addMessage(chatResponse,fullResponse.toString(),stopButton,sendButton);
            AlertManager.error(e);
        }
    }
}
