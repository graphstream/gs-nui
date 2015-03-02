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
package org.graphstream.nui;

import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.views.UICamera;

/**
 * The module UISpace allows to manage the space of the nodes coordinates. It is
 * composed of the lowest and highest points of this space such that nodes
 * positions have to be located between these two points.
 *
 * Several modes are available. The first one,
 * {@link org.graphstream.nui.UISpace.Mode.GROWING}, considers that the space
 * will grow according to the changes occurring in the nodes position. If one
 * position (x,y) changes, and if x is lower (resp. higher) than the abscissa of
 * the lowest (resp. highest) space point, then this lowest (resp. highest)
 * point will be updated. This is the same for the ordinate.
 * 
 * The second mode, {@link org.graphstream.nui.UISpace.Mode.ADAPTATIVE}, is
 * similar to the first one but it will keep a fixed distance between the lowest
 * and the highest nodes locations, so the space will grow according to changes,
 * but will decrease too.
 * 
 * The third one, {@link org.graphstream.nui.UISpace.Mode.FIXED}, allows just to
 * define the space but this space will not change according to the changes of
 * the nodes location. This mode can be used, for example, with a layout
 * algorithm to tell the layout what is the space of the graph.
 * 
 * The space parameters can be controlled directly from the graph using
 * attributes with a <code>ui.space.</code> prefix. Available attributes are :
 * <ul>
 * <li><code>ui.space.mode</code> to control the mode of the space ;</li>
 * <li><code>ui.space.3d</code> ;</li>
 * <li><code>ui.space.lowest</code> linked to the lowest point of the space ;</li>
 * <li><code>ui.space.highest</code> linked to the highest point of the space.</li>
 * </ul>
 */
public interface UISpace extends UIModule {
	public static final String MODULE_ID = "space";
	public static final int MODULE_PRIORITY = UIDataset.MODULE_PRIORITY + 1;

	/**
	 * Defines the several modes who rule how the space will evolve.
	 */
	public static enum Mode {
		/**
		 * This mode considers that the space will grow according to the changes
		 * occurring in the nodes position. If one position (x,y) changes, and
		 * if x is lower (resp. higher) than the abscissa of the lowest (resp.
		 * highest) space point, then this lowest (resp. highest) point will be
		 * updated. This is the same for the ordinate.
		 */
		GROWING,
		/**
		 * This mode is similar to the growing one but it will keep a fixed
		 * distance between the lowest and the highest nodes locations, so the
		 * space will grow according to changes, but will decrease too
		 */
		ADAPTATIVE,
		/**
		 * This mode allows just to define the space but this space will not
		 * change according to the changes of the nodes location. This mode can
		 * be used, for example, with a layout algorithm to tell the layout what
		 * is the space of the graph.
		 */
		FIXED
	}

	/**
	 * Getter for the space mode. @see org.graphstream.nui.UISpace.Mode for more
	 * details about the different modes.
	 * 
	 * @return the space mode
	 */
	Mode getMode();

	/**
	 * Setter for the space mode. @see org.graphstream.nui.UISpace.Mode for more
	 * details about the different modes.
	 * 
	 * @param mode
	 *            the new space node
	 */
	void setMode(Mode mode);

	/**
	 * The space can be a 2-dimensional or 3-dimensional one. This method allows
	 * to know if the third dimension is enabled.
	 * 
	 * @return true if this is a three dimensions space
	 */
	boolean is3D();

	/**
	 * Enable or disable the third dimension. Default is that the space is only
	 * a 2-dimensional space.
	 * 
	 * @param on
	 *            true to enable the third dimension
	 */
	void set3D(boolean on);

	Bounds getBounds();

	int lengthToPX(UICamera camera, double lengthInGU);

	double lengthToGU(UICamera camera, int lengthInPX);
}
