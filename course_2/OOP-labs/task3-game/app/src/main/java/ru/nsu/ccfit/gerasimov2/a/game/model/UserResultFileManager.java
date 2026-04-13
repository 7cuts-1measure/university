package ru.nsu.ccfit.gerasimov2.a.game.model;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserResultFileManager {
    public static final String FILE_PATH = "data.json";
    public static final int MAX_USERS = 20;
    public static final int TOP_USERS = 3;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void save(UserResult ur) throws IOException {
        File file = new File(FILE_PATH);
        System.out.println("SaveFile: " + file.getAbsolutePath());
        List<UserResult> results;

        if (file.exists() && file.length() > 0) {
            results = objectMapper.readValue(file, new TypeReference<List<UserResult>>() {});
        } else {
            results = new ArrayList<>();
        }

        if (results.size() >= MAX_USERS) {
            results.subList(TOP_USERS, results.size()).clear();
        } 

        results.add(ur);
        results.sort(new Comparator<UserResult>() {
        @Override
            public int compare(UserResult o1, UserResult o2) {
                return o2.score - o1.score;
            }
        });
        objectMapper.writeValue(file, results);
    }

    public List<UserResult> load() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(file, new TypeReference<List<UserResult>>() {});
    }
}
