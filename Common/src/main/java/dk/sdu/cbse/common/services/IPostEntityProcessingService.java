package dk.sdu.cbse.common.services;

import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;

/**
 * Post-tick processing interface for cross-entity logic.
 *
 * <p>Implementations are called after all {@link IEntityProcessingService}
 * processors have run, meaning all entity positions and states are up to date
 * for the current tick. This makes it the correct place for logic that must
 * consider multiple entities at once — primarily collision detection.</p>
 *
 * <p>Typical responsibilities include: detecting collisions between entities,
 * splitting or destroying asteroids, applying damage, and removing dead entities
 * from the world.</p>
 */
public interface IPostEntityProcessingService {

    /**
     * Performs cross-entity processing after all per-entity updates are complete.
     *
     * <p><b>Pre-conditions:</b></p>
     * <ul>
     *   <li>{@code gameData} is not null.</li>
     *   <li>{@code world} is not null and contains all active entities.</li>
     *   <li>All {@link IEntityProcessingService#process(GameData, World)} calls
     *       for this tick have already been completed — entity positions are current.</li>
     * </ul>
     *
     * <p><b>Post-conditions:</b></p>
     * <ul>
     *   <li>All collision effects (damage, splits, removals) have been applied.</li>
     *   <li>Entities with zero or less health have been removed from {@code world}.</li>
     *   <li>Any new entities spawned as a result (e.g. split asteroids) have been
     *       added to {@code world}.</li>
     * </ul>
     *
     * @param gameData shared game state
     * @param world    the entity container for the current game session
     */
    void process(GameData gameData, World world);
}
