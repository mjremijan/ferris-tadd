package org.ferris.add.main;

import java.io.InputStream;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws Exception {

        TvMazeClient client = new TvMazeClient();
        TvMazeParser parser = new TvMazeParser();

        try (InputStream in = client.downloadSchedule()) {
            AtomicInteger i = new AtomicInteger(1);
            parser.streamUpcomingEpisodes(in)
                    .sorted(Comparator.comparing(EpisodeInfo::airDate))
                    .forEach(ep ->
                            //1485
                            System.out.printf(
                                    "%04d %s %-23s %d %s - s%02de%03d - %s%n",
                                    i.getAndIncrement(),
                                    ep.airDate(),
                                    ep.channelName(),
                                    ep.showId(),
                                    ep.showName(),
                                    ep.season(),
                                    ep.episodeNumber(),
                                    ep.episodeName()
                            ));
        }
    }
}
