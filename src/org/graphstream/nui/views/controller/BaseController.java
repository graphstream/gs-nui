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
package org.graphstream.nui.views.controller;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graphstream.nui.UIView;
import org.graphstream.nui.views.UIController;

public abstract class BaseController implements UIController {
	protected Map<ControllerType, Map<Object, List<UIViewAction>>> actions;
	protected UIView view;

	protected BaseController() {
		actions = new EnumMap<ControllerType, Map<Object, List<UIViewAction>>>(
				ControllerType.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.UIController#bindAction(org.graphstream.nui
	 * .views.UIController.ControllerType, int, int,
	 * org.graphstream.nui.views.controller.UIViewAction)
	 */
	@Override
	public void bindAction(ControllerType controllerType, int modifier,
			int target, UIViewAction action) {
		Map<Object, List<UIViewAction>> typeActions = actions
				.get(controllerType);

		if (typeActions == null) {
			typeActions = new HashMap<Object, List<UIViewAction>>();
			actions.put(controllerType, typeActions);
		}

		Object identifier = createIdentifier(modifier, target);
		List<UIViewAction> identifierActions = typeActions.get(identifier);

		if (identifierActions == null) {
			identifierActions = new LinkedList<UIViewAction>();
			typeActions.put(identifier, identifierActions);
		}

		identifierActions.add(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.UIController#unbindAction(org.graphstream.nui
	 * .views.UIController.ControllerType, int, int,
	 * org.graphstream.nui.views.controller.UIViewAction)
	 */
	@Override
	public void unbindAction(ControllerType controllerType, int modifier,
			int target, UIViewAction action) {
		Map<Object, List<UIViewAction>> typeActions = actions
				.get(controllerType);

		if (typeActions == null)
			return;

		Object identifier = createIdentifier(modifier, target);
		List<UIViewAction> identifierActions = typeActions.get(identifier);

		if (identifierActions == null)
			return;

		identifierActions.remove(action);
	}

	protected void trigger(ControllerType controllerType, Object identifier,
			Object actionData) {
		Map<Object, List<UIViewAction>> typeActions = actions
				.get(controllerType);

		if (typeActions == null)
			return;

		List<UIViewAction> identifierActions = typeActions.get(identifier);

		if (identifierActions == null)
			return;

		for (UIViewAction action : identifierActions)
			action.actionPerformed(view, actionData);
	}

	protected abstract Object createIdentifier(int modifier, int target);
}
