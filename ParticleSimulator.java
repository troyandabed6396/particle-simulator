import java.util.*;
import java.util.function.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;

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
		for (Particle p : _particles) {
			double verticalWallCollisionTime = p.getVerticalWallCollisionTime(_width);
			double horizontalWallCollisionTime = p.getHorizontalWallCollisionTime(_width);

			if (verticalWallCollisionTime != Double.POSITIVE_INFINITY) {
				_events.add(new Event(lastTime + verticalWallCollisionTime, lastTime));
			}
			if (horizontalWallCollisionTime != Double.POSITIVE_INFINITY) {
				_events.add(new Event(lastTime + horizontalWallCollisionTime, lastTime));
			}

			for (Particle q : _particles) {
				if (p != q) {
					double collisionTime = p.getCollisionTime(q);
					if (collisionTime != Double.POSITIVE_INFINITY) {
						_events.add(new Event(lastTime + collisionTime, lastTime));
					}
				}
			}
		}

		_events.add(new TerminationEvent(_duration));
		while (_events.size() > 0) {
			Event event = _events.removeFirst();
			System.out.println("Processing event at time " + event._timeOfEvent + " created at time " + event._timeEventCreated);
			double delta = event._timeOfEvent - lastTime;

			if (event instanceof TerminationEvent) {
				updateAllParticles(delta);
				break;
			}

			// Check if event still valid; if not, then skip this event
			if (!isEventValid(event)) {
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
			for (Particle p : _particles) {
				if (p.getVerticalWallCollisionTime(_width) < 1e-6) {
					p.updateAfterVerticalWallCollision(event._timeOfEvent);
				} else if (p.getHorizontalWallCollisionTime(_width) < 1e-6) {
					p.updateAfterHorizontalWallCollision(event._timeOfEvent);
				}

				for (Particle q : _particles) {
					if (p != q) {
						if (p.getCollisionTime(q) != Double.POSITIVE_INFINITY) {
							p.updateAfterCollision(event._timeOfEvent, q);
						}
					}
				}
			}

			// Enqueue new events for the particle(s) that were involved in this event.
			for (Particle p : _particles) {
				double verticalWallCollisionTime = p.getVerticalWallCollisionTime(_width);
				double horizontalWallCollisionTime = p.getHorizontalWallCollisionTime(_width);

				if (verticalWallCollisionTime != Double.POSITIVE_INFINITY) {
					_events.add(new Event(event._timeOfEvent + verticalWallCollisionTime, event._timeOfEvent));
				}
				if (horizontalWallCollisionTime != Double.POSITIVE_INFINITY) {
					_events.add(new Event(event._timeOfEvent + horizontalWallCollisionTime, event._timeOfEvent));
				}

				for (Particle q : _particles) {
					if (p != q) {
						double collisionTime = p.getCollisionTime(q);
						if (collisionTime != Double.POSITIVE_INFINITY) {
							_events.add(new Event(event._timeOfEvent + collisionTime, event._timeOfEvent));
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

	private boolean isEventValid (Event event) {
		for (Particle p : _particles) {
			if (p.get_lastUpdateTime() > event._timeEventCreated) {
				return false;
			}
		}
		return true;
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
