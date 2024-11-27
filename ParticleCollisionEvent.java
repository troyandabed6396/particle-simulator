public class ParticleCollisionEvent extends Event {
    private Particle _p1, _p2;

    public ParticleCollisionEvent (double timeOfEvent, double timeEventCreated, Particle p1, Particle p2) {
        super(timeOfEvent, timeEventCreated);
        _p1 = p1;
        _p2 = p2;
    }

    public void update () {
        _p1.updateAfterCollision(_timeOfEvent, _p2);
    }
}
