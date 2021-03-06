/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.wizards.buildpath;

import java.util.Arrays;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.internal.ui.util.SelectionUtil;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
  */
public class BuildpathContainerSelectionPage extends WizardPage {

	private static final String DIALOGSTORE_SECTION = "BuildpathContainerSelectionPage"; //$NON-NLS-1$
	private static final String DIALOGSTORE_CONTAINER_IDX = "index"; //$NON-NLS-1$

	private static class BuildpathContainerLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			return ((BuildpathContainerDescriptor) element).getName();
		}
	}

	private static class BuildpathContainerSorter extends ViewerComparator {
	}

	private ListViewer fListViewer;
	private BuildpathContainerDescriptor[] fContainers;
	private IDialogSettings fDialogSettings;

	/**
	 * Constructor for BuildpathContainerWizardPage.
	 *
	 * @param containerPages
	 */
	protected BuildpathContainerSelectionPage(
			BuildpathContainerDescriptor[] containerPages) {
		super("BuildpathContainerWizardPage"); //$NON-NLS-1$
		setTitle(NewWizardMessages.BuildpathContainerSelectionPage_title);
		setDescription(
				NewWizardMessages.BuildpathContainerSelectionPage_description);
		setImageDescriptor(DLTKPluginImages.DESC_WIZBAN_ADD_LIBRARY);

		fContainers = containerPages;

		IDialogSettings settings = DLTKUIPlugin.getDefault()
				.getDialogSettings();
		fDialogSettings = settings.getSection(DIALOGSTORE_SECTION);
		if (fDialogSettings == null) {
			fDialogSettings = settings.addNewSection(DIALOGSTORE_SECTION);
			fDialogSettings.put(DIALOGSTORE_CONTAINER_IDX, 0);
		}
		validatePage();
	}

	@Override
	public void createControl(Composite parent) {
		fListViewer = new ListViewer(parent, SWT.SINGLE | SWT.BORDER);
		fListViewer.setLabelProvider(new BuildpathContainerLabelProvider());
		fListViewer.setContentProvider(new ArrayContentProvider());
		fListViewer.setComparator(new BuildpathContainerSorter());
		fListViewer.setInput(Arrays.asList(fContainers));
		fListViewer.addSelectionChangedListener(event -> validatePage());
		fListViewer.addDoubleClickListener(event -> doDoubleClick());

		int selectionIndex = fDialogSettings.getInt(DIALOGSTORE_CONTAINER_IDX);
		if (selectionIndex >= fContainers.length) {
			selectionIndex = 0;
		}
		fListViewer.getList().select(selectionIndex);
		validatePage();
		setControl(fListViewer.getList());
		Dialog.applyDialogFont(fListViewer.getList());

		if (DLTKCore.DEBUG) {
			System.err.println(
					"BuildpathContainerSelectionPage: add help support here"); //$NON-NLS-1$
		}
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
		// IDLTKHelpContextIds.BP_SELECT_CLASSPATH_CONTAINER);
	}

	/**
	 * Method validatePage.
	 */
	private void validatePage() {
		setPageComplete(getSelected() != null);
	}

	public BuildpathContainerDescriptor getSelected() {
		if (fListViewer != null) {
			ISelection selection = fListViewer.getSelection();
			return (BuildpathContainerDescriptor) SelectionUtil
					.getSingleElement(selection);
		}
		return null;
	}

	public BuildpathContainerDescriptor[] getContainers() {
		return fContainers;
	}

	protected void doDoubleClick() {
		if (canFlipToNextPage()) {
			getContainer().showPage(getNextPage());
		}
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete(); // avoid the getNextPage call to prevent
									// potential plugin load
	}

	@Override
	public void setVisible(boolean visible) {
		if (!visible && fListViewer != null) {
			fDialogSettings.put(DIALOGSTORE_CONTAINER_IDX,
					fListViewer.getList().getSelectionIndex());
		}
		super.setVisible(visible);
	}

}
