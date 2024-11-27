public class ParticleCollisionEvent extends Event {
    private Particle _p1, _p2;

    /**
     * Constructor for the ParticleCollisionEvent class.
     * @param timeOfEvent the time of the event
     * @param timeEventCreated the time the event was created
     * @param p1 the first particle involved in the collision
     * @param p2 the second particle involved in the collision
     */
    public ParticleCollisionEvent (double timeOfEvent, double timeEventCreated, Particle p1, Particle p2) {
        super(timeOfEvent, timeEventCreated);
        _p1 = p1;
        _p2 = p2;
    }

    /**
     * Updates the velocity of the particles after the collision.
     */
    @Override
    public void update () {
        _p1.updateAfterCollision(_timeOfEvent, _p2);
    }

    /**
     * Determines if the event is valid.
     * @return true if the event is valid, false otherwise
     */
    @Override
    public boolean isValid() {
        return _p1.get_lastUpdateTime() <= _timeEventCreated && _p2.get_lastUpdateTime() <= _timeEventCreated;
    }
}
