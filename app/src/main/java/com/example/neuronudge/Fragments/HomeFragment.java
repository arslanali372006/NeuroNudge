package com.example.neuronudge.Fragments;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.neuronudge.R;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private TextView gptResponseBox;
    private EditText gptPromptInput;
    private Button sendButton;
    private ProgressBar loadingIndicator;

    private final OkHttpClient client = new OkHttpClient();

    private static final String API_KEY = "AIzaSyCwkYy8pBibPFA-kna2nZpn1fnThvmT6cE";  // <-- Replace with your actual API key
    private static final String MODEL_NAME = "gemini-2.0-pro-vision-latest";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_NAME + ":generateContent?key=" + API_KEY;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        gptResponseBox = view.findViewById(R.id.gptResponseBox);
        gptPromptInput = view.findViewById(R.id.gptPromptInput);
        sendButton = view.findViewById(R.id.sendButton);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);

        sendButton.setOnClickListener(v -> {
            String prompt = gptPromptInput.getText().toString().trim();
            if (!prompt.isEmpty()) {
                loadingIndicator.setVisibility(View.VISIBLE);
                gptResponseBox.setText(""); // Clear previous response
                sendPromptToGemini(prompt);
            }
        });

        return view;
    }

    private void sendPromptToGemini(String prompt) {
        new Thread(() -> {
            try {
                JSONObject jsonBody = new JSONObject();
                JSONObject contentObj = new JSONObject();
                JSONObject partObj = new JSONObject();

                partObj.put("text", prompt);

                // parts is an array containing partObj
                contentObj.put("parts", new JSONArray().put(partObj));

                // contents is an array containing contentObj
                jsonBody.put("contents", new JSONArray().put(contentObj));

                RequestBody body = RequestBody.create(
                        jsonBody.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                String apiKey = "AIzaSyCwkYy8pBibPFA-kna2nZpn1fnThvmT6cE";  // replace with your key
                String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

                Request request = new Request.Builder()
                        .url(apiUrl)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

                JSONObject jsonResponse = new JSONObject(responseData);
                String reply = jsonResponse
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");

                requireActivity().runOnUiThread(() -> {
                    gptResponseBox.setText(reply);
                    loadingIndicator.setVisibility(View.GONE);
                });

            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    gptResponseBox.setText("Error: " + e.getMessage());
                    loadingIndicator.setVisibility(View.GONE);
                });
            }
        }).start();
    }


}

