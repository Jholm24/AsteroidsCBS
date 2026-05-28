package dk.sdu.cbse.playersystem;

import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.GameKeys;
import dk.sdu.cbse.common.data.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerControlSystemTest {


    @Mock
    private World world;

    private PlayerControlSystem system;
    private Player player;
    private GameData gameData;

    @BeforeEach
    void setUp() {
        system = new PlayerControlSystem();
        gameData = new GameData();

        player = new Player();
        player.setX(100);
        player.setY(100);
        player.setRotation(0);

        // Stub: whenever process() asks world for players, return our test player
        doReturn(List.of(player)).when(world).getEntities(Player.class);
    }

    @Test
    void pressing_W_should_move_player_forward() {
        gameData.getKeys().setKey(GameKeys.W, true);

        system.process(gameData, world);

        // rotation=0° → cos(0)=1, so X increases
        assertTrue(player.getX() > 100, "Player should move right when facing 0° and W is held");
    }

    @Test
    void pressing_A_should_rotate_player_left() {
        double rotationBefore = player.getRotation();
        gameData.getKeys().setKey(GameKeys.A, true);

        system.process(gameData, world);

        assertTrue(player.getRotation() < rotationBefore, "A should rotate counter-clockwise (decreasing angle)");
    }

    @Test
    void pressing_D_should_rotate_player_right() {
        double rotationBefore = player.getRotation();
        gameData.getKeys().setKey(GameKeys.D, true);

        system.process(gameData, world);

        assertTrue(player.getRotation() > rotationBefore, "D should rotate clockwise (increasing angle)");
    }

    @Test
    void no_keys_pressed_player_should_not_move() {
        double xBefore = player.getX();
        double yBefore = player.getY();

        system.process(gameData, world);

        assertEquals(xBefore, player.getX(), "X should not change without input");
        assertEquals(yBefore, player.getY(), "Y should not change without input");
    }

    @Test
    void process_should_always_query_world_for_player_entities() {
        system.process(gameData, world);

        // verify() checks that this method was called exactly once on the mock
        verify(world, times(1)).getEntities(Player.class);
    }

    @Test
    void player_at_left_edge_should_wrap_to_right_side() {
        player.setX(-1); // outside left boundary
        gameData.getKeys().setKey(GameKeys.W, false); // no movement, just boundary check

        // Trigger a boundary correction by moving player off-screen via W+rotation
        player.setRotation(180); // facing left
        gameData.getKeys().setKey(GameKeys.W, true);
        player.setX(0); // right at edge

        // force it past boundary
        player.setX(-1);
        system.process(gameData, world);

        assertTrue(player.getX() >= 0, "Player should be wrapped back inside the left boundary");
    }
}
