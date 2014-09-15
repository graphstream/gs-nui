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
package org.graphstream.nui.space;

import java.util.logging.Logger;

import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.UISpace;
import org.graphstream.nui.dataset.DatasetListener;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.util.Tools;
import org.graphstream.ui.geom.Point3;

public class DefaultSpace extends AbstractModule implements UISpace {
	public static final double DEFAULT_PADDING = 0.1;
	protected Mode mode;
	protected final Point3 lowest;
	protected final Point3 highest;
	protected boolean is3D;

	protected UIIndexer indexer;
	protected UIDataset dataset;

	protected CoordinatesListener listener;

	protected ElementIndex lxBounds;
	protected ElementIndex hxBounds;
	protected ElementIndex lyBounds;
	protected ElementIndex hyBounds;
	protected ElementIndex lzBounds;
	protected ElementIndex hzBounds;

	protected double padding;

	public DefaultSpace() {
		super(MODULE_ID, UIIndexer.MODULE_ID, UIDataset.MODULE_ID);

		lowest = new Point3(-1, -1, -1);
		highest = new Point3(1, 1, 1);
		listener = new CoordinatesListener();

		lxBounds = hxBounds = lyBounds = hyBounds = lzBounds = hzBounds = null;

		padding = DEFAULT_PADDING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.AbstractModule#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx) {
		super.init(ctx);

		lowest.set(-1, -1, -1);
		highest.set(1, 1, 1);
		is3D = false;
		mode = Mode.GROWING;

		indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);
		dataset = (UIDataset) ctx.getModule(UIDataset.MODULE_ID);

		dataset.addDatasetListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		dataset.removeDatasetListener(listener);

		dataset = null;
		indexer = null;

