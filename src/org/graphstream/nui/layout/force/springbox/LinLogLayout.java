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
package org.graphstream.nui.layout.force.springbox;

import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.layout.force.ForceLayout;
import org.graphstream.nui.layout.force.Particle;
import org.graphstream.nui.layout.force.Spring;
import org.graphstream.nui.spacePartition.SpaceCell;
import org.graphstream.ui.geom.Vector3;

public class LinLogLayout extends ForceLayout {
	/**
	 * Default general attraction factor.
	 */
	protected double aFactor = 1f;

	/**
	 * Default general repulsion factor.
	 */
	protected double rFactor = 1f;

	protected boolean edgeBased = true;

	protected double maxR = 0.5;

	protected double a = 0;

	protected double r = -1.2;

	protected Vector3 delta;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.layout.force.ForceLayout#createParticle(org.graphstream
	 * .nui.indexer.ElementIndex)
	 */
	@Override
	protected Particle createParticle(ElementIndex index) {
		return new LinLogParticle();
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
		// TODO Auto-generated method stub
		return null;
	}

	class LinLogParticle extends Particle {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.layout.force.Particle#attraction(org.graphstream
		 * .nui.layout.force.Particle, org.graphstream.nui.layout.force.Spring)
		 */
		@Override
		public void attraction(Particle other, Spring spring) {
			delta.set(other.x() - x(), other.y() - y(), other.z() - z());
			double len = delta.length();

			if (len > 0) {
				double factor = 1;

				factor = (Math.pow(len, a - 2)) * spring.getWeight() * aFactor;
				energies.accumulateEnergy(factor);

				delta.scalarMult(factor);
				displacement.add(delta);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.layout.force.Particle#repulsion(org.graphstream
		 * .nui.layout.force.Particle)
		 */
		@Override
		public void repulsion(Particle other) {
			delta.set(other.x() - x(), other.y() - y(), other.z() - z());
			double len = delta.length();

			if (len > 0) {
				double degFactor = edgeBased ? degree() * other.degree() : 1;
				double factor = 1;

				factor = -degFactor * (Math.pow(len, r - 2)) * other.weight()
						* weight() * rFactor;

				if (factor < -maxR) {
					factor = -maxR;
				}

				energies.accumulateEnergy(factor); // TODO check this
				delta.scalarMult(factor);
				displacement.add(delta);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.layout.force.Particle#repulsion(org.graphstream
		 * .nui.spacePartition.SpaceCell)
		 */
		@Override
		public void repulsion(SpaceCell baryFriends) {
			// TODO Auto-generated method stub

		}
	}
}
