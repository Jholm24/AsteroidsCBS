package dk.sdu.cbse.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends Application {

    public static void main(String[] args) {
        launch(Main.class);
    }

    @Override
    public void start(Stage window) throws Exception {
        startScoringService();

        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(dk.sdu.cbse.main.ModuleConfig.class);

        dk.sdu.cbse.main.Game game = ctx.getBean(dk.sdu.cbse.main.Game.class);
        game.start(window);
        game.render();
    }

    private void startScoringService() {
        Path jar = Path.of(System.getProperty("user.dir"), "services", "ScoringService-1.0.1-SNAPSHOT.jar");
        if (!Files.exists(jar)) {
            System.out.println("ScoringService JAR not found at " + jar + ", scores will not be saved.");
            return;
        }
        try {
            new ProcessBuilder("java", "-jar", jar.toString())
                    .redirectErrorStream(true)
                    .start();
            System.out.println("ScoringService started, waiting for it to be ready...");
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println("Could not start ScoringService: " + e.getMessage());
        }
    }
}
