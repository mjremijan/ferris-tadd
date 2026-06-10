# Ferris Tv AirDate Digest (TADD)

Downloads the TVMaze full schedule, filters episodes airing today through two weeks ahead,
extracts selected fields, sorts by air date, and prints results.

Build:

mvn clean package

Run:

mvn exec:java -Dexec.mainClass=org.ferris.add.main.Main
