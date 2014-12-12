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
package org.graphstream.nui.spacePartition;

import java.util.Iterator;
import java.util.LinkedList;

import org.graphstream.nui.UISpacePartition;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.spacePartition.data.SpaceCellData;
import org.graphstream.nui.spacePartition.data.SpaceCellDataIndex;
import org.graphstream.nui.spacePartition.data.SpaceCellDataSet;

public abstract class BaseSpaceCell implements SpaceCell {
	protected final LinkedList<ElementIndex> elements;
	protected final Bounds boundary;
	protected final UISpacePartition spacePartition;
	protected boolean changed;
	protected SpaceCellDataSet datas;

	protected BaseSpaceCell(UISpacePartition spacePartition, Bounds boundary) {
		this.spacePartition = spacePartition;
		this.boundary = boundary;
		this.elements = new LinkedList<ElementIndex>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<ElementIndex> iterator() {
		return elements.iterator();
	}

	protected void register() {
		if (spacePartition instanceof SpaceCellHandler)
			((SpaceCellHandler) spacePartition).register(this);
	}

	protected void unregister() {
		if (spacePartition instanceof SpaceCellHandler)
			((SpaceCellHandler) spacePartition).unregister(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.spacePartition.SpaceCell#getBoundary()
	 */
	@Override
	public Bounds getBoundary() {
		return boundary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.spacePartition.SpaceCell#getData(org.graphstream.
	 * nui.spacePartition.data.SpaceCellDataIndex)
	 */
	@Override
	public SpaceCellData getData(SpaceCellDataIndex index) {
		if (changed)
			computeData();

		return datas.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.spacePartition.SpaceCell#setSpaceCellDataCell(org
	 * .graphstream.nui.spacePartition.data.SpaceCellDataSet)
	 */
	@Override
	public void setSpaceCellDataCell(SpaceCellDataSet set) {
		this.datas = set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("space_cell@%s%s", boundary, elements);
	}

	protected void computeData() {
		for (SpaceCellData data : datas)
			data.compute(spacePartition.getContext(), this);

		changed = false;
	}
}
