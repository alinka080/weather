package org.example;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final String API_KEY = "244d7a0eb22b5e0d3aa345d6a25591fe";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Scanner scanner =new Scanner(System.in);
        while (true) {
            System.out.print("Введите город: ");
            final String city = scanner.nextLine();
            if(city.equals("Выход")){
                System.out.println("Выход из программы.");
                break;
            }

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    String urlString = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric&lang=ru";
                    URL url = new URL(urlString);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 404) {
                        System.out.println("ERROR 404 \nГород не найден. Пожалуйста, попробуйте еще раз.");
                        return;
                    }
                    if (responseCode == 400) {
                        System.out.println("ERROR 400 \nЗапрос неправильно сформирован. Пожалуйста, попробуйте еще раз.");
                        return;
                    }
                    if (responseCode == 401) {
                        System.out.println("ERROR 401 \nПроблемы с аутентификацией. Пожалуйста, попробуйте еще раз.");
                        return;
                    }
                    if (responseCode == 500) {
                        System.out.println("ERROR 401 \nОшибка на сервере, которая может указывать на\n" +
                                "временные проблемы с API. Пожалуйста, попробуйте еще раз.");
                        return;
                    }


                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();

                    JSONObject jsonResponse = new JSONObject(response.toString());

                    JSONObject main = jsonResponse.getJSONObject("main");
                    JSONObject weather = jsonResponse.getJSONArray("weather").getJSONObject(0);
                    JSONObject wind = jsonResponse.getJSONObject("wind");

                    String description = weather.getString("description");
                    double temperature = main.getDouble("temp");
                    int humidity = main.getInt("humidity");
                    double speed = wind.getDouble("speed");

                    System.out.println("Погода в " + city + ":");
                    System.out.println("Описание: " + description);
                    System.out.println("Температура: " + temperature + "C");
                    System.out.println("Влажность: " + humidity + "%");
                    System.out.println("Скорость ветра: " + speed + " м/с");
                    System.out.println("Для выхода из программы напишите слово Выход");

                    // System.out.println(response);

                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            });

            future.get();
        }
    }
}