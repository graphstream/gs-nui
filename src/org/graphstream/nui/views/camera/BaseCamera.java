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
package org.graphstream.nui.views.camera;

import java.util.logging.Logger;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UISpace;
import org.graphstream.nui.UIView;
import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.views.UICamera;
import org.graphstream.nui.geom.Vector3;
import org.graphstream.nui.geom.Vector4;

public abstract class BaseCamera<T extends CameraTransform> implements UICamera {
	private static final Logger LOGGER = Logger.getLogger(BaseCamera.class
			.getName());

	protected UIContext ctx;

	protected Vector4 viewport;

	protected Bounds observedSpace;
	protected RotationVector rotation;

	protected T transform;

	protected boolean changed;

	protected BaseCamera() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.UICamera#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx, UIView view) {
		this.ctx = ctx;
		this.rotation = new RotationVector();
		this.rotation.set(Math.PI / 3, Math.PI / 3, Math.PI / 3);
		this.viewport = new ViewportVector();
		this.changed = true;

		this.observedSpace = new InternalBounds();
		UISpace space = (UISpace) ctx.getModule(UISpace.MODULE_ID);
		observedSpace.set(space.getBounds().getLowestPoint(), space.getBounds()
				.getHighestPoint());

		this.transform = createTransform();
		this.transform.init(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.UICamera#release()
	 */
	@Override
	public void release() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.UICamera#getObservedSpace()
	 */
	@Override
	public Bounds getObservedSpace() {
		return observedSpace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.UICamera#getSpaceRotation()
	 */
	@Override
	public Vector3 getSpaceRotation() {
		return rotation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.UICamera#getViewport()
	 */
	@Override
	public Vector4 getViewport() {
		return viewport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.UICamera#convert(org.graphstream.ui.geom.Point3
	 * , org.graphstream.ui.geom.Point3,
	 * org.graphstream.nui.views.UICamera.ConvertType)
	 */
	@Override
	public void convert(Vector3 source, Vector3 target, ConvertType type) {
		checkChanged();
		transform.convert(source, target, type);
	}

	protected abstract T createTransform();

	protected void checkChanged() {
		if (changed) {
			transform.init(this);
			changed = false;
		}
	}

	class InternalBounds extends Bounds {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.space.Bounds#fireBoundsUpdated()
		 */
		@Override
		protected void fireBoundsUpdated() {
			super.fireBoundsUpdated();
			changed = true;
		}
	}

	class RotationVector extends Vector3 {
		@Override
		protected void dataChanged() {
			LOGGER.info(String.format("rotation set to %s", rotation));
			changed = true;
		}
	}

	class ViewportVector extends Vector4 {
		@Override
		protected void dataChanged() {
			LOGGER.info(String.format("viewport set to %s", viewport));
			changed = true;
		}
	}
}
