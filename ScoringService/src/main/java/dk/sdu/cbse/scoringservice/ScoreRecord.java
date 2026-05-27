package dk.sdu.cbse.scoringservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ScoreRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int asteroidsDestroyed;
    private long secondsSurvived;

    public ScoreRecord() {}

    public ScoreRecord(int asteroidsDestroyed, long secondsSurvived) {
        this.asteroidsDestroyed = asteroidsDestroyed;
        this.secondsSurvived = secondsSurvived;
    }

    public Long getId() { return id; }
    public int getAsteroidsDestroyed() { return asteroidsDestroyed; }
    public long getSecondsSurvived() { return secondsSurvived; }
    public void setAsteroidsDestroyed(int v) { this.asteroidsDestroyed = v; }
    public void setSecondsSurvived(long v) { this.secondsSurvived = v; }
}
