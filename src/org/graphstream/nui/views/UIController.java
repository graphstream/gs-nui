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

import org.graphstream.nui.views.controller.UIViewAction;

public interface UIController {
	public static enum ControllerType {
		KEYBOARD, MOUSE, TOUCH
	}

	public static enum ModifierType {
		NONE, CONTROL_LEFT, CONTROL_RIGHT, ALT_LEFT, ALT_RIGHT, SHIFT_LEFT, SHIFT_RIGHT
	}

	public static interface ControllerInput {
	}

	public static enum KeyboardInput implements ControllerInput {
		// /////////////////////////////////////////
		// Letters
		KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, //
		KEY_G, KEY_H, KEY_I, KEY_J, KEY_K, KEY_L, //
		KEY_M, KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R, //
		KEY_S, KEY_T, KEY_U, KEY_V, KEY_W, KEY_X, //
		KEY_Y, KEY_Z, //
		// //////////////////////////////////
		// Digits
		KEY_0, KEY_1, KEY_2, KEY_3, KEY_4, //
		KEY_5, KEY_6, KEY_7, KEY_8, KEY_9, //
		// ///////////////////////////////////////////////
		// Functions
		KEY_F1, KEY_F2, KEY_F3, KEY_F4, KEY_F5, KEY_F6, //
		KEY_F7, KEY_F8, KEY_F9, KEY_F10, KEY_F11, KEY_F12, //
		KEY_SPACE, KEY_BACKSPACE, KEY_ENTER, KEY_TAB, KEY_HOME, KEY_DEL, KEY_INS, KEY_PRINT, //
		KEY_LEFT, KEY_RIGHT, KEY_UP, KEY_DOWN, KEY_PG_UP, KEY_PG_DOWN
	}

	public static enum MouseInput implements ControllerInput {
		MOVE, DRAG, CLICK
	}

	void bindAction(ControllerType controllerType, int modifier, int target,
			UIViewAction action);

	void unbindAction(ControllerType controllerType, int modifier, int target,
			UIViewAction action);
}
