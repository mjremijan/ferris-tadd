package org.ferris.add.main;

public record Airing(
      ChannelInfo channel
    , ShowInfo show
    , EpisodeInfo episode
) {
    @Override
    public String toString() {
        return "%s %-25s: %s - %s".formatted(episode.airDate(), channel, show, episode);
    }
    
    public static Airing build(
          ChannelInfo channel
        , ShowInfo show
        , EpisodeInfo episode
    ) {
        Airing retval = null;
        boolean buildAiring;
        
        
        if (
                // Country
                (
                    "US".equalsIgnoreCase(channel.country())
                    ||
                    "".equalsIgnoreCase(channel.country())
                )
                
                &&
                
                // Language
                (
                    "English".equalsIgnoreCase(show.language())
                    ||
                    "".equalsIgnoreCase(show.language())
                )
        ) {
            retval = new Airing(channel, show, episode);
        }
        
        return retval;
    }
}
