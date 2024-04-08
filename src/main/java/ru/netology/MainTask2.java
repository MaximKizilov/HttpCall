package ru.netology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class MainTask2 {
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=ieTZ9ahI07X6ePw1yXXAs0rATkaQZxrJPiqlAMpw");
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            PostNasa postNasa = mapper.readValue(
                    response.getEntity().getContent(), new
                            TypeReference<>() {
                            });
            System.out.println(postNasa.toString());
            createFile(postNasa);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void createFile(PostNasa postNasa) {
        File file = new File("src//main//resources//Hello.jpg");
        try {
            if (file.createNewFile())
                System.out.println("Файл был создан");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        try (BufferedInputStream in = new BufferedInputStream(new URL(postNasa.getUrl()).openStream());
             FileOutputStream out = new FileOutputStream(file)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
