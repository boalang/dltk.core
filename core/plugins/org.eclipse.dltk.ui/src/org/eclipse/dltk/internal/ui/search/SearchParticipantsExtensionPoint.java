/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.search.ScriptSearchPage;

public class SearchParticipantsExtensionPoint {

	private Set<SearchParticipantDescriptor> fActiveParticipants = null;
	private static SearchParticipantsExtensionPoint fgInstance;

	public boolean hasAnyParticipants() {
		return Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ScriptSearchPage.PARTICIPANT_EXTENSION_POINT).length > 0;
	}

	private synchronized Set<SearchParticipantDescriptor> getAllParticipants() {
		if (fActiveParticipants != null)
			return fActiveParticipants;
		IConfigurationElement[] allParticipants = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ScriptSearchPage.PARTICIPANT_EXTENSION_POINT);
		fActiveParticipants = new HashSet<>(allParticipants.length);
		for (int i = 0; i < allParticipants.length; i++) {
			SearchParticipantDescriptor descriptor = new SearchParticipantDescriptor(allParticipants[i]);
			IStatus status = descriptor.checkSyntax();
			if (status.isOK()) {
				fActiveParticipants.add(descriptor);
			} else {
				DLTKUIPlugin.log(status);
			}
		}
		return fActiveParticipants;
	}

	private void collectParticipants(IDLTKLanguageToolkit language, Set<SearchParticipantRecord> participants,
			IProject[] projects) {
		Iterator<SearchParticipantDescriptor> activeParticipants = getAllParticipants().iterator();
		Set<String> seenParticipants = new HashSet<>();
		while (activeParticipants.hasNext()) {
			SearchParticipantDescriptor participant = activeParticipants.next();
			if (participant.isEnabled() && language.getNatureId().equals(participant.getLanguage())) {
				String id = participant.getID();
				for (int i = 0; i < projects.length; i++) {
					if (seenParticipants.contains(id))
						continue;
					try {
						if (projects[i].hasNature(participant.getNature())) {
							participants.add(new SearchParticipantRecord(participant, participant.create()));
							seenParticipants.add(id);
						}
					} catch (CoreException e) {
						DLTKUIPlugin.log(e.getStatus());
						participant.disable();
					}
				}
			}
		}
	}

	public SearchParticipantRecord[] getSearchParticipants(IDLTKLanguageToolkit language, IProject[] concernedProjects)
			throws CoreException {
		Set<SearchParticipantRecord> participantSet = new HashSet<>();
		collectParticipants(language, participantSet, concernedProjects);
		return participantSet.toArray(new SearchParticipantRecord[participantSet.size()]);
	}

	public static SearchParticipantsExtensionPoint getInstance() {
		if (fgInstance == null) {
			synchronized (SearchParticipantsExtensionPoint.class) {
				if (fgInstance == null) {
					fgInstance = new SearchParticipantsExtensionPoint();
				}
			}
		}
		return fgInstance;
	}

	public static void debugSetInstance(SearchParticipantsExtensionPoint instance) {
		fgInstance = instance;
	}
}
