public class WallCollisionEvent extends Event {
    private Particle _p;
    private boolean _vertical;

    public WallCollisionEvent (double timeOfEvent, double timeEventCreated, Particle p, boolean vertical) {
        super(timeOfEvent, timeEventCreated);
        _p = p;
        _vertical = vertical;
    }

    public void update () {
        if (_vertical) {
            _p.updateAfterVerticalWallCollision(_timeOfEvent);
        } else {
            _p.updateAfterHorizontalWallCollision(_timeOfEvent);
        }
    }
}
