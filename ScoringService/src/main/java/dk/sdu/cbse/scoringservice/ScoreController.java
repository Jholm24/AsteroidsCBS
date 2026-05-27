package dk.sdu.cbse.scoringservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/scores")
public class ScoreController {

    private final ScoreRepository repository;

    public ScoreController(ScoreRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ScoreRecord> getTopScores() {
        List<ScoreRecord> all = repository.findTopScores();
        return all.size() > 10 ? all.subList(0, 10) : all;
    }

    @PostMapping
    public ResponseEntity<Void> addScore(@RequestBody Map<String, Object> body) {
        int asteroids = (int) body.get("asteroidsDestroyed");
        long seconds = ((Number) body.get("secondsSurvived")).longValue();
        repository.save(new ScoreRecord(asteroids, seconds));
        return ResponseEntity.ok().build();
    }
}
