package org.ferris.add.main;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class TvMazeClient {

    private static final String URL =
            "https://api.tvmaze.com/schedule/full";

    public InputStream downloadSchedule() throws Exception {
        
        InputStream contents = null;
        
        // read from file?
        if (contents == null) {
            Path path = Path.of("D:\\Desktop\\full.json");
            if (Files.exists(path)) {
                contents = Files.newInputStream(path);
            }
        }   

        // read from api?
        if (contents == null) {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .GET()
                    .build();

            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            
            contents = response.body();
        }
        return contents;
    }
}
