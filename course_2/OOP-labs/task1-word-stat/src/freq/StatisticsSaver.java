package freq;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class StatisticsSaver {
        /**
     * Saves statistic in format: word,frequency,frequency_in_percents
     * 
     * @param filename - ouptut file
     * @throws IOException
     */

    private WordStatistics wordStatistics;


    public StatisticsSaver(WordStatistics wordStatistics) {
        this.wordStatistics = wordStatistics;
    }

    public void saveAsCSV(String filename, boolean sorted) throws IOException {
        Map<String, Integer> statMap = wordStatistics.getStatisticsMap();
        List<Entry<String, Integer>> entries = new ArrayList<>(statMap.entrySet());

        if (sorted) { 
            entries.sort(Entry.<String, Integer>comparingByValue().reversed()
                .thenComparing(Entry.comparingByKey()));
        }
        
        try (FileWriter fileWriter = new FileWriter(filename);
                BufferedWriter bufferOut = new BufferedWriter(fileWriter)) {
            for (Entry<String, Integer> pair : entries) {
                String line = String.format(Locale.ROOT, "%s,%d,%.2f%%\n",
                        pair.getKey(),
                        pair.getValue(),
                        pair.getValue() / (float) wordStatistics.getTotalWords() * 100);

                bufferOut.append(line);
            }
        }
    }
}
