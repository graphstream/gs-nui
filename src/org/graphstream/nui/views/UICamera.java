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
package org.graphstream.nui.views;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIView;
import org.graphstream.nui.geom.Vector3;
import org.graphstream.nui.geom.Vector4;
import org.graphstream.nui.space.Bounds;

/**
 * Each view rendering a graph has its own camera. It allows to define the
 * position of the observer compared to the graph. There are several elements to
 * consider :
 * <ul>
 * <li>the size of the rendering surface ;</li>
 * <li>the space of the coordinates of the graph ;</li>
 * <li>the viewport, which is the part of the nodes space which is being
 * renderer.</li>
 * </ul>
 * 
 * A camera will manage these elements and will be able to translate points from
 * one space (nodes space, or pixels space) to one other.
 */
public interface UICamera {
	public static enum ConvertType {
		PX_TO_GU, GU_TO_PX
	}

	void init(UIContext ctx, UIView view);

	void release();

	Bounds getObservedSpace();

	Vector3 getSpaceRotation();

	Vector4 getViewport();
	
	void convert(Vector3 source, Vector3 target, ConvertType type);
}
