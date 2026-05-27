package dk.sdu.cbse.common.services;

import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;

/**
 * Lifecycle interface for game plugins.
 *
 * <p>Implementations are discovered at runtime via the Java ServiceLoader
 * (or a child ModuleLayer) and are responsible for adding and removing
 * their entities from the game world when the plugin starts or stops.</p>
 *
 * <p>Each plugin should manage exactly the entities it owns — no more, no less.</p>
 */
public interface IGamePluginService {

    /**
     * Called once when the plugin is loaded into the game.
     * The implementation should create and register all entities
     * belonging to this plugin into the world.
     *
     * <p><b>Pre-conditions:</b></p>
     * <ul>
     *   <li>{@code gameData} is not null and contains valid display dimensions.</li>
     *   <li>{@code world} is not null.</li>
     * </ul>
     *
     * <p><b>Post-conditions:</b></p>
     * <ul>
     *   <li>All entities owned by this plugin have been added to {@code world}.</li>
     *   <li>{@code gameData} is unchanged.</li>
     * </ul>
     *
     * @param gameData shared game state (screen dimensions, key input, etc.)
     * @param world    the entity container for the current game session
     */
    void start(GameData gameData, World world);

    /**
     * Called once when the plugin is unloaded from the game.
     * The implementation should remove all entities it owns from the world.
     *
     * <p><b>Pre-conditions:</b></p>
     * <ul>
     *   <li>{@code gameData} is not null.</li>
     *   <li>{@code world} is not null.</li>
     *   <li>{@link #start(GameData, World)} has previously been called.</li>
     * </ul>
     *
     * <p><b>Post-conditions:</b></p>
     * <ul>
     *   <li>All entities owned by this plugin have been removed from {@code world}.</li>
     *   <li>No entities belonging to this plugin remain in {@code world}.</li>
     * </ul>
     *
     * @param gameData shared game state
     * @param world    the entity container for the current game session
     */
    void stop(GameData gameData, World world);
}
