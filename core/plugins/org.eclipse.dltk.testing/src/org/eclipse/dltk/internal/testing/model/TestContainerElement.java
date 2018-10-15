/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.testing.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.testing.model.ITestElement;
import org.eclipse.dltk.testing.model.ITestElementContainer;

public abstract class TestContainerElement extends TestElement
		implements ITestElementContainer {

	private List<TestElement> fChildren;
	private Status fChildrenStatus;

	public TestContainerElement(TestContainerElement parent, String id,
			String testName) {
		this(parent, id, testName, 8);
	}

	public TestContainerElement(TestContainerElement parent, String id,
			String testName, int childrenCount) {
		super(parent, id, testName);
		fChildren = new ArrayList<>(childrenCount);
	}

	@Override
	public Result getTestResult(boolean includeChildren) {
		if (includeChildren) {
			return getStatus().convertToResult();
		} else {
			return super.getStatus().convertToResult();
		}
	}

	public String getSuiteTypeName() {
		return getTestName();
	}

	@Override
	public ITestElement[] getChildren() {
		return fChildren.toArray(new ITestElement[fChildren.size()]);
	}

	public void addChild(TestElement child) {
		fChildren.add(child);
	}

	@Override
	public Status getStatus() {
		Status suiteStatus = getSuiteStatus();
		if (fChildrenStatus != null) {
			// must combine children and suite status here, since failures can
			// occur e.g. in @AfterClass
			return Status.combineStatus(fChildrenStatus, suiteStatus);
		} else {
			return suiteStatus;
		}
	}

	private Status getCumulatedStatus() {
		TestElement[] children = fChildren
				.toArray(new TestElement[fChildren.size()]);
		// copy list to avoid concurreny problems
		if (children.length == 0)
			return getSuiteStatus();

		Status cumulated = children[0].getStatus();

		for (int i = 1; i < children.length; i++) {
			Status childStatus = children[i].getStatus();
			cumulated = Status.combineStatus(cumulated, childStatus);
		}
		// not necessary, see special code in Status.combineProgress()
		// if (suiteStatus.isErrorOrFailure() && cumulated.isNotRun())
		// return suiteStatus; //progress is Done if error in Suite and no
		// children run
		return cumulated;
	}

	public Status getSuiteStatus() {
		return super.getStatus();
	}

	public void childChangedStatus(TestElement child, Status childStatus) {
		int childCount = fChildren.size();
		if (child == fChildren.get(0) && childStatus.isRunning()) {
			// is first child, and is running -> copy status
			internalSetChildrenStatus(childStatus);
			return;
		}
		TestElement lastChild = fChildren.get(childCount - 1);
		if (child == lastChild) {
			if (childStatus.isDone()) {
				// all children done, collect cumulative status
				internalSetChildrenStatus(getCumulatedStatus());
				return;
			}
			// go on (child could e.g. be a TestSuiteElement with
			// RUNNING_FAILURE)

		} else if (!lastChild.getStatus().isNotRun()) {
			// child is not last, but last child has been run -> child has been
			// rerun or is rerunning
			internalSetChildrenStatus(getCumulatedStatus());
			return;
		}

		// finally, set RUNNING_FAILURE/ERROR if child has failed but suite has
		// not failed:
		if (childStatus.isFailure()) {
			if (fChildrenStatus == null
					|| !fChildrenStatus.isErrorOrFailure()) {
				internalSetChildrenStatus(Status.RUNNING_FAILURE);
				return;
			}
		} else if (childStatus.isError()) {
			if (fChildrenStatus == null || !fChildrenStatus.isError()) {
				internalSetChildrenStatus(Status.RUNNING_ERROR);
				return;
			}
		}
	}

	private void internalSetChildrenStatus(Status status) {
		if (fChildrenStatus == status)
			return;

		if (status == Status.RUNNING) {
			if (fTime >= 0.0d) {
				// re-running child: ignore change
			} else {
				fTime = -System.currentTimeMillis() / 1000d;
			}
		} else if (status.convertToProgressState() == ProgressState.COMPLETED) {
			if (fTime < 0) { // assert ! Double.isNaN(fTime)
				double endTime = System.currentTimeMillis() / 1000d;
				fTime = endTime + fTime;
			}
		}

		fChildrenStatus = status;
		TestContainerElement parent = getParent();
		if (parent != null)
			parent.childChangedStatus(this, getStatus());
	}

	@Override
	public String toString() {
		String className = getClass().getName();
		final int index = className.lastIndexOf('.');
		if (index > 0) {
			className = className.substring(index + 1);
		}
		return className + ": " + getSuiteTypeName() + " : " + super.toString() //$NON-NLS-1$//$NON-NLS-2$
				+ " (" + fChildren.size() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
