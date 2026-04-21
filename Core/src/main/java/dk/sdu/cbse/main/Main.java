package dk.sdu.cbse.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main extends Application {



    public static void main(String[] args) {
        launch(Main.class);
    }

    @Override
    public void start(Stage window) throws Exception {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(dk.sdu.cbse.main.ModuleConfig.class);

        for (String beanName : ctx.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }

        dk.sdu.cbse.main.Game game = ctx.getBean(dk.sdu.cbse.main.Game.class);
        game.start(window);
        game.render();

    }

}
