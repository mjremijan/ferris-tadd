package org.ferris.tadd.main;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TvMazeClient {

    private static final String broadcastUriTemplate 
        = "https://api.tvmaze.com/schedule?date=%s";
    
    private static final String webUriTemplate 
        = "https://api.tvmaze.com/schedule/web?date=%s";
    
    private static HttpClient client;
    private static DateTimeFormatter formatter;
    
    public TvMazeClient() {
        client = HttpClient.newBuilder().build();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }
    
    public String downloadSchedule(boolean showsForToday, int showsForDays) throws Exception 
    {
        // Get starting date
        LocalDate showDate = (showsForToday) ? LocalDate.now() : LocalDate.now().plusDays(1);

        // Loop showsForDays
        StringBuilder sp = new StringBuilder();
        sp.append("[");
        {
            for (int i=1; i <= showsForDays; i++) {
                if (i > 1) {
                    sp.append("\n,\n");
                }
                sp.append(getShowsForDate(formatter.format(showDate)));
                showDate = showDate.plusDays(1);
            }
        }
        sp.append("]");
        
        return sp.toString();
    }
    
    protected String getShowsForDate(String showDate) throws Exception 
    {         
        return 
            getStrippedJson(broadcastUriTemplate.formatted(showDate))
            +
            "\n,\n"
            +
            getStrippedJson(webUriTemplate.formatted(showDate))
        ;
    }
    
    private String getStrippedJson(String requestUrl) throws Exception 
    {
        System.out.printf("GET %s%n", requestUrl);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(requestUrl))
            .GET()
            .build();

        String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body().trim();

        if (json.startsWith("[") && json.endsWith("]")) {
            return json.substring(1, json.length() - 1);
        } else {
            throw new RuntimeException("JSON string does not start and end with [] characters. \n" + json);
        }
    }
    
//    private static final String fullApiUrl =
//            "https://api.tvmaze.com/schedule/full";
//
//    public String downloadSchedule() throws Exception {
//        
//        String contents = null;
//        
//        // read from file?
//        if (contents == null) {
//            Path path = Path.of("D:\\Desktop\\full.json");
//            if (Files.exists(path)) {
//                contents = Files.readString(path);
//            }
//        }   
//
//        // read from api?
//        if (contents == null) 
//        {
////            HttpClient client = HttpClient.newBuilder()
////                .connectTimeout(Duration.ofMinutes(20))
////                .build()
////            ;
////            
////            HttpRequest request = HttpRequest.newBuilder()
////                .uri(URI.create(URL))
////                .timeout(Duration.ofMinutes(20))
////                .version(HttpClient.Version.HTTP_1_1)
////                .GET()
////                .build();
////
////            HttpResponse<InputStream> response =
////                client.send(request, HttpResponse.BodyHandlers.ofInputStream());
//            
//            Path outPath = Path.of(System.getProperty("java.io.tmpdir"), "tadd-tvmaze-full.json");
//            for (int i=1; i<=3; i++)
//            {
//                URL url = URI.create(fullApiUrl).toURL();                
//                long start = System.nanoTime();
//                try (
//                    InputStream in = url.openStream();
//                    OutputStream out = Files.newOutputStream(
//                        outPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING 
//                    )
//                ) {
//                    byte[] buffer = new byte[8192];
//                    int bytesRead;
//                    int cnt = 0;
//                    while ((bytesRead = in.read(buffer)) != -1) {
//                        System.out.printf("%d Read %d bytes%n", ++cnt, bytesRead);
//                        out.write(buffer, 0, bytesRead);
//                    }
//                    System.out.printf("API call attempt #%d succeeded%n", i);
//                    i = 100;
//                }
//                catch (Exception e) {
//                    System.out.printf("API call attempt #%d failed%n", i);
//                }
//                finally {
//                    Duration elapsed = Duration.ofNanos(System.nanoTime() - start);
//
//                    long minutes = elapsed.toMinutes();
//                    long seconds = elapsed.minusMinutes(minutes).toSeconds();
//
//                    System.out.printf("Elapsed time: %d minutes, %d seconds%n",
//                        minutes,
//                        seconds);    
//                }
//            }
//            
//            contents = Files.readString(outPath);
//        }
//        return contents;
//    }
}
