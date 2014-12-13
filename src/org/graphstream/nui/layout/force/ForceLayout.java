/*
 * Copyright 2006 - 2014
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
package org.graphstream.nui.layout.force;

import java.util.logging.Logger;

import org.graphstream.nui.UIAttributes;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UILayout;
import org.graphstream.nui.UISwapper;
import org.graphstream.nui.UIAttributes.AttributeType;
import org.graphstream.nui.UISwapper.ValueFactory;
import org.graphstream.nui.attributes.AttributeHandler;
import org.graphstream.nui.dataset.DataProvider;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIndex.EdgeIndex;
import org.graphstream.nui.indexer.ElementIndex.NodeIndex;
import org.graphstream.nui.indexer.ElementIndex.Type;
import org.graphstream.nui.layout.LayoutAlgorithmBase;
import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.spacePartition.SpaceCell;
import org.graphstream.nui.spacePartition.TreeSpaceCell;
import org.graphstream.nui.spacePartition.TreeSpacePartition;
import org.graphstream.nui.spacePartition.data.BarycenterData;
import org.graphstream.nui.spacePartition.data.SpaceCellDataIndex;
import org.graphstream.nui.swapper.UIArrayReference;
import org.graphstream.nui.util.Tools;
import org.graphstream.ui.geom.Point3;

public abstract class ForceLayout extends LayoutAlgorithmBase {
	private static final Logger LOGGER = Logger.getLogger(ForceLayout.class
			.getName());

	/**
	 * Global force strength. This is a factor in [0..1] that is used to scale
	 * all computed displacements.
	 */
	protected double force = 0.5;

	/**
	 * The stabilization limit of this algorithm.
	 */
	protected double stabilizationLimit = 0.9;

	protected UIArrayReference<Particle> particles;

	protected UIArrayReference<Spring> springs;

	protected Energies energies;

	protected SpaceCellDataIndex barycenterIndex;

	protected double barnesHutTheta = .7f;

	protected Point3[] boundaryPoints;

	protected double boundaryWeight = 0.5;

	protected ForceLayout() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.layout.BaseLayout#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx, UILayout layout) {
		super.init(ctx, layout);

		UISwapper swapper = (UISwapper) ctx.getModule(UISwapper.MODULE_ID);
		assert swapper != null;

		particles = swapper.createArray(Type.NODE, 1, Particle.class,
				new ValueFactory<Particle>() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.graphstream.nui.UISwapper.ValueFactory#createValue
					 * (org.graphstream.nui.indexer.ElementIndex, int)
					 */
					@Override
					public Particle createValue(ElementIndex index,
							int component) {
						return ForceLayout.this
								.createParticle((NodeIndex) index);
					}
				});

		springs = swapper.createArray(Type.EDGE, 1, Spring.class,
				new ValueFactory<Spring>() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.graphstream.nui.UISwapper.ValueFactory#createValue
					 * (org.graphstream.nui.indexer.ElementIndex, int)
					 */
					@Override
					public Spring createValue(ElementIndex index, int component) {
						return createSpring(index);
					}
				});

		UIAttributes attributes = (UIAttributes) ctx
				.getModule(UIAttributes.MODULE_ID);

		attributes.registerUIAttributeHandler(AttributeType.NODE, "frozen",
				new AttributeHandler() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see org.graphstream.nui.attributes.AttributeHandler#
					 * handleAttribute(org.graphstream.nui.indexer.ElementIndex,
					 * java.lang.String, java.lang.Object)
					 */
					@Override
					public void handleAttribute(ElementIndex index,
							String attributeId, Object value) {
						particles.get(index, 0).setFrozen(
								Tools.checkAndGetBoolean(value, true));
					}
				});

		attributes.registerUIAttributeHandler(AttributeType.EDGE, "ignored",
				new AttributeHandler() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see org.graphstream.nui.attributes.AttributeHandler#
					 * handleAttribute(org.graphstream.nui.indexer.ElementIndex,
					 * java.lang.String, java.lang.Object)
					 */
					@Override
					public void handleAttribute(ElementIndex index,
							String attributeId, Object value) {
						springs.get(index, 0).setIgnored(
								Tools.checkAndGetBoolean(value, true));
					}
				});

		energies = new Energies();
		dataProvider = new ParticlesDataProvider();
		computeBoundaryPoints();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.layout.BaseLayout#enableSpacePartition(boolean)
	 */
	@Override
	public void enableSpacePartition(boolean on) {
		if (!on && spacePartition != null && barycenterIndex != null) {
			spacePartition.removeSpaceCellData(barycenterIndex);
			barycenterIndex = null;
		}

		super.enableSpacePartition(on);

		if (spacePartition != null) {
			barycenterIndex = spacePartition
					.addSpaceCellData(BarycenterData.FACTORY);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		if (spacePartition != null && barycenterIndex != null)
			spacePartition.removeSpaceCellData(barycenterIndex);

		springs.release();
		particles.release();

		super.release();
	}

	public double getForce() {
		return force;
	}

	public void setForce(double value) {
		this.force = value;
	}

	public double getStabilizationLimit() {
		return stabilizationLimit;
	}

	public void setStabilizationLimit(double value) {
		this.stabilizationLimit = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.layout.BaseLayout#compute()
	 */
	@Override
	public void compute() {
		preComputation();

		Point3 p1 = new Point3();
		Point3 p2 = new Point3();

		//
		// Repulsion
		//
		if (enableSpacePartition && viewZone > 0)
			computeRepulsionWithSpacePartition();
		else
			computeRepulsion();

		//
		// Attraction
		//
		for (int i = 0; i < dataset.getEdgeCount(); i++) {
			EdgeIndex e = indexer.getEdgeIndex(i);

			if (springs.get(e, 0).isIgnored())
				continue;

			NodeIndex n1 = e.getSource();
			NodeIndex n2 = e.getTarget();
			Particle part1 = particles.get(n1, 0);
			Particle part2 = particles.get(n2, 0);

			dataset.getNodeXYZ(n1, p1);
			dataset.getNodeXYZ(n2, p2);

			if (!part1.isFrozen())
				part1.attraction(p1, p2, getAttractionWeight(n1, e));

			if (!part2.isFrozen())
				part2.attraction(p2, p1, getAttractionWeight(n2, e));
		}

		for (int i = 0; i < indexer.getNodeCount(); i++) {
			NodeIndex index = indexer.getNodeIndex(i);
			Particle p = particles.get(index, 0);
			dataset.getNodeXYZ(index, p1);

			if (p.isFrozen())
				continue;

			for (int j = 0; j < boundaryPoints.length; j++)
				p.repulsion(p1, boundaryPoints[j], boundaryWeight);

			p.displacement.normalize();
			p.displacement.scalarMult(force);

			//if (len > (space.getBounds().getDiagonal() / 2))
			//	p.displacement.scalarMult((space.getBounds().getDiagonal() / 2)
			//			/ len);
		}

		energies.storeEnergy();
	}

	protected void computeRepulsion() {
		Point3 p1 = new Point3();
		Point3 p2 = new Point3();
		double w;

		for (int i = 0; i < dataset.getNodeCount() - 1; i++) {
			NodeIndex n1 = indexer.getNodeIndex(i);
			Particle part1 = particles.get(n1, 0);

			dataset.getNodeXYZ(n1, p1);

			for (int j = i + 1; j < dataset.getNodeCount(); j++) {
				NodeIndex n2 = indexer.getNodeIndex(j);
				Particle part2 = particles.get(n2, 0);

				dataset.getNodeXYZ(n2, p2);

				if (!n1.isConnectedTo(n2)) {
					w = getRepulsionWeight(n1, n2);

					if (!part1.isFrozen())
						particles.get(n1, 0).repulsion(p1, p2, w);

					if (!part2.isFrozen())
						particles.get(n2, 0).repulsion(p2, p1, w);
				}
			}
		}
	}

	protected void computeRepulsionWithSpacePartition() {
		if (spacePartition instanceof TreeSpacePartition) {
			TreeSpacePartition tree = (TreeSpacePartition) spacePartition;

			Point3 p1 = new Point3();
			Point3 p2 = new Point3();

			for (int i = 0; i < dataset.getNodeCount(); i++) {
				NodeIndex n1 = indexer.getNodeIndex(i);
				Particle part1 = particles.get(n1, 0);

				dataset.getNodeXYZ(n1, p1);

				if (!part1.isFrozen())
					computeRepulsionRecursive(n1, p1, p2, tree.getRootCell());
			}
		} else {
			LOGGER.warning("can only use a tree space partition");
			computeRepulsion();
		}
	}

	protected void computeRepulsionRecursive(NodeIndex n1, Point3 p1,
			Point3 p2, TreeSpaceCell cell) {
		//
		// Cell is close enough
		//
		if (intersection(p1, cell)) {
			//
			// Cell is a leaf
			//
			if (cell.getChildrenCount() == 0) {
				for (ElementIndex n2 : cell) {
					dataset.getNodeXYZ(n2, p2);

					particles.get(n1, 0).repulsion(p1, p2,
							getRepulsionWeight(n1, (NodeIndex) n2));
				}
			}
			//
			// Not a leaf, check children
			//
			else {
				for (int i = 0; i < cell.getChildrenCount(); i++)
					computeRepulsionRecursive(n1, p1, p2, cell.getChild(i));
			}
		}
		//
		// Using barycenter data
		//
		else {
			if (cell != spacePartition.getSpaceCell(n1)) {
				BarycenterData barycenter = (BarycenterData) cell
						.getData(barycenterIndex);
				double dist = p1.distance(barycenter.getBarycenter());

				if (cell.getChildrenCount() > 0
						&& (cell.getBoundary().getDiagonal() / dist) > barnesHutTheta) {
					for (int i = 0; i < cell.getChildrenCount(); i++)
						computeRepulsionRecursive(n1, p1, p2, cell.getChild(i));
				} else if (barycenter.getWeight() != 0) {
					particles.get(n1, 0).repulsion(p1,
							barycenter.getBarycenter(),
							getRepulsionWeight(n1, cell, barycenter));
				}
			}
		}
	}

	protected void preComputation() {
		computeViewZoneRadius();
	}

	protected void computeBoundaryPoints() {
		Point3[] b = new Point3[space.is3D() ? 24 : 8];
		Point3 l = space.getBounds().getLowestPoint();
		Point3 h = space.getBounds().getHighestPoint();
		double cx, cy;
		int i = 0;

		cx = (h.x + l.x) / 2;
		cy = (h.y + l.y) / 2;

		if (space.is3D()) {
			double cz = (h.z + l.z) / 2;

			b[i++] = new Point3(l.x, l.y, l.z);
			b[i++] = new Point3(cx, l.y, l.z);
			b[i++] = new Point3(h.x, l.y, l.z);
			b[i++] = new Point3(l.x, cy, l.z);
			b[i++] = new Point3(cx, cy, l.z);
			b[i++] = new Point3(h.x, cy, l.z);
			b[i++] = new Point3(l.x, h.y, l.z);
			b[i++] = new Point3(cx, h.y, l.z);
			b[i++] = new Point3(h.x, h.y, l.z);

			b[i++] = new Point3(l.x, l.y, cz);
			b[i++] = new Point3(cx, l.y, cz);
			b[i++] = new Point3(h.x, l.y, cz);
			b[i++] = new Point3(l.x, cy, cz);
			b[i++] = new Point3(h.x, cy, cz);
			b[i++] = new Point3(l.x, h.y, cz);
			b[i++] = new Point3(cx, h.y, cz);
			b[i++] = new Point3(h.x, h.y, cz);

			b[i++] = new Point3(l.x, l.y, h.z);
			b[i++] = new Point3(cx, l.y, h.z);
			b[i++] = new Point3(h.x, l.y, h.z);
			b[i++] = new Point3(l.x, cy, h.z);
			b[i++] = new Point3(cx, cy, h.z);
			b[i++] = new Point3(h.x, cy, h.z);
			b[i++] = new Point3(l.x, h.y, h.z);
			b[i++] = new Point3(cx, h.y, h.z);
			b[i++] = new Point3(h.x, h.y, h.z);

		} else {
			b[i++] = new Point3(l.x, l.y);
			b[i++] = new Point3(cx, l.y);
			b[i++] = new Point3(h.x, l.y);
			b[i++] = new Point3(l.x, cy);
			b[i++] = new Point3(h.x, cy);
			b[i++] = new Point3(l.x, h.y);
			b[i++] = new Point3(cx, h.y);
			b[i++] = new Point3(h.x, h.y);
		}

		boundaryPoints = b;
	}

	protected double getAttractionWeight(NodeIndex source, EdgeIndex target) {
		return dataset.getElementWeight(target);
	}

	protected double getRepulsionWeight(NodeIndex source, NodeIndex target) {
		return dataset.getElementWeight(target);
	}

	protected double getRepulsionWeight(NodeIndex source, SpaceCell target,
			BarycenterData data) {
		return data.getWeight();
	}

	protected abstract Particle createParticle(NodeIndex index);

	protected abstract Spring createSpring(ElementIndex index);

	class ParticlesDataProvider implements DataProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.dataset.DataProvider#getNodeXYZ(org.graphstream
		 * .nui.indexer.ElementIndex, double[])
		 */
		@Override
		public void getNodeXYZ(ElementIndex index, double[] xyz) {
			Particle p = particles.get(index, 0);
			Bounds b = space.getBounds();
			Point3 h = b.getHighestPoint();
			Point3 l = b.getLowestPoint();

			dataset.getNodeXYZ(index, xyz);

			if (!p.isFrozen()) {
				// System.err.printf("pub %s %s%n", index, p.displacement);

				xyz[0] = check(xyz[0] + p.displacement.x(), l.x, h.x);
				xyz[1] = check(xyz[1] + p.displacement.y(), l.y, h.y);

				if (space.is3D())
					xyz[2] = check(xyz[2] + p.displacement.z(), l.z, h.z);
			}
		}

		protected double check(double v, double min, double max) {
			return Math.max(Math.min(v, max), min);
		}
	}

	class ParticleInternal {

	}
}
