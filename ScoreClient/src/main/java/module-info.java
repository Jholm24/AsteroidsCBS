module ScoreClient {
    requires CommonScoring;
    requires spring.web;
    requires spring.core;
    requires spring.beans;

    provides dk.sdu.cbse.commonscoring.IScoringService
        with dk.sdu.cbse.scoreclient.ScoringServiceClient;
}
