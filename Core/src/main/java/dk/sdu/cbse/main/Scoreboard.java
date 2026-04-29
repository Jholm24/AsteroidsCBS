package dk.sdu.cbse.main;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Scoreboard {

    record Entry(int asteroidsDestroyed, long secondsSurvived) {}

    private static final String FILE_PATH = System.getProperty("user.home") + "/asteroids_scores.csv";
    private static final int MAX_ENTRIES = 10;

    private final List<Entry> entries = new ArrayList<>();

    Scoreboard() {
        load();
    }

    void addEntry(int asteroidsDestroyed, long secondsSurvived) {
        entries.add(new Entry(asteroidsDestroyed, secondsSurvived));
        entries.sort(Comparator.comparingInt(Entry::asteroidsDestroyed)
                .thenComparingLong(Entry::secondsSurvived)
                .reversed());
        if (entries.size() > MAX_ENTRIES) {
            entries.subList(MAX_ENTRIES, entries.size()).clear();
        }
        save();
    }

    List<Entry> getEntries() {
        return List.copyOf(entries);
    }

    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    entries.add(new Entry(Integer.parseInt(parts[0].trim()), Long.parseLong(parts[1].trim())));
                }
            }
            entries.sort(Comparator.comparingInt(Entry::asteroidsDestroyed)
                    .thenComparingLong(Entry::secondsSurvived)
                    .reversed());
        } catch (IOException | NumberFormatException ignored) {}
    }

    private void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Entry e : entries) {
                writer.println(e.asteroidsDestroyed() + "," + e.secondsSurvived());
            }
        } catch (IOException ignored) {}
    }
}
