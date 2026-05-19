package dk.sdu.cbse.main;


import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.GameKeys;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.entities.player.IPlayer;
import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.services.IPostEntityProcessingService;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.layout.GridPane;
import javafx.geometry.HPos;

class Game {

    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();
    private final Pane gameWindow = new Pane();
    private final List<IGamePluginService> gamePluginServices;
    private final List<IEntityProcessingService> entityProcessingServiceList;
    private final List<IPostEntityProcessingService> postEntityProcessingServices;
    private final Scoreboard scoreboard = new Scoreboard();
    private AnimationTimer animationTimer;
    private VBox gameOverOverlay;
    private Text destroyedText;
    private Text timerText;
    private long gameStartNano = 0;
    private long elapsedSeconds = 0;
    private static final double HEALTH_BAR_WIDTH = 200;
    private static final double HEALTH_BAR_HEIGHT = 14;
    private Rectangle healthBar;
    private double playerMaxHealth = 0;

    Game(List<IGamePluginService> gamePluginServices, List<IEntityProcessingService> entityProcessingServiceList, List<IPostEntityProcessingService> postEntityProcessingServices) {
        this.gamePluginServices = gamePluginServices;
        this.entityProcessingServiceList = entityProcessingServiceList;
        this.postEntityProcessingServices = postEntityProcessingServices;
    }

    public void start(Stage window) throws Exception {
        destroyedText = new Text(10, 20, "Destroyed asteroids: 0");
        destroyedText.setFill(Color.WHITE);
        timerText = new Text(10, 40, "Time: 0s");
        timerText.setFill(Color.WHITE);
        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        var bgUrl = getClass().getResource("/space.png");
        if (bgUrl != null) {
            BackgroundImage bg = new BackgroundImage(
                new Image(bgUrl.toExternalForm()),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1.0, 1.0, true, true, false, false)
            );
            gameWindow.setBackground(new Background(bg));
        } else {
            gameWindow.setStyle("-fx-background-color: black;");
        }
        gameWindow.getChildren().addAll(destroyedText, timerText);

