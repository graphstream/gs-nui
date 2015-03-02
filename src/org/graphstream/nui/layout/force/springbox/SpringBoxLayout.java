/*
 * Copyright 2006 - 2015
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

import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIndex.NodeIndex;
import org.graphstream.nui.layout.force.ForceLayout;
import org.graphstream.nui.layout.force.Particle;
import org.graphstream.nui.layout.force.Spring;
import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.geom.Vector3;

public class SpringBoxLayout extends ForceLayout {
	public static final String LAYOUT_NAME = "springbox";

	protected double k = 1;

	protected double C = 1.0;
	/**
	 * Default attraction.
	 */
	protected double K1 = 0.06f; // 0.3 ??

	/**
	 * Default repulsion.
	 */
	protected double K2 = 0.024f; // 0.12 ??

	protected Vector3 delta = new Vector3();

	protected double lastArea = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.layout.force.ForceLayout#createParticle(org.graphstream
	 * .nui.indexer.ElementIndex)
	 */
	@Override
	protected Particle createParticle(NodeIndex index) {
		return new SpringBoxParticle(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.layout.force.ForceLayout#createSpring(org.graphstream
	 * .nui.indexer.ElementIndex)
	 */
	@Override
	protected Spring createSpring(ElementIndex index) {
		return new Spring();
	}

	protected void preComputation() {
		super.preComputation();

		Bounds b = space.getBounds();
		double area = b.getWidth() * b.getHeight()
				* (space.is3D() ? b.getDepth() : 1.0);

		if (area != lastArea) {
			area /= dataset.getNodeCount();

			if (space.is3D())
				area = Math.cbrt(area);
			else
				area = Math.sqrt(area);

			k = C * area;
			lastArea = area;
		}
	}

	class SpringBoxParticle extends Particle {
		public SpringBoxParticle(NodeIndex index) {
			super(index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.layout.force.Particle#attraction(org.graphstream
		 * .ui.geom.Point3, double)
		 */
		@Override
		public void attraction(Vector3 p1, Vector3 p2, double weight) {
			delta.set(p2.x() - p1.x(), p2.y() - p1.y(), p2.z() - p1.z());
			double len = delta.normalize();

			double k = SpringBoxLayout.this.k * weight;

			double factor = K1 * (len - k);
			factor = factor * (1f / (index.getDegree() * 0.1f));

			energies.accumulateEnergy(factor);

			delta.selfScalarMult(factor);
			displacement.selfAdd(delta);

			// System.err.printf("> [%.2f;%.2f] -- [%.2f;%.2f] : [%.2f;%.2f]%n",
			// p1.x, p1.y, p2.x, p2.y, delta.x(), delta.y());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.layout.force.Particle#repulsion(org.graphstream
		 * .ui.geom.Point3, double)
		 */
		@Override
		public void repulsion(Vector3 p1, Vector3 p2, double weight) {
			delta.set(p2.x() - p1.x(), p2.y() - p1.y(), p2.z() - p1.z());
			double len = delta.normalize();

			if (len > 0) {

				if (len < k)
					len = k;
				double factor = len != 0 ? ((K2 / (len * len)) * weight)
						: 0.00001;

				energies.accumulateEnergy(factor); // TODO check this
				delta.selfScalarMult(-factor);
				displacement.selfAdd(delta);

				// System.err.printf(
				// "< [%.2f;%.2f] -- [%.2f;%.2f] : [%.2f;%.2f]%n", x(),
				// y(), p.x, p.y, delta.x(), delta.y());
			}
		}
	}
}
