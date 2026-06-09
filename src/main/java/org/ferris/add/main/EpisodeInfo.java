package org.ferris.add.main;

import java.time.LocalDate;

public record EpisodeInfo(
          int id
        , String name
        , int season
        , int episode
        , LocalDate airDate
) {
    @Override
    public String toString() {
        return "s%02de%03d - %s [%d]".formatted(season, episode, name, id);
    }
}