        Scene scene = new Scene(gameWindow);
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.A)) {
                gameData.getKeys().setKey(GameKeys.A, true);
            }
            if (event.getCode().equals(KeyCode.D)) {
                gameData.getKeys().setKey(GameKeys.D, true);
            }
            if (event.getCode().equals(KeyCode.W)) {
                gameData.getKeys().setKey(GameKeys.W, true);
            }
            if (event.getCode().equals(KeyCode.SPACE)) {
                gameData.getKeys().setKey(GameKeys.SPACE, true);
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.A)) {
                gameData.getKeys().setKey(GameKeys.A, false);
            }
            if (event.getCode().equals(KeyCode.D)) {
                gameData.getKeys().setKey(GameKeys.D, false);
            }
            if (event.getCode().equals(KeyCode.W)) {
                gameData.getKeys().setKey(GameKeys.W, false);
            }
            if (event.getCode().equals(KeyCode.SPACE)) {
                gameData.getKeys().setKey(GameKeys.SPACE, false);
            }

        });

        // Lookup all Game Plugins using ServiceLoader
        for (IGamePluginService iGamePlugin : getGamePluginServices()) {
            iGamePlugin.start(gameData, world);
        }
        for (Entity entity : world.getEntities()) {
            Polygon polygon = new Polygon(entity.getPolygonCoordinates());
            Color c = entity.getColor();
            polygon.setFill(c);
            polygons.put(entity, polygon);
            gameWindow.getChildren().add(polygon);
        }

        // Capture max health from player's initial health value
        for (Entity e : world.getEntities()) {
            if (e instanceof IPlayer) {
                playerMaxHealth = e.getHealth();
                break;
            }
        }

        // Health bar UI (top right)
        double barX = gameData.getDisplayWidth() - HEALTH_BAR_WIDTH - 20;
        Text healthLabel = new Text(barX, 22, "Player health");
        healthLabel.setFill(Color.WHITE);
        healthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Rectangle healthBarBg = new Rectangle(barX, 30, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        healthBarBg.setFill(Color.DARKRED);

        healthBar = new Rectangle(barX, 30, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        healthBar.setFill(Color.RED);

        gameWindow.getChildren().addAll(healthLabel, healthBarBg, healthBar);

        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();
    }

    public void render() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameStartNano == 0) {
                    gameStartNano = now;
                }
                if (gameData.isGameOver()) {
                    stop();
                    showGameOver();
                    return;
                }
                elapsedSeconds = (now - gameStartNano) / 1_000_000_000L;
                update();
                draw();
                gameData.getKeys().update();
            }
        };
        animationTimer.start();
    }

    private void showGameOver() {
        scoreboard.addEntry(gameData.getDestroyedAsteroids(), elapsedSeconds);

        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFill(Color.RED);
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 72));

        Text scoreText = new Text("Asteroids destroyed: " + gameData.getDestroyedAsteroids() + "  |  Time survived: " + elapsedSeconds + "s");
        scoreText.setFill(Color.YELLOW);
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Text boardTitle = new Text("- HIGH SCORES -");
        boardTitle.setFill(Color.WHITE);
        boardTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(6);
        grid.setAlignment(Pos.CENTER);

        List<Scoreboard.Entry> entries = scoreboard.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            Scoreboard.Entry e = entries.get(i);
            Text rank = new Text((i + 1) + ".");
            Text asteroids = new Text(e.asteroidsDestroyed() + " asteroids");
            Text time = new Text(e.secondsSurvived() + "s");
            Color rowColor = (i == 0) ? Color.GOLD : Color.LIGHTGRAY;
            rank.setFill(rowColor);
            asteroids.setFill(rowColor);
            time.setFill(rowColor);
            rank.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            asteroids.setFont(Font.font("Arial", 18));
            time.setFont(Font.font("Arial", 18));
            GridPane.setHalignment(rank, HPos.RIGHT);
            GridPane.setHalignment(asteroids, HPos.LEFT);
            GridPane.setHalignment(time, HPos.RIGHT);
            grid.add(rank, 0, i);
            grid.add(asteroids, 1, i);
            grid.add(time, 2, i);
        }

        Button restartButton = new Button("RESTART");
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        restartButton.setOnAction(e -> restart());

        gameOverOverlay = new VBox(18, gameOverText, scoreText, boardTitle, grid, restartButton);
        gameOverOverlay.setAlignment(Pos.CENTER);
        gameOverOverlay.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        gameOverOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.75);");

        gameWindow.getChildren().add(gameOverOverlay);
    }

    private void restart() {
        for (IGamePluginService plugin : gamePluginServices) {
            plugin.stop(gameData, world);
        }
        new ArrayList<>(world.getEntities()).forEach(world::removeEntity);
        polygons.forEach((entity, polygon) -> gameWindow.getChildren().remove(polygon));
        polygons.clear();
        gameWindow.getChildren().remove(gameOverOverlay);
        gameOverOverlay = null;
        gameData.setGameOver(false);
        gameData.resetDestroyedAsteroids();
        gameData.clearPendingAsteroidSpawns();
        gameStartNano = 0;
        elapsedSeconds = 0;
        for (IGamePluginService plugin : gamePluginServices) {
            plugin.start(gameData, world);
        }
        for (Entity e : world.getEntities()) {
            if (e instanceof IPlayer) {
                playerMaxHealth = e.getHealth();break;
            }
        }
        healthBar.setWidth(HEALTH_BAR_WIDTH);
        animationTimer.start();
    }

    private void update() {
        for (IEntityProcessingService entityProcessorService : getEntityProcessingServices()) {
            entityProcessorService.process(gameData, world);
        }
        for (IPostEntityProcessingService postEntityProcessorService : getPostEntityProcessingServices()) {
            postEntityProcessorService.process(gameData, world);
        }
    }

    private void draw() {
        destroyedText.setText("Destroyed asteroids: " + gameData.getDestroyedAsteroids());
        timerText.setText("Time: " + elapsedSeconds + "s");

        // Update health bar based on current player health percentage
        for (Entity e : world.getEntities()) {
            if (e instanceof IPlayer && playerMaxHealth > 0) {
                double pct = Math.max(0, e.getHealth() / playerMaxHealth);
                healthBar.setWidth(pct * HEALTH_BAR_WIDTH);
                break;
            }
        }

        for (Entity polygonEntity : polygons.keySet()) {
            if (!world.getEntities().contains(polygonEntity)) {

                Polygon removedPolygon = polygons.get(polygonEntity);

                polygons.remove(polygonEntity);
                gameWindow.getChildren().remove(removedPolygon);
            }
        }

        for (Entity entity : world.getEntities()) {
            Polygon polygon = polygons.get(entity);
            if (polygon == null) {
                polygon = new Polygon(entity.getPolygonCoordinates());
                polygon.setFill(entity.getColor());
                polygons.put(entity, polygon);
                gameWindow.getChildren().add(polygon);
            }
            polygon.setTranslateX(entity.getX());
            polygon.setTranslateY(entity.getY());
            polygon.setRotate(entity.getRotation());
            if (entity instanceof IPlayer && entity.isImmune()) {
                polygon.setFill(System.currentTimeMillis() % 200 < 100 ? Color.WHITE : entity.getColor());
            }
        }
    }

    public List<IGamePluginService> getGamePluginServices() {
        return gamePluginServices;
    }

    public List<IEntityProcessingService> getEntityProcessingServices() {
        return entityProcessingServiceList;
    }

    public List<IPostEntityProcessingService> getPostEntityProcessingServices() {
        return postEntityProcessingServices;
    }

}
