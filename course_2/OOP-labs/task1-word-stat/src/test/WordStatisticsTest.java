import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import freq.StatisticsSaver;
import freq.WordStatistics;

public class WordStatisticsTest {
    private int testCount = 0;

    private String CreateTextFile(String content) throws IOException {

        final String filename = "test/cache/test_" + String.valueOf(++testCount) + ".txt";
        File file = new File(filename);

        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs(); 
        }

        try (FileWriter fileOut = new FileWriter(file.getAbsoluteFile())) {
            fileOut.write(content);
        }
        return filename;
    }

    private static List<String> readAllLines(String filename) throws IOException {
        try (Reader reader = new FileReader(filename)) {
            return reader.readAllLines();
        }
    }

    private void test(String content, List<String> expected) throws IOException {
        String filename = CreateTextFile(content);
        String resultFilename = filename + ".csv";

        WordStatistics wordstat = new WordStatistics(filename);
        StatisticsSaver saver = new StatisticsSaver(wordstat);
        saver.saveAsCSV(resultFilename, true);
        
        List<String> result = readAllLines(resultFilename);
        assertEquals(expected, result);
    }

    @Test
    public void lexicographicalWords() throws IOException {
        test("aa ba aa ba ca dd",
                List.of("aa,2,33.33%", "ba,2,33.33%", "ca,1,16.67%", "dd,1,16.67%"));
    }

    @Test
    public void noLexicographicalWords() throws IOException {
        test("ba ba aa aa ca dd",
                List.of("aa,2,33.33%", "ba,2,33.33%", "ca,1,16.67%", "dd,1,16.67%"));
    }

    @Test
    public void emptyInput() throws IOException {
        test("", List.of());
    }

    @Test
    public void startsFromSpace() throws IOException {
        test(" aa dd",
                List.of("aa,1,50.00%", "dd,1,50.00%"));
    }

    @Test
    public void BigFile() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            content.append("word1000\n");
        }
        for (int i = 0; i < 300; i++) {
            content.append("word300\n");
        }
        for (int i = 0; i < 5000; i++) {
            content.append("word5000\n");
        }

        test(content.toString(),
                List.of("word5000,5000,79.37%",
                        "word1000,1000,15.87%",
                        "word300,300,4.76%"));
    }

    @Test
    public void FileNotFoundExcpetion() {
        FileNotFoundException thrown = assertThrows(FileNotFoundException.class,
                () -> {
                    new WordStatistics("ThisFile.Should.Not.Exist.120aaaa42a2d");
                });
    }
}