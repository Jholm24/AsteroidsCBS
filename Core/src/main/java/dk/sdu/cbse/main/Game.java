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

class Game {

    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();
    private final Pane gameWindow = new Pane();
    private final List<IGamePluginService> gamePluginServices;
    private final List<IEntityProcessingService> entityProcessingServiceList;
    private final List<IPostEntityProcessingService> postEntityProcessingServices;
    private AnimationTimer animationTimer;
    private VBox gameOverOverlay;
    private Text destroyedText;
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
        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        gameWindow.setStyle("-fx-background-color: black;");
        gameWindow.getChildren().add(destroyedText);

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
                if (gameData.isGameOver()) {
                    stop();
                    showGameOver();
                    return;
                }
                update();
                draw();
                gameData.getKeys().update();
            }
        };
        animationTimer.start();
    }

    private void showGameOver() {
        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFill(Color.RED);
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 72));

        Button restartButton = new Button("RESTART");
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        restartButton.setOnAction(e -> restart());

        gameOverOverlay = new VBox(30, gameOverText, restartButton);
        gameOverOverlay.setAlignment(Pos.CENTER);
        gameOverOverlay.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());

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
        for (IGamePluginService plugin : gamePluginServices) {
            plugin.start(gameData, world);
        }
        // Re-capture max health in case it changed, and reset bar to full
        for (Entity e : world.getEntities()) {
            if (e instanceof IPlayer) {
                playerMaxHealth = e.getHealth();
                break;
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
