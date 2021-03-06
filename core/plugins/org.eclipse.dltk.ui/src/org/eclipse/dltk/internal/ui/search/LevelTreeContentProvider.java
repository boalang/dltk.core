/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.internal.ui.StandardModelElementContentProvider;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class LevelTreeContentProvider extends DLTKSearchContentProvider
		implements ITreeContentProvider {
	private Map fChildrenMap;
	private StandardModelElementContentProvider fContentProvider;

	public static final int LEVEL_TYPE = 1;
	public static final int LEVEL_FILE = 2;
	public static final int LEVEL_PACKAGE = 3;
	public static final int LEVEL_PROJECT = 4;

	private static int[][] SCRIPT_ELEMENT_TYPES = { { IModelElement.TYPE },
			{ IModelElement.SOURCE_MODULE }, { IModelElement.SCRIPT_FOLDER },
			{ IModelElement.SCRIPT_PROJECT, IModelElement.PROJECT_FRAGMENT },
			{ IModelElement.SCRIPT_MODEL } };
	private static int[][] RESOURCE_TYPES = { {}, { IResource.FILE },
			{ IResource.FOLDER }, { IResource.PROJECT }, { IResource.ROOT } };

	private static final int MAX_LEVEL = SCRIPT_ELEMENT_TYPES.length - 1;
	private int fCurrentLevel;

	static class FastModelElementProvider
			extends StandardModelElementContentProvider {
		@Override
		public Object getParent(Object element) {
			Object parent = getExtendedParent(element);
			if (parent != null) {
				return parent;
			}
			return internalGetParent(element);
		}
	}

	public LevelTreeContentProvider(DLTKSearchResultPage page, int level) {
		super(page);
		fCurrentLevel = level;
		fContentProvider = new FastModelElementProvider();
	}

	@Override
	public Object getParent(Object child) {
		Object possibleParent = internalGetParent(child);
		if (possibleParent instanceof IModelElement) {
			IModelElement modelElement = (IModelElement) possibleParent;
			for (int j = fCurrentLevel; j < MAX_LEVEL + 1; j++) {
				for (int i = 0; i < SCRIPT_ELEMENT_TYPES[j].length; i++) {
					if (modelElement
							.getElementType() == SCRIPT_ELEMENT_TYPES[j][i]) {
						return null;
					}
				}
			}
		} else if (possibleParent instanceof IResource) {
			IResource resource = (IResource) possibleParent;
			for (int j = fCurrentLevel; j < MAX_LEVEL + 1; j++) {
				for (int i = 0; i < RESOURCE_TYPES[j].length; i++) {
					if (resource.getType() == RESOURCE_TYPES[j][i]) {
						return null;
					}
				}
			}
		}
		// TODO make this optional if there are languages with 1 class per file
		// if (fCurrentLevel != LEVEL_FILE && child instanceof IType) {
		// IType type = (IType) child;
		// if (possibleParent instanceof ISourceModule)
		// possibleParent = type.getScriptFolder();
		// }
		return possibleParent;
	}

	private Object internalGetParent(Object child) {
		return fContentProvider.getParent(child);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	protected synchronized void initialize(DLTKSearchResult result) {
		super.initialize(result);
		fChildrenMap = new HashMap();
		if (result != null) {
			Object[] elements = result.getElements();
			for (int i = 0; i < elements.length; i++) {
				if (getPage().getDisplayedMatchCount(elements[i]) > 0) {
					insert(null, null, elements[i]);
				}
			}
		}
	}

	protected void insert(Map toAdd, Set toUpdate, Object child) {
		Object parent = getParent(child);
		while (parent != null) {
			if (insertChild(parent, child)) {
				if (toAdd != null)
					insertInto(parent, child, toAdd);
			} else {
				if (toUpdate != null)
					toUpdate.add(parent);
				return;
			}
			child = parent;
			parent = getParent(child);
		}
		if (insertChild(fResult, child)) {
			if (toAdd != null)
				insertInto(fResult, child, toAdd);
		}
	}

	private boolean insertChild(Object parent, Object child) {
		return insertInto(parent, child, fChildrenMap);
	}

	private boolean insertInto(Object parent, Object child, Map map) {
		Set children = (Set) map.get(parent);
		if (children == null) {
			children = new HashSet();
			map.put(parent, children);
		}
		return children.add(child);
	}

	protected void remove(Set toRemove, Set toUpdate, Object element) {
		// precondition here: fResult.getMatchCount(child) <= 0

		if (hasChildren(element)) {
			if (toUpdate != null)
				toUpdate.add(element);
		} else {
			if (getPage().getDisplayedMatchCount(element) == 0) {
				fChildrenMap.remove(element);
				Object parent = getParent(element);
				if (parent != null) {
					if (removeFromSiblings(element, parent)) {
						remove(toRemove, toUpdate, parent);
					}
				} else {
					if (removeFromSiblings(element, fResult)) {
						if (toRemove != null)
							toRemove.add(element);
					}
				}
			} else {
				if (toUpdate != null) {
					toUpdate.add(element);
				}
			}
		}
	}

	/**
	 * @param element
	 * @param parent
	 * @return returns true if it really was a remove (i.e. element was a child
	 *         of parent).
	 */
	private boolean removeFromSiblings(Object element, Object parent) {
		Set siblings = (Set) fChildrenMap.get(parent);
		if (siblings != null) {
			return siblings.remove(element);
		}
		return false;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Set children = (Set) fChildrenMap.get(parentElement);
		if (children == null)
			return EMPTY_ARR;
		int limit = getPage().getElementLimit().intValue();
		if (limit != -1 && limit < children.size()) {
			Object[] limitedArray = new Object[limit];
			Iterator iterator = children.iterator();
			for (int i = 0; i < limit; i++) {
				limitedArray[i] = iterator.next();
			}
			return limitedArray;
		}
		return children.toArray();
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public synchronized void elementsChanged(Object[] updatedElements) {
		AbstractTreeViewer viewer = (AbstractTreeViewer) getPage().getViewer();
		if (fResult == null)
			return;
		Set toRemove = new HashSet();
		Set toUpdate = new HashSet();
		Map toAdd = new HashMap();
		for (int i = 0; i < updatedElements.length; i++) {
			if (getPage().getDisplayedMatchCount(updatedElements[i]) > 0)
				insert(toAdd, toUpdate, updatedElements[i]);
			else
				remove(toRemove, toUpdate, updatedElements[i]);
		}

		viewer.remove(toRemove.toArray());
		for (Iterator iter = toAdd.keySet().iterator(); iter.hasNext();) {
			Object parent = iter.next();
			HashSet children = (HashSet) toAdd.get(parent);
			viewer.add(parent, children.toArray());
		}
		for (Iterator elementsToUpdate = toUpdate.iterator(); elementsToUpdate
				.hasNext();) {
			viewer.refresh(elementsToUpdate.next());
		}

	}

	@Override
	public void clear() {
		initialize(fResult);
		getPage().getViewer().refresh();
	}

	public void setLevel(int level) {
		fCurrentLevel = level;
		initialize(fResult);
		getPage().getViewer().refresh();
	}

}
