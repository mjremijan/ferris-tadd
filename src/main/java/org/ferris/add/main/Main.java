package org.ferris.add.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {

        TvMazeClient client = new TvMazeClient();
        TvMazeParser parser = new TvMazeParser();
        String json = client.downloadSchedule();
        
        DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("EEEE, MMMM d");
        
        DateTimeFormatter formatter2
            = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        
        parser.streamUpcomingEpisodes(json)
            .collect(Collectors.groupingBy(
                a -> a.episode().airDate(),
                TreeMap::new,
                Collectors.toList())
            ).forEach((date, episodesForDate) -> {

                System.out.printf("%n%s%n%n", date.format(formatter));

                record R(String channel, String show){};
                episodesForDate.stream()
                    .map(a -> new R(a.channel().name(), a.show().name()))
                    .sorted(Comparator.comparingInt((R r) -> r.channel().length())
                            .thenComparing(R::channel)
                    )
                    .distinct()
                    .forEach(r -> System.out.printf("  [%s]  %s%n", r.channel(), r.show()))
                ;
            });  
                
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
                
        parser.streamUpcomingEpisodes(json)
            .collect(Collectors.groupingBy(
                a -> a.episode().airDate(),
                TreeMap::new,
                Collectors.toList())
            ).forEach((date, episodesForDate) -> {

                out.printf("<p><b>%s</b></p>", date.format(formatter));

                record R(String channel, String show, String summary){}
                out.printf("<table border=\"1\">%n");
                episodesForDate.stream()
                    .map(a -> new R(a.channel().name(), a.show().name(), a.show().summary()))
                    .sorted(Comparator.comparingInt((R r) -> r.channel().length())
                            .thenComparing(R::channel)
                    )
                    .distinct()
                    .forEach(r -> out.printf("<tr><td style=\"padding: 10px;\">[%s]</td><td style=\"padding: 10px;\"><b>%s</b><br />%s</td></tr>", r.channel(), r.show(), r.summary()))
                ;
                out.printf("</table>");
            });  


        ObjectMapper mapper = new ObjectMapper();
        ObjectNode req = mapper.createObjectNode();
        req.put("from", "onboarding@resend.dev");
        req.putArray("to").add("mjremijan@yahoo.com");
        req.put("subject", "AirDate Digest for " + formatter2.format(LocalDate.now()));
        req.put("html", sw.toString());
        String body = mapper.writeValueAsString(req);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.resend.com/emails"))
            .header("Authorization", "Bearer " + getApiKey())
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

    private static String getApiKey() {
        return "";
    }
}
