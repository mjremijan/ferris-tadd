package org.ferris.tadd.main;

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

        System.out.printf("Creating TvMazeClient%n");
        TvMazeClient client = new TvMazeClient();
        
        System.out.printf("Creating TvMazeParser%n");
        TvMazeParser parser = new TvMazeParser();
        
        System.out.printf("Creating Conf%n");
        Config config       = new Config();
        
        DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("EEEE, MMMM d");
        
        DateTimeFormatter formatter2
            = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        
        
        System.out.printf("Downloading schedule%n");
        String json = client.downloadSchedule();
        
        System.out.printf("Logging airings%n");
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
           
        System.out.printf("Creating HTML email message%n");
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        
        String tr = """
        <tr>
            <td style="padding: 10px;">[%s]</td>
            <td>       
                <img src="%s" style="max-height:125px; width:auto;">    
            </td>            
            <td style="padding: 10px;">
                <b>%s</b><br />
                %s
            </td>
        </tr>
        """;
        
        parser.streamUpcomingEpisodes(json)
            .collect(Collectors.groupingBy(
                a -> a.episode().airDate(),
                TreeMap::new,
                Collectors.toList())
            ).forEach((date, episodesForDate) -> {

                out.printf("<p><b>%s</b></p>", date.format(formatter));

                record R(String channel, String show, String summary, String imageUrl){}
                out.printf("<table border=\"1\">%n");
                episodesForDate.stream()
                    .map(a -> new R(a.channel().name(), a.show().name(), a.show().summary(), a.show().imageUrl()))
                    .sorted(Comparator.comparingInt((R r) -> r.channel().length())
                            .thenComparing(R::channel)
                    )
                    .distinct()
                    //.forEach(r -> out.printf("<tr><td style=\"padding: 10px;\">[%s]</td><td style=\"padding: 10px;\"><b>%s</b><br />%s</td></tr>", r.channel(), r.show(), r.summary()))
                    .forEach(r -> out.printf(tr, r.channel(), r.imageUrl().isEmpty() ? "https://upload.wikimedia.org/wikipedia/commons/e/ed/Pix.gif" : r.imageUrl(), r.show(), r.summary()))
                ;
                out.printf("</table>");
            });  

        if (!config.isSendEmail()) {
            System.out.printf("Sending email DISABLED%n");
        } 
        else
        {
            System.out.printf("Sending email%n");
          
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode req = mapper.createObjectNode();
            req.put("from", "onboarding@resend.dev");
            req.putArray("to").add("mjremijan@yahoo.com");
            req.put("subject", "TV AirDate Digest for " + formatter2.format(LocalDate.now()));
            req.put("html", sw.toString());
            String body = mapper.writeValueAsString(req);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + config.getResendApiKey())
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
