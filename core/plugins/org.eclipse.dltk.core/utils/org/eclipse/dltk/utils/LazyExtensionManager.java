/*******************************************************************************
 * Copyright (c) 2009, 2017 xored software, Inc. and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.utils;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class LazyExtensionManager<E> implements Iterable<E> {

	public static class Descriptor<E> {
		private final LazyExtensionManager<E> manager;
		private final IConfigurationElement configurationElement;
		private E instance;
		private boolean valid;

		public Descriptor(LazyExtensionManager<E> manager,
				IConfigurationElement configurationElement) {
			this.manager = manager;
			this.configurationElement = configurationElement;
			this.valid = true;
		}

		public synchronized E get() {
			if (instance != null) {
				return instance;
			} else if (!valid) {
				return null;
			}
			instance = create();
			return instance;
		}

		@SuppressWarnings("unchecked")
		protected E create() {
			try {
				return (E) configurationElement
						.createExecutableExtension(manager.classAttr);
			} catch (CoreException e) {
				valid = false;
				manager.remove(this);
				return null;
			}
		}

		/**
		 * @since 2.0
		 */
		public String getAttribute(String name) {
			return configurationElement.getAttribute(name);
		}

		/**
		 * @since 3.0
		 */
		public int getIntAttribute(String name) {
			return parseInt(getAttribute(name));
		}

		/**
		 * @since 2.0
		 */
		protected static int parseInt(String value) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	}

	private static class InstanceIterator<E> implements Iterator<E> {

		private final Descriptor<E>[] descriptors;

		public InstanceIterator(Descriptor<E>[] descriptors) {
			this.descriptors = descriptors;
		}

		private int index = 0;
		private boolean nextEvaluated = false;
		private E next = null;

		@Override
		public boolean hasNext() {
			if (!nextEvaluated) {
				evaluateNext();
				nextEvaluated = true;
			}
			return next != null;
		}

		private void evaluateNext() {
			while (index < descriptors.length) {
				next = descriptors[index++].get();
				if (next != null) {
					return;
				}
			}
		}

		@Override
		public E next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			final E result = next;
			next = null;
			nextEvaluated = false;
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static class DescriptorIterator<E>
			implements Iterator<Descriptor<E>> {

		private final Descriptor<E>[] descriptors;

		public DescriptorIterator(Descriptor<E>[] descriptors) {
			this.descriptors = descriptors;
		}

		private int index = 0;
		private boolean nextEvaluated = false;
		private Descriptor<E> next = null;

		@Override
		public boolean hasNext() {
			if (!nextEvaluated) {
				evaluateNext();
				nextEvaluated = true;
			}
			return next != null;
		}

		private void evaluateNext() {
			while (index < descriptors.length) {
				next = descriptors[index++];
				if (next != null) {
					return;
				}
			}
		}

		@Override
		public Descriptor<E> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			final Descriptor<E> result = next;
			next = null;
			nextEvaluated = false;
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static class DescriptorCollection<E>
			extends AbstractCollection<Descriptor<E>> {

		private final Descriptor<E>[] descriptors;

		public DescriptorCollection(Descriptor<E>[] descriptors) {
			this.descriptors = descriptors;
		}

		@Override
		public Iterator<Descriptor<E>> iterator() {
			return new DescriptorIterator<>(descriptors);
		}

		@Override
		public int size() {
			return descriptors.length;
		}

	}

	private final String extensionPoint;
	protected final String classAttr = "class"; //$NON-NLS-1$

	/**
	 * @param extensionPoint
	 * @param elementType
	 */
	public LazyExtensionManager(String extensionPoint) {
		this.extensionPoint = extensionPoint;
	}

	// Contains list of descriptors.
	private List<Descriptor<E>> extensions;

	/**
	 * Return array of descriptors. If there are no contributed instances the
	 * empty array is returned.
	 *
	 * @param natureId
	 * @return
	 * @throws CoreException
	 */
	public Descriptor<E>[] getDescriptors() {
		return internalGetInstances();
	}

	private synchronized Descriptor<E>[] internalGetInstances() {
		if (extensions == null) {
			initialize();
		}
		@SuppressWarnings("unchecked")
		final Descriptor<E>[] resultArray = new Descriptor[extensions.size()];
		extensions.toArray(resultArray);
		return resultArray;
	}

	/**
	 * Returns instance iterator
	 *
	 * @return
	 */
	@Override
	public Iterator<E> iterator() {
		return new InstanceIterator<>(internalGetInstances());
	}

	public Iterator<Descriptor<E>> descriptorIterator() {
		return new DescriptorIterator<>(internalGetInstances());
	}

	/**
	 * @since 2.0
	 */
	public Collection<Descriptor<E>> descriptors() {
		return new DescriptorCollection<>(internalGetInstances());
	}

	synchronized void remove(Descriptor<E> descriptor) {
		if (extensions != null) {
			extensions.remove(descriptor);
		}
	}

	private void initialize() {
		extensions = new ArrayList<>(5);
		registerConfigurationElements();
		initializeDescriptors(extensions);
	}

	protected void registerConfigurationElements() {
		registerConfigurationElements(Platform.getExtensionRegistry()
				.getConfigurationElementsFor(extensionPoint));
	}

	protected void registerConfigurationElements(
			IConfigurationElement[] confElements) {
		for (int i = 0; i < confElements.length; i++) {
			final IConfigurationElement confElement = confElements[i];
			if (isValidElement(confElement)) {
				final Descriptor<E> descriptor = createDescriptor(confElement);
				if (isValidDescriptor(descriptor)) {
					extensions.add(descriptor);
				}
			}
		}
	}

	/**
	 * @since 3.0
	 */
	protected boolean isValidElement(IConfigurationElement confElement) {
		return true;
	}

	/**
	 * @param confElement
	 * @return
	 */
	protected Descriptor<E> createDescriptor(
			IConfigurationElement confElement) {
		return new Descriptor<>(this, confElement);
	}

	protected boolean isValidDescriptor(Descriptor<E> descriptor) {
		return descriptor != null;
	}

	/**
	 * @param descriptors
	 */
	protected void initializeDescriptors(List<Descriptor<E>> descriptors) {
		// empty
	}

}
