package dk.sdu.cbse.scoringservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ScoreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScoreRepository repository;

    @BeforeEach
    void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    void post_score_should_persist_and_be_returned_by_get() throws Exception {
        mockMvc.perform(post("/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"asteroidsDestroyed\":5,\"secondsSurvived\":120}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].asteroidsDestroyed", is(5)))
                .andExpect(jsonPath("$[0].secondsSurvived", is(120)));
    }

    @Test
    void get_scores_returns_empty_list_when_no_scores_exist() throws Exception {
        mockMvc.perform(get("/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void get_scores_returns_at_most_ten_results() throws Exception {
        for (int i = 1; i <= 12; i++) {
            mockMvc.perform(post("/scores")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"asteroidsDestroyed\":" + i + ",\"secondsSurvived\":60}"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    void get_scores_ordered_by_asteroids_destroyed_descending() throws Exception {
        mockMvc.perform(post("/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"asteroidsDestroyed\":3,\"secondsSurvived\":60}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"asteroidsDestroyed\":10,\"secondsSurvived\":60}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].asteroidsDestroyed", is(10)))
                .andExpect(jsonPath("$[1].asteroidsDestroyed", is(3)));
    }

    @Test
    void equal_asteroids_ordered_by_survival_time_descending() throws Exception {
        mockMvc.perform(post("/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"asteroidsDestroyed\":10,\"secondsSurvived\":30}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"asteroidsDestroyed\":10,\"secondsSurvived\":90}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].secondsSurvived", is(90)))
                .andExpect(jsonPath("$[1].secondsSurvived", is(30)));
    }
}
