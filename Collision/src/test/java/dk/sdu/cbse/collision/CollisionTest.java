package dk.sdu.cbse.collision;

import dk.sdu.cbse.common.data.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollisionTest {

    private Collision collision;
    private Entity a;
    private Entity b;

    @BeforeEach
    void setUp() {
        collision = new Collision();
        a = new Entity();
        b = new Entity();
    }

    @Test
    void overlapping_entities_should_collide() {
        a.setX(0); a.setY(0); a.setRadius(10);
        b.setX(5); b.setY(0); b.setRadius(10); // distance=5, sum of radii=20
        assertTrue(collision.isColliding(a, b));
    }

    @Test
    void touching_entities_should_collide() {
        a.setX(0); a.setY(0); a.setRadius(10);
        b.setX(19); b.setY(0); b.setRadius(10); // distance=19, sum=20 → inside threshold
        assertTrue(collision.isColliding(a, b));
    }

    @Test
    void entities_far_apart_should_not_collide() {
        a.setX(0);   a.setY(0);  a.setRadius(10);
        b.setX(100); b.setY(0);  b.setRadius(10); // distance=100, sum=20
        assertFalse(collision.isColliding(a, b));
    }

    @Test
    void entities_exactly_at_sum_of_radii_should_not_collide() {
        a.setX(0);  a.setY(0); a.setRadius(10);
        b.setX(20); b.setY(0); b.setRadius(10); // distance=20, sum=20 → not strictly less than
        assertFalse(collision.isColliding(a, b));
    }

    @Test
    void diagonal_overlap_should_collide() {
        a.setX(0); a.setY(0); a.setRadius(10);
        b.setX(6); b.setY(6); b.setRadius(10); // distance≈8.49, sum=20
        assertTrue(collision.isColliding(a, b));
    }

    @Test
    void same_entity_different_instance_at_same_position_should_collide() {
        a.setX(0); a.setY(0); a.setRadius(10);
        b.setX(0); b.setY(0); b.setRadius(10); // distance=0, sum=20
        assertTrue(collision.isColliding(a, b));
    }

    @Test
    void zero_radius_entities_at_same_position_should_not_collide() {
        a.setX(0); a.setY(0); a.setRadius(0);
        b.setX(0); b.setY(0); b.setRadius(0); // distance=0, sum=0 → not less than
        assertFalse(collision.isColliding(a, b));
    }
}
