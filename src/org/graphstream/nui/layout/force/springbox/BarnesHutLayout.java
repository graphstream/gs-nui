/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.nui.layout.force.springbox;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UISpacePartition;
import org.graphstream.nui.layout.BaseLayout;
import org.graphstream.nui.layout.force.ForceLayout;
import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.util.Tools;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SourceBase;
import org.graphstream.stream.sync.SinkTime;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Base implementation of a Barnes-Hut space decomposition and particle
 * interaction algorithm to be used for force-based layout algorithms.
 * 
 * <p>
 * This base class creates the space decomposition method and manages the main
 * loop of the simulation. The simulation is made of {@link NodeParticle} and
 * {@link EdgeSpring} elements that are created and linked for you in response
 * to graph events received via the {@link Sink} interface. However you have to
 * provide an implementation of the abstract {@link NodeParticle} class (by
 * overriding the abstract method {@link #newNodeParticle(String)}).
 * </p>
 * 
 * <p>
 * As almost all the repulsion/attraction forces computation is done in the
 * {@link NodeParticle} class, this is the most important one.
 * </p>
 * 
 * <p>
 * This algorithm can be configured using several attributes put on the graph :
 * <ul>
 * <li>layout.force : a floating point number (default 0.5f), that allows to
 * define the importance of movement of each node at each computation step. The
 * larger the value the quicker nodes move to their position of lowest energy.
 * However too high values can generate non stable layouts and oscillations.</li>
 * <li>layout.quality : an integer between 0 and 4. With value 0 the layout is
 * faster but it also can be farther from equilibrium. With value 4 the
 * algorithm tries to be as close as possible from equilibrium (the n-tree and
 * Barnes-Hut algorithms are disabled), but the computation can take a lot of
 * time (the algorithm becomes O(n^2)). TODO change this into layout.barneshut
 * or something similar.</li>
 * </ul>
 * You can also put the following attributes on nodes :
 * <ul>
 * <li>layout.weight : The force of repulsion of a node. The larger the value,
 * the more the node repulses its neighbors.</li>
 * </ul>
 * And on edges :
 * <ul>
 * <li>layout.weight : the multiplier for the desired edge length. By default
 * the algorithm tries to make each edge of length one. This is the position of
 * lowest energy for a spring. This coefficient allows to modify this target
 * spring length. Value larger than one will make the edge longer. Values
 * between 0 and 1 will make the edge smaller.</li>
 * <li>layout.stabilization-limit : the stabilization of a layout is a number
 * between 0 and 1. 1 means fully stable, but this value is rare. Therefore one
 * can consider the layout stable at a lower value. The default is 0.9. You can
 * fix it with this attribute.</li>
 * </ul>
 * </p>
 */
public abstract class BarnesHutLayout extends ForceLayout {
	/**
	 * class level logger
	 */
	private static final Logger LOGGER = Logger.getLogger(BarnesHutLayout.class
			.getName());

	public static final String ATTRIBUTE_RANDOM_SEED = "randomSeed";

	public static final String ATTRIBUTE_QUALITY = "quality";

	/**
	 * The set of edges.
	 */
	protected HashMap<String, EdgeSpring> edges = new HashMap<String, EdgeSpring>();

	/**
	 * Used to avoid stabilizing if an event occurred.
	 */
	protected int lastElementCount = 0;

	/**
	 * Random number generator.
	 */
	protected Random random;

	/**
	 * Energy, and the history of energies.
	 */
	protected Energies energies = new Energies();

	/**
	 * Global force strength. This is a factor in [0..1] that is used to scale
	 * all computed displacements.
	 */
	protected double force = 1f;

	/**
	 * The view distance at which the cells of the n-tree are explored
	 * exhaustively, after this the poles are used. This is a multiple of k.
	 */
	protected double viewZone = 5f;

	/**
	 * The Barnes/Hut theta threshold to know if we use a pole or not.
	 */
	protected double theta = .7f;

	/**
	 * The quality level.
	 */
	protected double quality = 1;

	/**
	 * The diagonal of the graph area at the current step.
	 */
	protected double area = 1;

	/**
	 * The stabilization limit of this algorithm.
	 */
	protected double stabilizationLimit = 0.9;

	// Attributes -- Settings

	/**
	 * The gravity factor. If set to 0 the gravity computation is disabled.
	 */
	protected double gravity = 0;

	protected long randomSeed = Long.MAX_VALUE;

	/**
	 * New 2D Barnes-Hut simulation.
	 */
	public BarnesHutLayout() {
		this(new Random(System.currentTimeMillis()));
	}

	/**
	 * New Barnes-Hut simulation.
	 * 
	 * @param is3D
	 *            If true the simulation dimensions count is 3 else 2.
	 * @param randomNumberGenerator
	 *            The random number generator to use.
	 */
	public BarnesHutLayout(Random randomNumberGenerator) {
		super(UIDataset.MODULE_ID, UISpacePartition.MODULE_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.layout.BaseLayout#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx) {
		super.init(ctx);

		if (randomSeed == Long.MAX_VALUE)
			random = new Random(System.currentTimeMillis());
		else
			random = new Random(randomSeed);

		setQuality(quality);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String key, Object value) {
		super.setAttribute(key, value);

		switch (key) {
		case ATTRIBUTE_RANDOM_SEED:
			try {
				randomSeed = Tools.checkAndGetLong(value);
				random = new Random(randomSeed);
			} catch (IllegalArgumentException e) {
				LOGGER.warning(String.format("Illegal value for %s.%s : %s",
						MODULE_ID, ATTRIBUTE_RANDOM_SEED, value));
			}

			break;
		case ATTRIBUTE_QUALITY:
			try {
				quality = Tools.checkAndGetDouble(value);
				setQuality(quality);
			} catch (IllegalArgumentException e) {
				LOGGER.warning(String.format("Illegal value for %s.%s : %s",
						MODULE_ID, ATTRIBUTE_RANDOM_SEED, value));
			}

			break;
		default:
			break;
		}
	}

	protected double randomXInsideBounds() {
		double lx = space.getBounds().getLowestPoint().x;
		double hx = space.getBounds().getHighestPoint().x;

		return lx + random.nextDouble() * (hx - lx);
	}

	protected double randomYInsideBounds() {
		double ly = space.getBounds().getLowestPoint().y;
		double hy = space.getBounds().getHighestPoint().y;

		return ly + random.nextDouble() * (hy - ly);
	}

	protected double randomZInsideBounds() {
		double lz = space.getBounds().getLowestPoint().z;
		double hz = space.getBounds().getHighestPoint().z;

		return lz + random.nextDouble() * (hz - lz);
	}

	/**
	 * A gravity factor that attracts all nodes to the center of the layout to
	 * avoid flying components. If set to zero, the gravity computation is
	 * disabled.
	 * 
	 * @return The gravity factor, usually between 0 and 1.
	 */
	public double getGravityFactor() {
		return gravity;
	}

	/**
	 * Set the gravity factor that attracts all nodes to the center of the
	 * layout to avoid flying components. If set to zero, the gravity
	 * computation is disabled.
	 * 
	 * @param value
	 *            The new gravity factor, usually between 0 and 1.
	 */
	public void setGravityFactor(double value) {
		gravity = value;
	}

	public abstract String getLayoutAlgorithmName();

	public double getStabilization() {
		if (lastElementCount == indexer.getNodeCount() + indexer.getEdgeCount()) {
			if (stats.getTime() > energies.getBufferSize())
				return energies.getStabilization();
		}

		lastElementCount = indexer.getNodeCount() + indexer.getEdgeCount();

		return 0;
	}

	public double getStabilizationLimit() {
		return stabilizationLimit;
	}

	public double getQuality() {
		return quality;
	}

	public double getForce() {
		return force;
	}

	public Random getRandom() {
		return random;
	}

	public Energies getEnergies() {
		return energies;
	}

	/**
	 * The Barnes-Hut theta value used to know if we use a pole or not.
	 * 
	 * @return The theta value (between 0 and 1).
	 */
	public double getBarnesHutTheta() {
		return theta;
	}

	public double getViewZone() {
		return viewZone;
	}

	/**
	 * Change the barnes-hut theta parameter allowing to know if we use a pole
	 * or not.
	 * 
	 * @param theta
	 *            The new value for theta (between 0 and 1).
	 */
	public void setBarnesHutTheta(double theta) {
		if (theta > 0 && theta < 1) {
			this.theta = theta;
		}
	}

	public void setForce(double value) {
		this.force = value;
	}

	public void setStabilizationLimit(double value) {
		this.stabilizationLimit = value;
	}

	public void setQuality(double qualityLevel) {
		if (qualityLevel > 1)
			qualityLevel = 1;
		else if (qualityLevel < 0)
			qualityLevel = 0;

		quality = qualityLevel;
	}

	public void clear() {
		energies.clearEnergies();
		nodes.removeAllParticles();
		edges.clear();
		nodeMoveCount = 0;
		lastStepTime = 0;
	}

	public void compute() {
		long t1;

		computeArea();

		maxMoveLength = Double.MIN_VALUE;
		t1 = System.currentTimeMillis();
		nodeMoveCount = 0;
		avgLength = 0;

		// All the movement computation is done in this call.
		nodes.step();

		if (nodeMoveCount > 0)
			avgLength /= nodeMoveCount;

		// Ready for the next step.

		// center.set(0, 0, 0);
		energies.storeEnergy();
		time++;
		lastStepTime = System.currentTimeMillis() - t1;
	}

	protected void computeArea() {
		Bounds b = space.getBounds();
		area = b.getHighestPoint().distance(b.getLowestPoint());
	}

	public void shake() {
		energies.clearEnergies();
	}

	protected NodeParticle addNode(String sourceId, String id) {
		NodeParticle np = newNodeParticle(id);
		nodes.addParticle(np);
		return np;
	}

	public void moveNode(String id, double x, double y, double z) {
		NodeParticle node = (NodeParticle) nodes.getParticle(id);

		if (node != null) {
			node.moveTo(x, y, z);
			energies.clearEnergies();
		}
	}

	public void freezeNode(String id, boolean on) {
		NodeParticle node = (NodeParticle) nodes.getParticle(id);

		if (node != null) {
			node.frozen = on;
		}
	}

	protected void setNodeWeight(String id, double weight) {
		NodeParticle node = (NodeParticle) nodes.getParticle(id);

		if (node != null)
			node.setWeight(weight);
	}

	protected void removeNode(String sourceId, String id) {
		NodeParticle node = (NodeParticle) nodes.removeParticle(id);

		if (node != null) {
			node.removeNeighborEdges();
		} else {
			LOGGER.warning(String.format(
					"layout %s: cannot remove non existing node %s%n",
					getLayoutAlgorithmName(), id));
		}
	}

	protected void addEdge(String sourceId, String id, String from, String to,
			boolean directed) {
		NodeParticle n0 = (NodeParticle) nodes.getParticle(from);
		NodeParticle n1 = (NodeParticle) nodes.getParticle(to);

		if (n0 != null && n1 != null) {
			EdgeSpring e = new EdgeSpring(id, n0, n1);
			EdgeSpring o = edges.put(id, e);

			if (o != null) {
				LOGGER.warning(String.format(
						"layout %s: edge '%s' already exists.",
						getLayoutAlgorithmName(), id));
			} else {
				n0.registerEdge(e);
				n1.registerEdge(e);
			}

			chooseNodePosition(n0, n1);
		} else {
			if (n0 == null)
				LOGGER.warning(String
						.format("layout %s: node '%s' does not exist, cannot create edge %s.",
								getLayoutAlgorithmName(), from, id));
			if (n1 == null)
				LOGGER.warning(String
						.format("layout %s: node '%s' does not exist, cannot create edge %s.",
								getLayoutAlgorithmName(), to, id));
		}
	}

	/**
	 * Choose the best position for a node that was just connected by only one
	 * edge to a cluster of nodes.
	 * 
	 * @param n0
	 *            source node of the edge.
	 * @param n1
	 *            target node of the edge.
	 */
	protected abstract void chooseNodePosition(NodeParticle n0, NodeParticle n1);

	protected void addEdgeBreakPoint(String edgeId, int points) {
		LOGGER.warning(String.format(
				"layout %s: edge break points are not handled yet.",
				getLayoutAlgorithmName()));
	}

	protected void ignoreEdge(String edgeId, boolean on) {
		EdgeSpring edge = edges.get(edgeId);

		if (edge != null) {
			edge.ignored = on;
		}
	}

	protected void setEdgeWeight(String id, double weight) {
		EdgeSpring edge = edges.get(id);

		if (edge != null)
			edge.weight = weight;
	}

	protected void removeEdge(String sourceId, String id) {
		EdgeSpring e = edges.remove(id);

		if (e != null) {
			e.node0.unregisterEdge(e);
			e.node1.unregisterEdge(e);
		} else {
			LOGGER.warning(String.format(
					"layout %s: cannot remove non existing edge %s%n",
					getLayoutAlgorithmName(), id));
		}
	}

	// SourceBase interface

	protected void graphAttributeChanged_(String graphId, String attribute,
			Object oldValue, Object newValue) {
		if (attribute.equals("layout.force")) {
			if (newValue instanceof Number)
				setForce(((Number) newValue).doubleValue());
			energies.clearEnergies();
		} else if (attribute.equals("layout.quality")) {
			if (newValue instanceof Number) {
				int q = ((Number) newValue).intValue();

				q = q > 4 ? 4 : q;
				q = q < 0 ? 0 : q;

				setQuality(q);
				LOGGER.fine(String.format("layout.%s.quality: %d.",
						getLayoutAlgorithmName(), q));
			}

			energies.clearEnergies();
		} else if (attribute.equals("layout.gravity")) {
			if (newValue instanceof Number) {
				double value = ((Number) newValue).doubleValue();
				setGravityFactor(value);
				LOGGER.fine(String.format("layout.%s.gravity: %f.",
						getLayoutAlgorithmName(), value));
			}
		} else if (attribute.equals("layout.exact-zone")) {
			if (newValue instanceof Number) {
				double factor = ((Number) newValue).doubleValue();

				factor = factor > 1 ? 1 : factor;
				factor = factor < 0 ? 0 : factor;

				viewZone = factor;
				LOGGER.fine(String.format(
						"layout.%s.exact-zone: %f of [0..1]%n",
						getLayoutAlgorithmName(), viewZone));

				energies.clearEnergies();
			}
		} else if (attribute.equals("layout.output-stats")) {
			if (newValue == null)
				outputStats = false;
			else
				outputStats = true;

			LOGGER.fine(String.format("layout.%s.output-stats: %b%n",
					getLayoutAlgorithmName(), outputStats));
		} else if (attribute.equals("layout.stabilization-limit")) {
			if (newValue instanceof Number) {
				stabilizationLimit = ((Number) newValue).doubleValue();
				if (stabilizationLimit > 1)
					stabilizationLimit = 1;
				else if (stabilizationLimit < 0)
					stabilizationLimit = 0;

				energies.clearEnergies();
			}
		}
	}

	protected void nodeAttributeChanged_(String graphId, String nodeId,
			String attribute, Object oldValue, Object newValue) {
		if (attribute.equals("layout.weight")) {
			if (newValue instanceof Number)
				setNodeWeight(nodeId, ((Number) newValue).doubleValue());
			else if (newValue == null)
				setNodeWeight(nodeId, 1);

			energies.clearEnergies();
		} else if (attribute.equals("layout.frozen")) {
			freezeNode(nodeId, (newValue != null));
		} else if (attribute.equals("xyz") || attribute.equals("xy")) {
			double xyz[] = new double[3];
			GraphPosLengthUtils.positionFromObject(newValue, xyz);
			moveNode(nodeId, xyz[0], xyz[1], xyz[2]);
		} else if (attribute.equals("x") && newValue instanceof Number) {
			NodeParticle node = (NodeParticle) nodes.getParticle(nodeId);
			if (node != null) {
				moveNode(nodeId, ((Number) newValue).doubleValue(),
						node.getPosition().y, node.getPosition().z);
			}
		} else if (attribute.equals("y") && newValue instanceof Number) {
			NodeParticle node = (NodeParticle) nodes.getParticle(nodeId);
			if (node != null) {
				moveNode(nodeId, node.getPosition().x,
						((Number) newValue).doubleValue(), node.getPosition().z);
			}
		}
	}

	protected void edgeAttributeChanged_(String graphId, String edgeId,
			String attribute, Object oldValue, Object newValue) {
		if (attribute.equals("layout.weight")) {
			if (newValue instanceof Number)
				setEdgeWeight(edgeId, ((Number) newValue).doubleValue());
			else if (newValue == null)
				setEdgeWeight(edgeId, 1);

			energies.clearEnergies();
		} else if (attribute.equals("layout.ignored")) {
			if (newValue instanceof Boolean)
				ignoreEdge(edgeId, (Boolean) newValue);
			energies.clearEnergies();
		}
	}

	/**
	 * Factory method to create node particles.
	 * 
	 * @param id
	 *            The identifier of the new node/particle.
	 * @return The new node/particle.
	 */
	public abstract NodeParticle newNodeParticle(String id);
}