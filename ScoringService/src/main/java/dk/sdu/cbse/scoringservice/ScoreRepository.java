package dk.sdu.cbse.scoringservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ScoreRepository extends JpaRepository<ScoreRecord, Long> {

    @Query("SELECT s FROM ScoreRecord s ORDER BY s.asteroidsDestroyed DESC, s.secondsSurvived DESC")
    List<ScoreRecord> findTopScores();
}
