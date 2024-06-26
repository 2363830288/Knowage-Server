/*
 * Knowage, Open Source Business Intelligence suite
 * Copyright (C) 2016 Engineering Ingegneria Informatica S.p.A.
 *
 * Knowage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Knowage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.eng.spago.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.eng.spago.base.Constants;
import it.eng.spago.tracing.TracerSingleton;

public class LightNavigator {

	// Thread-safe list used as stack (last-in-first-out)
	private final List<MarkedRequest> list = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Adds a <code>MarkedRequest</code> element to the stack.
	 *
	 * @param markedRequest the <code>MarkedRequest</code> object to be added to the stack
	 */
	public void add(MarkedRequest markedRequest) throws NavigationException {
		if (markedRequest == null) {
			TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.CRITICAL,
					"LightNavigator: " + "add: the MarkedRequest object at input is null.");
			throw new NavigationException("The MarkedRequest object at input is null.");
		}
		list.add(0, markedRequest);
	}

	/**
	 * Retrieves the <code>MarkedRequest</code> object at the desired position in the stack and deletes the more recent <code>MarkedRequest</code> objects present
	 * in the stack.
	 *
	 * @param i The int representing the position of the stack.
	 * @throws <code>NavigationException</code> if the position at input is not present in the stack
	 */
	public MarkedRequest goBackToPosition(int i) throws NavigationException {
//		 Index i must be 0 < i < list.size()-1, the request at index 0 is the request for the current page
		if (i < 0 || i > list.size() - 1) {
			TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.CRITICAL,
					"LightNavigator: " + "goBackToPosition: the position " + i + " is not present in the stack.");
			TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,
					"The requests stack is:\n" + this.toString());
			throw new NavigationException("Index of the required request is not correct.");
		}
		MarkedRequest markedRequest = list.get(i);
		synchronized (list) {
			for (int j = 0; j < i; j++) {
				list.remove(0);
			}
		}
		return markedRequest;
	}

	/**
	 * Deletes the more recent <code>MarkedRequest</code> object in the stack and adds the <code>MarkedRequest</code> passed at input in the first position of the
	 * stack.
	 *
	 * @param markedRequest the <code>MarkedRequest</code> object that will replace the more recent <code>MarkedRequest</code> in the stack
	 * @throws <code>NavigationException</code> if teh <code>MarkedRequest</code> object at input is null of if the stack is empty.
	 */
	public void replaceLast(MarkedRequest markedRequest) throws NavigationException {
		if (markedRequest == null) {
			TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.CRITICAL,
					"LightNavigator: " + "replaceLast: the MarkedRequest object at input is null.");
			throw new NavigationException("The MarkedRequest object at input is null.");
		}
		if (list.isEmpty()) {
			TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.CRITICAL, "LightNavigator: "
					+ "replaceLast: the stack is empty: it is not possible to substitute the request.");
			throw new NavigationException("The stack is empty: it is not possible to substitute the request.");
		}
		synchronized (list) {
			// removes the most recent request
			list.remove(0);
			// adds the request at input in the first position
			list.add(0, markedRequest);
		}
	}

	/**
	 * Resets the stack.
	 *
	 */
	public void reset() {
		synchronized (list) {
			list.clear();
		}
	}

	/**
	 * Retrieves the more recent <code>MarkedRequest</code> object in the stack with the mark passed at input and deletes the more recent <code>MarkedRequest</code>
	 * objects present in the stack.
	 *
	 * @param mark The string mark of the desired <code>MarkedRequest</code> object in the stack.
	 * @throws <code>NavigationException</code> if there are no <code>MarkedRequest</code> objects with the mark passed at input.
	 */
	public MarkedRequest goBackToMark(String mark) throws NavigationException {
		if (mark == null) {
			TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.MAJOR,
					"LightNavigator: " + "goBackToMark: the input mark is null.");
			throw new NavigationException("The input mark for navigation research cannot be null.");
		}
		MarkedRequest toReturn = null;
		int i = 1;
		synchronized (list) {
			while (i <= list.size()) {
				MarkedRequest markedRequest = list.get(i - 1);
				if (mark.equalsIgnoreCase(markedRequest.getMark())) {
					toReturn = markedRequest;
					break;
				}
				i++;
			}
			if (toReturn == null) {
				TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.MAJOR,
						"LightNavigator: " + "goBackToMark: Request with mark '" + mark + "' not found.");
				TracerSingleton.log(Constants.NOME_MODULO, TracerSingleton.DEBUG,
						"The requests stack is:\n" + this.toString());
				throw new NavigationException("Request with mark '" + mark + "' not found.");
			}
			for (int j = 0; j < i - 1; j++) {
				list.remove(0);
			}
		}
		return toReturn;
	}

	@Override
	public String toString() {
		StringBuilder toReturn = new StringBuilder("");
		synchronized (list) {
			for (int i = 0; i < list.size(); i++) {
				toReturn.append("Position " + i + ":\n");
				Object obj = list.get(i);
				toReturn.append(obj != null ? list.get(i).toString() : "null");
				toReturn.append("\n-------------------------------------------\n");
			}
		}
		return toReturn.toString();
	}
}
