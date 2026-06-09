package org.ferris.add.main;

import java.util.List;

public record ChannelInfo(
          Type type
        , int id
        , String name
        , String country
) {
    public enum Type {
        NETWORK,
        WEB
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static List<String> exclude = List.of(
          "MS NOW" 
        , "Fox Business Network" 
        , "Newsmax" 
        , "NewsNation" 
        //, "ABC" 
        //, "NBC" 
        //, "CBS" 
        //, "Prime Video" 
        //, "Paramount+" 
        //, "Tubi" 
        , "YouTube" 
        , "NFL Network" 
        , "SkyShowtime" 
        , "PokerGO" 
        , "ESPN" 
        , "CBS News" 
        , "Syndication" 
        , "Hulu" 
        //, "NBC.com" 
        //, "Peacock" 
        , "Game Show Network" 
        , "ABC News Live" 
        //, "The CW" 
        , "HGTV" 
        //, "FOX" 
        , "Bravo" 
        , "Discovery" 
        , "TLC" 
        //, "PBS" 
        , "CNN" 
        , "Magnolia Network" 
        , "Food Network" 
        , "Investigation Discovery" 
        , "A&E" 
        //, "History" 
        , "Vice TV" 
        , "Comedy Central" 
        //, "Apple TV" 
        , "Dropout" 
        //, "Disney+" 
        //, "Netflix" 
        , "Nebula" 
        , "TBS" 
        , "BET" 
        , "WWE Network" 
        //, "HBO Max" 
        , "Beacon" 
        , "AMC+" 
        , "ALLBLK" 
        , "Twitch" 
        , "MTV" 
        //, "FX" 
        , "UP TV" 
        , "AMC" 
        , "National Geographic" 
        , "Nickelodeon" 
        , "Fox Nation" 
        , "USA Network" 
        , "WE tv" 
        , "STARZ" 
        , "Oprah Winfrey Network" 
        , "REELZ" 
        //, "HBO" 
        , "Cartoon Network" 
        , "TNT" 
        //, "Me-TV" 
        , "Fox News Channel" 
        , "Adult Swim" 
        , "Ovation" 
        , "StardomWorld" 
        , "MGM+" 
        , "Zeus" 
        , "Oxygen True Crime" 
        //, "PBS Passport" 
        , "Hallmark Channel" 
        , "Acorn TV" 
        , "TV One" 
    );
    
}
