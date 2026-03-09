package freq;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WordStatistics {
    private Map<String, Integer> map = new HashMap<String, Integer>();

    private int totalWords = 0;

    /**
     * Creates the map from input
     * 
     * @param reader
     * @return map, where the key is a word, value is a number of such words that
     *         reader gets
     * @throws IOExceptions
     */
    private void collectStatistics(Reader reader) throws IOException {
        totalWords = 0;

        final int BUFFER_SIZE = 512;
        char[] buffer = new char[BUFFER_SIZE];

        StringBuilder word = new StringBuilder();
        int cnt;

        while ((cnt = reader.read(buffer)) != -1) {
            if (cnt < BUFFER_SIZE) {
                buffer = Arrays.copyOf(buffer, cnt);
            }
            for (char c : buffer) {
                if (Character.isLetterOrDigit(c)) {
                    word.append(c);
                } else if (!word.isEmpty()) { // separator
                    map.merge(new String(word), 1, Integer::sum); // +1 for counting
                    word.setLength(0);
                    totalWords++;
                }
            }
        }
        if (!word.isEmpty()) {
            map.merge(new String(word), 1, Integer::sum); // +1 for counting
            word.setLength(0);
            totalWords++;
        }
    }
    

    /**
     * Processes the input file and by the time the object is created, all
     * statistics are ready
     * 
     * @param filename - input file for analysing
     * @throws IOException if has problems while reading file
     */
    public WordStatistics(String filename) throws IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(filename))) {
            collectStatistics(reader);
        }

    }

    public Map<String, Integer> getStatisticsMap() {
        return map;
    }

    public int getTotalWords() {
        return totalWords;
    }
}
