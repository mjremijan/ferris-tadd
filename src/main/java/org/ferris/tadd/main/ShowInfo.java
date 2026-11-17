package org.ferris.tadd.main;

public record ShowInfo(
          int id
        , String name
        , String language
        , String summary
        , String imageUrl
        , String url
) {
    @Override
    public String toString() {
        return "%s [%d]".formatted(name, id);
    }
}
