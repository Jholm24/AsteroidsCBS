package dk.sdu.cbse.scoreclient;

import dk.sdu.cbse.commonscoring.IScoringService;
import dk.sdu.cbse.commonscoring.ScoreEntry;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoringServiceClient implements IScoringService {

    private static final String BASE_URL = "http://localhost:8080";
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void addScore(int asteroidsDestroyed, long secondsSurvived) {
        try {
            String json = "{\"asteroidsDestroyed\":" + asteroidsDestroyed +
                          ",\"secondsSurvived\":" + secondsSurvived + "}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.postForEntity(BASE_URL + "/scores", new HttpEntity<>(json, headers), String.class);
        } catch (Exception e) {
            System.out.println("ScoringService unavailable, score not saved: " + e.getMessage());
        }
    }

    @Override
    public List<ScoreEntry> getTopScores() {
        try {
            String response = restTemplate.getForObject(BASE_URL + "/scores", String.class);
            return parseScores(response);
        } catch (Exception e) {
            System.out.println("ScoringService unavailable, cannot load scores: " + e.getMessage());
            return List.of();
        }
    }

    private List<ScoreEntry> parseScores(String json) {
        List<ScoreEntry> result = new ArrayList<>();
        if (json == null || json.isBlank()) return result;
        Pattern p = Pattern.compile("\"asteroidsDestroyed\":(\\d+),\"secondsSurvived\":(\\d+)");
        Matcher m = p.matcher(json);
        while (m.find()) {
            result.add(new ScoreEntry(Integer.parseInt(m.group(1)), Long.parseLong(m.group(2))));
        }
        return result;
    }
}
