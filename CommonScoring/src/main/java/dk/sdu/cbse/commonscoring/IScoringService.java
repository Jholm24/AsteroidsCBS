package dk.sdu.cbse.commonscoring;

import java.util.List;

public interface IScoringService {
    void addScore(int asteroidsDestroyed, long secondsSurvived);
    List<ScoreEntry> getTopScores();
}
