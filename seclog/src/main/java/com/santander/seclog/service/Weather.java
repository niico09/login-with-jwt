package com.santander.seclog.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class Weather {

    @Value("${santander.longAndLat}")
    String lonAndLat;

    @Value("${santander.apiKey}")
    String apiKey;

    //Format example to send the hour: 2020-10-19T18:46:19
    public int getTemperatureDay(final String hour) throws IOException, InterruptedException {

        int temp=0;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://rapidapi.p.rapidapi.com/" + lonAndLat + "," + hour ))
                .header("x-rapidapi-host", "dark-sky.p.rapidapi.com")
                .header("x-rapidapi-key", apiKey)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject jsonObject = new Gson().fromJson(response.body(), JsonObject.class);

        if(jsonObject.get("currently").getAsJsonObject().get("temperature") != null){
            temp = jsonObject.get("currently").getAsJsonObject().get("temperature").getAsInt();
        }

        return temp;
    }



}
