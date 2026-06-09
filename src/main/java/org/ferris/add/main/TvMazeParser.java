package org.ferris.add.main;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.ferris.add.main.EpisodeInfo.ChannelType;

public class TvMazeParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public Stream<EpisodeInfo> streamUpcomingEpisodes(InputStream jsonStream) throws Exception {

        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusWeeks(2);

        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(jsonStream);

        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new IllegalStateException("Expected JSON array");
        }

        Stream.Builder<EpisodeInfo> builder = Stream.builder();

        while (parser.nextToken() == JsonToken.START_OBJECT) {

            // ROOT episode
            JsonNode episode = mapper.readTree(parser);
            if (episode == null) {
                continue;
            }
            // ROOT show
            JsonNode show = episode.path("_embedded").path("show");
            if (show == null) {
                continue;
            }
            
            // show id
            int showId = show.path("id").asInt();
            
            // show name
            String showName = show.path("name").asText();
            
            // language
            String language = show.path("language").asText(null);
            if (language == null) {
                throw new RuntimeException("Show id %d has no language".formatted(showId));
            } 
            else 
            if (!"English".equalsIgnoreCase(language)) {
                continue;
            }
            
            // channel name
            // channel type
            String channelName = null;
            ChannelType channelType = null;
            JsonNode network = show.path("network");
            if (!network.isNull()) {
                channelName = network.path("name").asText(null);
                channelType = ChannelType.NETWORK; 
                JsonNode country = network.path("country");
                if (!country.isNull()) {
                    String countryCode = country.path("code").asText("");
                    if (!"US".equalsIgnoreCase(countryCode)) {
                        continue;
                    }
                }
            }
            if (channelName == null || channelName.isBlank()) {
                JsonNode webChannel = show.path("webChannel");
                if (!webChannel.isNull()) {
                    channelName = webChannel.path("name").asText(null);
                    channelType = ChannelType.WEB;
                }
            }
            if (isUnknown(channelName)) {
                continue;
            }
            
            // episode name
            // season
            // episode number
            String episodeName = episode.path("name").asText(null);
            if (isUnknown(episodeName)) {
                continue;
            }
            int season = episode.path("season").asInt();
            int episodeNumber = episode.path("number").asInt();

            // air date
            String airDateText = episode.path("airdate").asText(null);
            if (airDateText == null || airDateText.isBlank()) {
                continue;
            }
            LocalDate airDate = LocalDate.parse(airDateText);
            if (airDate.isBefore(today) || airDate.isAfter(cutoff)) {
                continue;
            }

            
            
            builder.add(new EpisodeInfo(
                    showName,
                    showId,
                    episodeName,
                    season,
                    episodeNumber,
                    channelName,
                    airDate));
        }

        return builder.build();
    }
    
    private boolean isUnknown(String s) {
        return (s != null && (s.startsWith("?") && s.endsWith("?")));
    }
}
