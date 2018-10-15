/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.viewsupport;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/**
 */
public class ImageImageDescriptor extends ImageDescriptor {
	private final Image fImage;

	public ImageImageDescriptor(Image image) {
		super();
		fImage = image;
	}

	@Override
	public ImageData getImageData() {
		return fImage.getImageData();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj != null) && getClass().equals(obj.getClass())
				&& fImage.equals(((ImageImageDescriptor) obj).fImage);
	}

	@Override
	public int hashCode() {
		return fImage.hashCode();
	}
}
