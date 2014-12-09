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

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UISwapper;
import org.graphstream.nui.UISwapper.ValueFactory;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIndex.Type;
import org.graphstream.nui.layout.BaseLayout;
import org.graphstream.nui.swapper.UIArrayReference;

public abstract class ForceLayout extends BaseLayout {

	/**
	 * Global force strength. This is a factor in [0..1] that is used to scale
	 * all computed displacements.
	 */
	protected double force = 1f;

	/**
	 * The stabilization limit of this algorithm.
	 */
	protected double stabilizationLimit = 0.9;

	protected UIArrayReference<Particle> particles;

	protected UIArrayReference<Spring> springs;
	
	protected Energies energies;

	protected ForceLayout() {
		super(UISwapper.MODULE_ID);
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
						return ForceLayout.this.createParticle(index);
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

	public void compute() {
		
	}

	protected abstract Particle createParticle(ElementIndex index);

	protected abstract Spring createSpring(ElementIndex index);
}
