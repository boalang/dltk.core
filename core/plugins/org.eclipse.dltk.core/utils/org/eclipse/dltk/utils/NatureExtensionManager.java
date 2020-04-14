/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.osgi.util.NLS;

public class NatureExtensionManager<E> {

	protected final String extensionPoint;
	protected final String classAttr = "class"; //$NON-NLS-1$
	private final String universalNatureId;
	private final Class<?> elementType;

	/**
	 * @param extensionPoint
	 * @param elementType
	 */
	public NatureExtensionManager(String extensionPoint, Class<E> elementType) {
		this(extensionPoint, elementType, null);
	}

	public NatureExtensionManager(String extensionPoint, Class<?> elementType, String universalNatureId) {
		this.extensionPoint = extensionPoint;
		this.elementType = elementType;
		this.universalNatureId = universalNatureId;
	}

	// Contains list of instances for selected nature.
	private Map<String, Object> extensions;

	private void initialize() {
		if (extensions != null) {
			return;
		}
		synchronized (extensionPoint) {
			if (extensions != null) {
				return;
			}
			extensions = new HashMap<>(5);
			registerConfigurationElements();
			for (Iterator<Object> i = extensions.values().iterator(); i.hasNext();) {
				@SuppressWarnings("unchecked")
				final List<Object> descriptors = (List<Object>) i.next();
				initializeDescriptors(descriptors);
			}
		}
	}

	protected void registerConfigurationElements() {
		registerConfigurationElements(Platform.getExtensionRegistry().getConfigurationElementsFor(extensionPoint),
				getCategoryAttributeName());
	}

	protected void registerConfigurationElements(IConfigurationElement[] confElements, final String categoryAttr) {
		for (int i = 0; i < confElements.length; i++) {
			final IConfigurationElement confElement = confElements[i];
			if (!isValidElement(confElement))
				continue;
			final String category = confElement.getAttribute(categoryAttr);
			if (category != null) {
				@SuppressWarnings("unchecked")
				List<Object> elements = (List<Object>) extensions.get(category);
				if (elements == null) {
					elements = new ArrayList<>();
					extensions.put(category, elements);
				}
				elements.add(createDescriptor(confElement));
			} else {
				final String[] bindings = new String[] { categoryAttr, extensionPoint,
						confElement.getContributor().getName() };
				final String msg = NLS.bind(Messages.NatureExtensionManager_missingCategoryAttribute, bindings);
				DLTKCore.warn(msg);
			}
		}
	}

	protected boolean isValidElement(IConfigurationElement element) {
		return true;
	}

	/**
	 * Returns the name of the attribute used to categorized the extensions
	 *
	 * @return
	 */
	protected String getCategoryAttributeName() {
		return "nature"; //$NON-NLS-1$
	}

	/**
	 * @param descriptors
	 */
	protected void initializeDescriptors(List<Object> descriptors) {
		// empty
	}

	/**
	 * Return array of instances for the specified natureId. If there are no
	 * contributed instances for the specified natureId the result of the
	 * {@link #createEmptyResult()} is returned.
	 *
	 * @param natureId
	 * @return
	 * @throws CoreException
	 */
	public E[] getInstances(String natureId) {
		initialize();
		final E[] nature = filter(getByNature(natureId), natureId);
		final E[] all = universalNatureId != null ? filter(getByNature(universalNatureId), natureId) : null;
		if (nature != null) {
			if (all != null) {
				return merge(all, nature);
			} else {
				return nature;
			}
		} else if (all != null) {
			return all;
		} else {
			return createEmptyResult();
		}
	}

	protected E[] merge(final E[] all, final E[] nature) {
		final E[] result = createArray(all.length + nature.length);
		// ssanders: Ensure that global items are included first,
		// because nature items may depend on them running first
		System.arraycopy(all, 0, result, 0, all.length);
		System.arraycopy(nature, 0, result, all.length, nature.length);
		return result;
	}

	/**
	 * @since 3.0
	 */
	protected E[] filter(E[] objects, String natureId) {
		return objects;
	}

	public E[] getAllInstances() {
		initialize();
		List<E> result = new ArrayList<>();
		for (Iterator<String> i = extensions.keySet().iterator(); i.hasNext();) {
			E[] natureInstances = getByNature(i.next());
			if (natureInstances != null) {
				for (int j = 0; j < natureInstances.length; ++j) {
					result.add(natureInstances[j]);
				}
			}
		}
		final E[] resultArray = createArray(result.size());
		result.toArray(resultArray);
		return resultArray;
	}

	protected E[] createEmptyResult() {
		return null;
	}

	/**
	 * @since 3.0
	 */
	@SuppressWarnings("unchecked")
	protected E[] createArray(int length) {
		return (E[]) Array.newInstance(elementType, length);
	}

	protected boolean isInstance(Object e) {
		return elementType.isAssignableFrom(e.getClass());
	}

	protected boolean isValidInstance(Object e) {
		return isInstance(e);
	}

	@SuppressWarnings("unchecked")
	private E[] getByNature(String natureId) {
		final Object ext = extensions.get(natureId);
		if (ext != null) {
			if (ext instanceof Object[]) {
				return (E[]) ext;
			} else if (ext instanceof List) {
				final List<Object> elements = (List<Object>) ext;
				final List<E> result = new ArrayList<>(elements.size());
				for (int i = 0; i < elements.size(); ++i) {
					final Object element = elements.get(i);
					if (isInstance(element)) {
						result.add((E) element);
					} else {
						try {
							final Object instance = createInstanceByDescriptor(element);
							if (instance != null && isValidInstance(instance)) {
								result.add((E) instance);
							}
						} catch (Exception e) {
							final String msg = NLS.bind(Messages.NatureExtensionManager_instantiantionError,
									elementType.getName());
							DLTKCore.error(msg, e);
						}
					}
				}
				final E[] resultArray = createArray(result.size());
				result.toArray(resultArray);
				saveInstances(natureId, resultArray);
				return resultArray;
			}
		}
		return null;
	}

	protected void saveInstances(String natureId, final E[] resultArray) {
		extensions.put(natureId, resultArray);
	}

	/**
	 * @param confElement
	 * @return
	 */
	protected Object createDescriptor(IConfigurationElement confElement) {
		return confElement;
	}

	/**
	 * @param descriptor
	 * @throws CoreException
	 */
	protected Object createInstanceByDescriptor(Object descriptor) throws CoreException {
		final IConfigurationElement cfg = (IConfigurationElement) descriptor;
		return cfg.createExecutableExtension(classAttr);
	}
}