		super.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String key, Object value) {
		if (value == null)
			return;

		switch (key) {
		case "mode":
			if (value instanceof String) {
				try {
					Mode m = Mode.valueOf((String) value);
					setMode(m);
				} catch (IllegalArgumentException e) {
					Logger.getLogger(getClass().getName()).warning(
							"failed to set space mode to " + value);
				}
			} else if (value instanceof Mode) {
				setMode((Mode) value);
			}

			break;
		case "padding":
			double p = Tools.checkAndGetDouble(value);
			padding = p;

			break;
		case "lowest":
			if (mode != Mode.FIXED) {
				Logger.getLogger(getClass().getName()).warning(
						"receive lowest point but space is not is fixed mode,"
								+ "automatically switch to FIXED mode");

				setMode(Mode.FIXED);
			}

			try {
				double[] l = Tools.checkAndGetDoubleArray(value);

				if (l == null || (l.length != 2 && !is3D)
						|| (l.length != 3 && is3D))
					throw new IllegalArgumentException("bad dimension");

				lowest.set(l[0], l[1], l[2]);
			} catch (IllegalArgumentException e) {
				Logger.getLogger(getClass().getName()).warning(
						"can not set the lowest point of space : "
								+ e.getMessage());
			}

			break;
		case "highest":
			if (mode != Mode.FIXED) {
				Logger.getLogger(getClass().getName()).warning(
						"receive highest point but space is not is fixed mode,"
								+ "automatically switch to FIXED mode");

				setMode(Mode.FIXED);
			}

			try {
				double[] h = Tools.checkAndGetDoubleArray(value);

				if (h == null || (h.length != 2 && !is3D)
						|| (h.length != 3 && is3D))
					throw new IllegalArgumentException("bad dimension");

				highest.set(h[0], h[1], h[2]);
			} catch (IllegalArgumentException e) {
				Logger.getLogger(getClass().getName()).warning(
						"can not set the highest point of space : "
								+ e.getMessage());
			}

			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UISpace#getMode()
	 */
	@Override
	public Mode getMode() {
		return mode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UISpace#setMode(org.graphstream.nui.UISpace.Mode)
	 */
	@Override
	public void setMode(Mode mode) {
		this.mode = mode;

		switch (mode) {
		case ADAPTATIVE:
		case GROWING:
			computeSpace();
			break;
		default:
			break;
		}

		fireSpaceUpdated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UISpace#is3D()
	 */
	@Override
	public boolean is3D() {
		return is3D;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UISpace#set3D(boolean)
	 */
	@Override
	public void set3D(boolean on) {
		is3D = on;
		fireSpaceUpdated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UISpace#getLowestPoint()
	 */
	@Override
	public Point3 getLowestPoint() {
		return lowest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UISpace#setLowestPoint(org.graphstream.ui.geom.Point3
	 * )
	 */
	@Override
	public void setLowestPoint(Point3 xyz) {
		lowest.copy(xyz);
		fireSpaceUpdated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UISpace#getHighestPoint()
	 */
	@Override
	public Point3 getHighestPoint() {
		return highest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UISpace#setHighestPoint(org.graphstream.ui.geom.Point3
	 * )
	 */
	@Override
	public void setHighestPoint(Point3 xyz) {
		highest.copy(xyz);
		fireSpaceUpdated();
	}

	protected void computeSpace() {
		lxBounds = hxBounds = lyBounds = hyBounds = lzBounds = hzBounds = null;

		double lx, hx, ly, hy, lz, hz;
		double[] xyz = new double[3];

		lx = ly = lz = dataset.getNodeCount() == 0 ? -1 : Double.MAX_VALUE;
		hx = hy = hz = dataset.getNodeCount() == 0 ? 1 : -Double.MAX_VALUE;

		for (int i = 0; i < dataset.getNodeCount(); i++) {
			xyz = dataset.getNodeXYZ(i, xyz);

			if (xyz[0] < lx) {
				lxBounds = indexer.getNodeIndex(i);
				lx = xyz[0];
			}

			if (xyz[0] > hx) {
				hxBounds = indexer.getNodeIndex(i);
				hx = xyz[0];
			}

			if (xyz[1] < ly) {
				lyBounds = indexer.getNodeIndex(i);
				ly = xyz[1];
			}

			if (xyz[1] > hy) {
				hyBounds = indexer.getNodeIndex(i);
				hy = xyz[1];
			}

			if (xyz[2] < lz) {
				lzBounds = indexer.getNodeIndex(i);
				lz = xyz[2];
			}

			if (xyz[2] > hz) {
				hzBounds = indexer.getNodeIndex(i);
				hz = xyz[2];
			}
		}

		lowest.set(lx - padding, ly - padding, lz - padding);
		highest.set(hx + padding, hy + padding, hz + padding);
	}

	protected void fireSpaceUpdated() {
		Logger.getGlobal().info("space updated " + lowest + " : " + highest);
	}

	class CoordinatesListener implements DatasetListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.dataset.DatasetListener#nodeMoved(ElementIndex,
		 * double, double, double)
		 */
		@Override
		public void nodeMoved(ElementIndex nodeIndex, double x, double y,
				double z) {
			boolean changed = false;

			switch (mode) {
			case GROWING:
				if (x < lowest.x) {
					lowest.x = x;
					changed = true;
				} else if (x > highest.x) {
					highest.x = x;
					changed = true;
				}

				if (y < lowest.y) {
					lowest.y = y;
					changed = true;
				} else if (y > highest.y) {
					highest.y = y;
					changed = true;
				}

				if (is3D) {
					if (z < lowest.z) {
						lowest.z = z;
						changed = true;
					} else if (z > highest.z) {
						highest.z = z;
						changed = true;
					}
				}

				break;
			case ADAPTATIVE:
				if (nodeIndex == lxBounds || nodeIndex == hxBounds
						|| nodeIndex == lyBounds || nodeIndex == hyBounds
						|| nodeIndex == lzBounds || nodeIndex == hzBounds) {
					computeSpace();
					changed = true;
				} else {
					if (x < lowest.x + padding) {
						lowest.x = x - padding;
						lxBounds = nodeIndex;
						changed = true;
					} else if (x > highest.x - padding) {
						highest.x = x + padding;
						hxBounds = nodeIndex;
						changed = true;
					}

					if (y < lowest.y + padding) {
						lowest.y = y - padding;
						lyBounds = nodeIndex;
						changed = true;
					} else if (y > highest.y - padding) {
						highest.y = y + padding;
						hyBounds = nodeIndex;
						changed = true;
					}

					if (is3D) {
						if (z < lowest.z + padding) {
							lowest.z = z - padding;
							lzBounds = nodeIndex;
							changed = true;
						} else if (z > highest.z - padding) {
							highest.z = z + padding;
							hzBounds = nodeIndex;
							changed = true;
						}
					}
				}

				break;
			default:
				//
				// If mode is fixed, there is nothing to do here.
				//
				break;
			}

			if (changed)
				fireSpaceUpdated();
		}
	}
}
