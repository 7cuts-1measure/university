package freq;

import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage:");
            System.out.println("    freq [input text] [output csv]");
            return;
        }

        try {
            System.out.println("Reading file" + args[0]);
            
            WordStatistics wordStatistics = new WordStatistics(args[0]); /* open file and process */ 

            StatisticsSaver saver = new StatisticsSaver(wordStatistics);
            saver.saveAsCSV(args[1],true); /* saving resukts */
            

            System.out.println("Statistic is saved to " + args[1]);
            System.out.println("Done!");
        } catch (IOException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }
}
