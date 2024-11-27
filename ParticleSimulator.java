import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ParticleSimulator extends JPanel {
	private Heap<Event> _events;
	private java.util.List<Particle> _particles;
	private double _duration;
	private int _width;

	/**
	 * @param filename the name of the file to parse containing the particles
	 */
	public ParticleSimulator (String filename) throws IOException {
		_events = new HeapImpl<>();

		// Parse the specified file and load all the particles.
		Scanner s = new Scanner(new File(filename));
		_width = s.nextInt();
		_duration = s.nextDouble();
		s.nextLine();
		_particles = new ArrayList<>();
		while (s.hasNext()) {
			String line = s.nextLine();
			Particle particle = Particle.build(line);
			_particles.add(particle);
		}

		setPreferredSize(new Dimension(_width, _width));
	}

	@Override
	/**
	 * Draws all the particles on the screen at their current locations
	 * DO NOT MODIFY THIS METHOD
	 */
        public void paintComponent (Graphics g) {
		g.clearRect(0, 0, _width, _width);
		for (Particle p : _particles) {
			p.draw(g);
		}
	}

	// Helper class to signify the final event of the simulation.
	private class TerminationEvent extends Event {
		TerminationEvent (double timeOfEvent) {
			super(timeOfEvent, 0);
		}

		@Override
		public void update() {
			// Do nothing
		}

		@Override
		public boolean isValid() {
			return false;
		}
	}

	/**
	 * Helper method to update the positions of all the particles based on their current velocities.
	 */
	private void updateAllParticles (double delta) {
		for (Particle p : _particles) {
			p.update(delta);
		}
	}

	/**
	 * Executes the actual simulation.
	 */
	private void simulate (boolean show) {
		double lastTime = 0;

		// Create initial events, i.e., all the possible
		// collisions between all the particles and each other,
		// and all the particles and the walls.
		for (int i = 0; i < _particles.size(); i++) {
			Particle p = _particles.get(i);
			// Add wall collision events
			double verticalWallCollisionTime = p.getVerticalWallCollisionTime(_width);
			double horizontalWallCollisionTime = p.getHorizontalWallCollisionTime(_width);

			if (verticalWallCollisionTime != Double.POSITIVE_INFINITY) {
				_events.add(new WallCollisionEvent(lastTime + verticalWallCollisionTime, lastTime, p, true));
			}
			if (horizontalWallCollisionTime != Double.POSITIVE_INFINITY) {
				_events.add(new WallCollisionEvent(lastTime + horizontalWallCollisionTime, lastTime, p, false));
			}

			// Add particle collision events
			for (int j = i + 1; j < _particles.size(); j++) {
				Particle q = _particles.get(j);
				if (p != q) {
					double collisionTime = p.getCollisionTime(q);
					if (collisionTime != Double.POSITIVE_INFINITY) {
						_events.add(new ParticleCollisionEvent(lastTime + collisionTime, lastTime, p, q));
					}
				}
			}
		}

		_events.add(new TerminationEvent(_duration));
		while (_events.size() > 0) {
			Event event = _events.removeFirst();
			double delta = event._timeOfEvent - lastTime;

			if (event instanceof TerminationEvent) {
				updateAllParticles(delta);
				break;
			}

			// Check if event still valid; if not, then skip this event
			if (!event.isValid()) {
				continue;
			}

			// Since the event is valid, then pause the simulation for the right
			// amount of time, and then update the screen.
			if (show) {
				try {
					Thread.sleep((long) delta);
				} catch (InterruptedException ie) {}
			}

			// Update positions of all particles
			updateAllParticles(delta);

			// Update the velocity of the particle(s) involved in the collision
			// (either for a particle-wall collision or a particle-particle collision).
			// You should call the Particle.updateAfterCollision method at some point.
			event.update();

			// Enqueue new events for the particle(s) that were involved in this event.
			for (int i = 0; i < _particles.size(); i++) {
				Particle p = _particles.get(i);
				// Add wall collision events
				double verticalWallCollisionTime = p.getVerticalWallCollisionTime(_width);
				double horizontalWallCollisionTime = p.getHorizontalWallCollisionTime(_width);

				if (verticalWallCollisionTime != Double.POSITIVE_INFINITY) {
					_events.add(new WallCollisionEvent(event._timeOfEvent + verticalWallCollisionTime, event._timeOfEvent, p, true));
				}
				if (horizontalWallCollisionTime != Double.POSITIVE_INFINITY) {
					_events.add(new WallCollisionEvent(event._timeOfEvent + horizontalWallCollisionTime, event._timeOfEvent, p, false));
				}

				// Add particle collision events
				for (int j = i + 1; j < _particles.size(); j++) {
					Particle q = _particles.get(j);
					if (p != q) {
						double collisionTime = p.getCollisionTime(q);
						if (collisionTime != Double.POSITIVE_INFINITY) {
							_events.add(new ParticleCollisionEvent(event._timeOfEvent + collisionTime, event._timeOfEvent, p, q));
						}
					}
				}
			}

			// Update the time of our simulation
			lastTime = event._timeOfEvent;

			// Redraw the screen
			if (show) {
				repaint();
			}
		}

		// Print out the final state of the simulation
		System.out.println(_width);
		System.out.println(_duration);
		for (Particle p : _particles) {
			System.out.println(p);
		}
	}

	public static void main (String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Usage: java ParticalSimulator <filename>");
			System.exit(1);
		}

		ParticleSimulator simulator;

		simulator = new ParticleSimulator(args[0]);
		JFrame frame = new JFrame();
		frame.setTitle("Particle Simulator");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(simulator, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulator.simulate(true);
	}
}
