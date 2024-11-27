public class WallCollisionEvent extends Event {
    private Particle _p;
    private boolean _vertical;

    /**
     * Constructor for the WallCollisionEvent class.
     * @param timeOfEvent the time of the event
     * @param timeEventCreated the time the event was created
     * @param p the particle involved in the collision
     * @param vertical true if the collision is with a vertical wall, false if it is with a horizontal wall
     */
    public WallCollisionEvent (double timeOfEvent, double timeEventCreated, Particle p, boolean vertical) {
        super(timeOfEvent, timeEventCreated);
        _p = p;
        _vertical = vertical;
    }

    /**
     * Updates the velocity of the particle after the collision.
     */
    public void update () {
        if (_vertical) {
            _p.updateAfterVerticalWallCollision(_timeOfEvent);
        } else {
            _p.updateAfterHorizontalWallCollision(_timeOfEvent);
        }
    }
}
