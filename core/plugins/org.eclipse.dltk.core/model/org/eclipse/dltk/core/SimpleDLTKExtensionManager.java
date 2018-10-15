/*******************************************************************************
 * Copyright (c) 2016 xored software, Inc. and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     xored software, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class SimpleDLTKExtensionManager {
	private List extensions;

	private final String extensionPoint;

	public SimpleDLTKExtensionManager(String extension) {
		this.extensionPoint = extension;
	}

	public static class ElementInfo {
		final IConfigurationElement config;
		public Object object;

		protected ElementInfo(IConfigurationElement config) {
			this.config = config;
		}

		public IConfigurationElement getConfig() {
			return this.config;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((config == null) ? 0 : config.hashCode());
			result = prime * result
					+ ((object == null) ? 0 : object.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ElementInfo other = (ElementInfo) obj;
			if (config == null) {
				if (other.config != null)
					return false;
			} else if (!config.equals(other.config))
				return false;
			if (object == null) {
				if (other.object != null)
					return false;
			} else if (!object.equals(other.object))
				return false;
			return true;
		}

	}

	private void initialize() {
		if (extensions != null) {
			return;
		}

		extensions = new ArrayList(5);
		IConfigurationElement[] cfg = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(this.extensionPoint);

		for (int i = 0; i < cfg.length; i++) {
			if (isValidElement(cfg[i])) {
				ElementInfo info = createInfo(cfg[i]);
				if (!this.extensions.contains(info)) {
					extensions.add(info);
				}
			}
		}
	}

	/**
	 * @since 4.0
	 */
	protected boolean isValidElement(IConfigurationElement confElement) {
		return true;
	}

	public ElementInfo[] getElementInfos() {
		initialize();
		return (ElementInfo[]) extensions.toArray(new ElementInfo[extensions
				.size()]);
	}

	/**
	 * Returns list of the {@link ElementInfo} objects.
	 * 
	 * @return
	 */
	protected List getElementInfoList() {
		initialize();
		return extensions;
	}

	protected ElementInfo createInfo(IConfigurationElement config) {
		return new ElementInfo(config);
	}
}
