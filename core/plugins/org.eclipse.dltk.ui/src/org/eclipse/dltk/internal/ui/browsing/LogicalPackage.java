/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.browsing;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;

/**
 * Contains a list of package fragments with the same name but residing in
 * different source folders of a unique script project.
 */
public class LogicalPackage extends PlatformObject {

	private Set<IScriptFolder> fPackages;
	private String fName;
	private IScriptProject fScriptProject;

	public LogicalPackage(IScriptFolder fragment) {
		fPackages = new HashSet<>();
		fScriptProject = fragment.getScriptProject();
		add(fragment);
		fName = fragment.getElementName();
	}

	public IScriptProject getScriptProject() {
		return fScriptProject;
	}

	public IScriptFolder[] getFragments() {
		return fPackages.toArray(new IScriptFolder[fPackages.size()]);
	}

	public void add(IScriptFolder fragment) {
		Assert.isTrue(fragment != null
				&& fScriptProject.equals(fragment.getScriptProject()));
		fPackages.add(fragment);
	}

	public void remove(IScriptFolder fragment) {
		fPackages.remove(fragment);
	}

	public boolean contains(IScriptFolder fragment) {
		return fPackages.contains(fragment);
	}

	public String getElementName() {
		return fName;
	}

	public int size() {
		return fPackages.size();
	}

	/**
	 * Returns true if the given fragment has the same name and resides inside
	 * the same project as the other fragments in the LogicalPackage.
	 *
	 * @param fragment
	 * @return boolean
	 */
	public boolean belongs(IScriptFolder fragment) {

		if (fragment == null)
			return false;

		if (fScriptProject.equals(fragment.getScriptProject())) {
			return fName.equals(fragment.getElementName());
		}

		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LogicalPackage))
			return false;

		LogicalPackage lp = (LogicalPackage) o;
		if (!fScriptProject.equals(lp.getScriptProject()))
			return false;

		IScriptFolder[] fragments = lp.getFragments();

		if (fragments.length != getFragments().length)
			return false;

		// this works because a LogicalPackage cannot contain the same
		// IScriptFolder twice
		for (int i = 0; i < fragments.length; i++) {
			IScriptFolder fragment = fragments[i];
			if (!fPackages.contains(fragment))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		IScriptFolder[] fragments = getFragments();
		return fScriptProject.hashCode()
				+ getHash(fragments, fragments.length - 1);
	}

	private int getHash(IScriptFolder[] fragments, int index) {
		if (index <= 0) {
			return fragments[0].hashCode() * 17;
		}
		return fragments[index].hashCode() * 17 + getHash(fragments, index - 1);
	}

}
