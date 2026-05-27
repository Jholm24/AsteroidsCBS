package dk.sdu.cbse.common.services;

import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;

/**
 * Per-tick processing interface for entity logic.
 *
 * <p>Implementations are called once per game loop iteration to update
 * the state of their relevant entities — for example, moving the player,
 * advancing bullets, or steering enemy AI.</p>
 *
 * <p>Processors at this stage should only modify the entities they own.
 * Cross-entity logic (such as collision detection) belongs in
 * {@link IPostEntityProcessingService}.</p>
 */
public interface IEntityProcessingService {

    /**
     * Updates the state of relevant entities for the current game tick.
     *
     * <p><b>Pre-conditions:</b></p>
     * <ul>
     *   <li>{@code gameData} is not null and reflects the current input and display state.</li>
     *   <li>{@code world} is not null and contains all currently active entities.</li>
     * </ul>
     *
     * <p><b>Post-conditions:</b></p>
     * <ul>
     *   <li>All entities managed by this processor have had their position,
     *       rotation, or other state updated for this tick.</li>
     *   <li>No entities owned by other processors have been modified.</li>
     * </ul>
     *
     * @param gameData shared game state (input, screen dimensions, etc.)
     * @param world    the entity container for the current game session
     */
    void process(GameData gameData, World world);
}
