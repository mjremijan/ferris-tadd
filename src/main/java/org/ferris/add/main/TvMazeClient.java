package org.ferris.add.main;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class TvMazeClient {

    private static final String URL =
            "https://api.tvmaze.com/schedule/full";

    public String downloadSchedule() throws Exception {
        
        String contents = null;
        
        // read from file?
        if (contents == null) {
            Path path = Path.of("D:\\Desktop\\full.json");
            if (Files.exists(path)) {
                contents = Files.readString(path);
            }
        }   

        // read from api?
        if (contents == null) {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            
            contents = response.body();
        }
        return contents;
    }
}
