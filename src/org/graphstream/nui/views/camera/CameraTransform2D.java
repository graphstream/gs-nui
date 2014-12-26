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

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.nui.geom.Vector3;
import org.graphstream.nui.geom.Vector4;
import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.views.UICamera;
import org.graphstream.nui.views.UICamera.ConvertType;
import org.graphstream.nui.views.swing.AWTTransform;

public class CameraTransform2D implements AWTTransform {
	private static final Logger LOGGER = Logger
			.getLogger(CameraTransform2D.class.getName());

	protected UICamera camera;
	protected AffineTransform tx;
	protected AffineTransform xt;

	public CameraTransform2D() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.camera.CameraTransform#init(org.graphstream
	 * .nui.views.UICamera)
	 */
	@Override
	public void init(UICamera camera) {
		this.camera = camera;

		Bounds subspace = camera.getObservedSpace();

		Vector3 origin = subspace.getCenter();
		Vector4 viewport = camera.getViewport();
		double wp = viewport.get(2);
		double hp = viewport.get(3);
		double wg = subspace.getWidth();
		double hg = subspace.getHeight();

		tx = new AffineTransform();

		double sx = wp / wg;
		double sy = hp / hg;

		tx.translate(wp / 2, hp / 2);
		tx.scale(sx, sy);
		tx.translate(-origin.x(), -origin.y());

		xt = new AffineTransform(tx);

		try {
			xt.invert();
		} catch (NoninvertibleTransformException e) {
			LOGGER.log(Level.WARNING, "Cannot inverse gu2px matrix.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.camera.CameraTransform#convert(org.graphstream
	 * .nui.geom.Vector3, org.graphstream.nui.geom.Vector3,
	 * org.graphstream.nui.views.UICamera.ConvertType)
	 */
	@Override
	public void convert(Vector3 source, Vector3 target, ConvertType type) {
		Point2D.Double p1 = new Point2D.Double(source.x(), source.y());
		Point2D.Double p2 = new Point2D.Double();

		switch (type) {
		case GU_TO_PX:
			tx.transform(p1, p2);
			break;
		case PX_TO_GU:
			xt.transform(p1, p2);
			break;
		default:
			break;
		}

		target.set(p2.x, p2.y, target.z());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.swing.AWTTransform#getAWTTransform()
	 */
	@Override
	public AffineTransform getAWTTransform() {
		return tx;
	}
}
