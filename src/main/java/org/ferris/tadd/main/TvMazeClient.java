package org.ferris.tadd.main;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

public class TvMazeClient {

    private static final String fullApiUrl =
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
        if (contents == null) 
        {
//            HttpClient client = HttpClient.newBuilder()
//                .connectTimeout(Duration.ofMinutes(20))
//                .build()
//            ;
//            
//            HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(URL))
//                .timeout(Duration.ofMinutes(20))
//                .version(HttpClient.Version.HTTP_1_1)
//                .GET()
//                .build();
//
//            HttpResponse<InputStream> response =
//                client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            
            Path outPath = Path.of(System.getProperty("java.io.tmpdir"), "tadd-tvmaze-full.json");
            for (int i=1; i<=3; i++)
            {
                URL url = URI.create(fullApiUrl).toURL();                
                long start = System.nanoTime();
                try (
                    InputStream in = url.openStream();
                    OutputStream out = Files.newOutputStream(
                        outPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING 
                    )
                ) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    int cnt = 0;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        System.out.printf("%d Read %d bytes%n", ++cnt, bytesRead);
                        out.write(buffer, 0, bytesRead);
                    }
                    System.out.printf("API call attempt #%d succeeded%n", i);
                    i = 100;
                }
                catch (Exception e) {
                    System.out.printf("API call attempt #%d failed%n", i);
                }
                finally {
                    Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

                    long minutes = elapsed.toMinutes();
                    long seconds = elapsed.minusMinutes(minutes).toSeconds();

                    System.out.printf("Elapsed time: %d minutes, %d seconds%n",
                        minutes,
                        seconds);    
                }
            }
            
            contents = Files.readString(outPath);
        }
        return contents;
    }
}
