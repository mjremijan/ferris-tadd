package org.ferris.add.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {

        TvMazeClient client = new TvMazeClient();
        TvMazeParser parser = new TvMazeParser();


//        try (InputStream in = client.downloadSchedule()) {
//            AtomicInteger i = new AtomicInteger(1);
//            parser.streamUpcomingEpisodes(in)
//                .map(a -> a.channel().name())
//                .distinct()
//                .forEach(n ->
//                    System.out.printf(
//                        ", \"%s\" %n",
//                        n
//                    )
//                );
//        }
        
        System.out.printf("%n%n");
        
//        try (InputStream in = client.downloadSchedule()) {
//            AtomicInteger i = new AtomicInteger(1);
//            parser.streamUpcomingEpisodes(in)
//                .sorted(Comparator.comparing(a -> a.episode().airDate()))
//                .forEach(a ->
//                    System.out.printf(
//                        "%04d %s %n",
//                        i.getAndIncrement(),
//                        a
//                    )
//                );
//        }

        System.out.printf("%n%n");
        
        DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("EEEE, MMMM d");
        
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
                
        try (InputStream in = client.downloadSchedule()) {
            parser.streamUpcomingEpisodes(in)
                .collect(Collectors.groupingBy(
                    a -> a.episode().airDate(),
                    TreeMap::new,
                    Collectors.toList())
                ).forEach((date, episodesForDate) -> {

                    out.printf("<p>%s</p>", date.format(formatter));

                    record r(String channel, String show){};
                    out.printf("<p>%n");
                    episodesForDate.stream()
                        .map(a -> new r(a.channel().name(), a.show().name()))
                        .sorted(Comparator.comparing(r -> r.channel().length()))
                        .distinct()
                        .forEach(r -> out.printf("&nbsp;&nbsp;[%s]  %s<br/>", r.channel(), r.show()))
                    ;
                    out.printf("</p>");
                });  
            System.out.println(sw.toString());
            
            
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode req = mapper.createObjectNode();
            req.put("from", "onboarding@resend.dev");
            req.putArray("to").add("mjremijan@yahoo.com");
            req.put("subject", "TV Digest for today");
            req.put("html", sw.toString());
            String body = mapper.writeValueAsString(req);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer xxxxxx")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
            
            HttpClient emailClient = HttpClient.newHttpClient();
            HttpResponse<String> response =
                emailClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
                );

            System.out.println(response.statusCode());
            System.out.println(response.body());
            
        }
    }
}
