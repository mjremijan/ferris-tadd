package org.ferris.add.main;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.ferris.add.main.ChannelInfo.Type;

public class TvMazeParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public Stream<Airing> streamUpcomingEpisodes(InputStream jsonStream) throws Exception {

        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(jsonStream);

        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new IllegalStateException("Expected JSON array");
        }

        Stream.Builder<Airing> builder = Stream.builder();

        while (parser.nextToken() == JsonToken.START_OBJECT) {
            // ROOT {}
            JsonNode episode = mapper.readTree(parser);
            if (episode == null || episode.isNull()) {
                continue;
            }
            // ROOT _embedded>>show
            JsonNode show = episode.path("_embedded").path("show");
            if (show == null || show.isMissingNode()) {
                continue;
            }
            
            // episode info
            EpisodeInfo episodeInfo = parseEpisodeInfo(episode);
            if (episodeInfo == null) {
                continue;
            }
            
            // show info
            ShowInfo showInfo = parseShowInfo(show);
            if (showInfo == null) {
                continue;
            }
            
            // channel info
            ChannelInfo channelInfo = parseChannelInfo(show);
            if (channelInfo == null) {
                continue;
            }
            
            // airing
            Airing airing = Airing.build(channelInfo, showInfo, episodeInfo);
            if (airing == null) {
                continue;
            }
            builder.add(airing);
        }

        return builder.build();
    }
    private String asString(JsonNode node, String path) {
        String s = node.path(path).asText(null);
        return (s == null) ? "" : s;
    }
    
    private String asStringRequired(JsonNode node, String path) {
        String s = asString(node, path);
        if (s == null || s.isEmpty()) {
            throw new RuntimeException(
                 "text missing for node \"%s\" and path \"%s\"".formatted(node, path)
            );
        }
        return s;
    }
    
    private int asIntRequired(JsonNode node, String path) {
        String s = node.path(path).asText(null);
        if (s == null) {
            throw new RuntimeException(
                "text null for node \"%s\" and path \"%s\"".formatted(node, path)
            );
        }
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
           throw new RuntimeException(
                "text NaN for node \"%s\" and path \"%s\"".formatted(node, path)
            ); 
        }
    }
    private int asInt(JsonNode node, String path, int defval) {
        int retval = defval;
        try {
            retval = asIntRequired(node, path);
        } catch (Exception ignore) {}
        return retval;
    }

    private EpisodeInfo parseEpisodeInfo(JsonNode episode) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate cutoff = tomorrow.plusDays(2);
        
        // air date
        String airDateText = asStringRequired(episode, "airdate");
        LocalDate airDate = LocalDate.parse(airDateText);
        if (airDate.isBefore(tomorrow) || airDate.isAfter(cutoff)) {
            return null;
        }

        // return
        return new EpisodeInfo(
              asIntRequired(episode, "id")
            , asStringRequired(episode, "name")
            , asInt(episode, "season", 0)
            , asInt(episode, "number", 0)
            , airDate
        );
    }

    private ShowInfo parseShowInfo(JsonNode show) {
        return new ShowInfo(
              asIntRequired(show, "id")
            , asStringRequired(show, "name")
            , asString(show, "language")
        );
    }

    private ChannelInfo parseChannelInfo(JsonNode show) {
        Type type;
        int id;
        String name;
        String country;
        
        // Type
        type = null; 
        JsonNode channelNode = NullNode.getInstance();
        {
            if (channelNode.isMissingNode() || channelNode.isNull()) {
                channelNode = show.path("network");
                type = Type.NETWORK;
            }
            if (channelNode.isMissingNode() || channelNode.isNull()) {
                channelNode = show.path("webChannel");
                type = Type.WEB;
            }
            if (channelNode.isMissingNode() || channelNode.isNull()) {
                throw new RuntimeException(
                    "both network and webChannel are missing for node %s".formatted(show)
                );   
            }
        }
          
        // id
        id = asIntRequired(channelNode, "id");
        
        // name
        name = asStringRequired(channelNode, "name");
        if (ChannelInfo.exclude.contains(name)) {
            return null;
        }
        
        // country
        country = ""; JsonNode countryNode = channelNode.path("country");
        if (!countryNode.isMissingNode()) {
            country = asString(countryNode, "code");
        }

        // return
        return new ChannelInfo(
              type
            , id
            , name
            , country
        );
    }
}
