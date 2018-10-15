/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core.builder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.compiler.util.SimpleLookupTable;
import org.eclipse.dltk.core.builder.IBuildState;
import org.eclipse.dltk.utils.TextUtils;

public class State {
	// NOTE: this state cannot contain types that are not defined in this
	// project

	final String scriptProjectName;

	int buildNumber;
	long lastStructuralBuildTime;
	SimpleLookupTable structuralBuildTimes;
	Set<IPath> structuralChanges;

	/**
	 * <ul>
	 * <li>0x16 boolean noCleanExternalFolders is always present
	 * <li>0x17 dependencies
	 * <li>0x18 dependencies + flags
	 * </ul>
	 **/
	public static final byte VERSION = 0x0018;

	Set<IPath> externalFolderLocations = new HashSet<>();

	boolean noCleanExternalFolders = false;

	static class DependencyInfo {
		int flags;

		public DependencyInfo() {
		}

		public DependencyInfo(DependencyInfo source) {
			this.flags = source.flags;
		}

		@Override
		public String toString() {
			final List<String> values = new ArrayList<>();
			if ((flags & IBuildState.STRUCTURAL) != 0) {
				values.add("STRUCTURAL");
			}
			if ((flags & IBuildState.CONTENT) != 0) {
				values.add("CONTENT");
			}
			if ((flags & IBuildState.EXPORTED) != 0) {
				values.add("EXPORTED");
			}
			return String.valueOf(flags) + (!values.isEmpty() ? ":" : "")
					+ TextUtils.join(values, '|');
		}
	}

	/**
	 * Full (absolute,including project) path to the set of paths, depending on
	 * it.
	 */
	private final Map<IPath, Map<IPath, DependencyInfo>> dependencies = new HashMap<>();

	private final Set<IPath> importProblems = new HashSet<>();

	static final byte SOURCE_FOLDER = 1;
	static final byte BINARY_FOLDER = 2;
	static final byte EXTERNAL_JAR = 3;
	static final byte INTERNAL_JAR = 4;

	private State(String projectName) {
		this.scriptProjectName = projectName;
	}

	public State(IProject project) {
		this.scriptProjectName = project.getName();

		this.buildNumber = 0; // indicates a full build
		this.lastStructuralBuildTime = System.currentTimeMillis();
		this.structuralBuildTimes = new SimpleLookupTable(3);
		this.noCleanExternalFolders = false;
	}

	protected State(ScriptBuilder scriptBuilder) {
		this.scriptProjectName = scriptBuilder.currentProject.getName();

		this.buildNumber = 0; // indicates a full build
		this.lastStructuralBuildTime = System.currentTimeMillis();
		this.structuralBuildTimes = new SimpleLookupTable(3);
		this.noCleanExternalFolders = false;
	}

	void copyFrom(State lastState) {
		this.buildNumber = lastState.buildNumber + 1;
		this.lastStructuralBuildTime = lastState.lastStructuralBuildTime;
		this.structuralBuildTimes = lastState.structuralBuildTimes;
		this.structuralChanges = null;

		this.externalFolderLocations.clear();
		this.externalFolderLocations.addAll(lastState.externalFolderLocations);
		this.noCleanExternalFolders = false;
		this.dependencies.clear();
		this.dependencies.putAll(lastState.dependencies);
		this.importProblems.clear();
		this.importProblems.addAll(lastState.importProblems);
	}

	public Set<IPath> getExternalFolders() {
		return this.externalFolderLocations;
	}

	void recordStructuralChanges(Set<IPath> changes) {
		if (changes != null && !changes.isEmpty()) {
			this.structuralChanges = new HashSet<>(changes);
		} else {
			this.structuralChanges = null;
		}
	}

	static State read(IProject project, DataInputStream in) throws IOException {
		if (ScriptBuilder.DEBUG)
			System.out.println("About to read state " + project.getName()); //$NON-NLS-1$
		if (VERSION != in.readByte()) {
			if (ScriptBuilder.DEBUG)
				System.out.println(
						"Found non-compatible state version... answered null for " //$NON-NLS-1$
								+ project.getName());
			return null;
		}

		State newState = new State(in.readUTF());
		if (!project.getName().equals(newState.scriptProjectName)) {
			if (ScriptBuilder.DEBUG)
				System.out.println(
						"Project's name does not match... answered null"); //$NON-NLS-1$
			return null;
		}
		newState.buildNumber = in.readInt();
		newState.lastStructuralBuildTime = in.readLong();

		int length = in.readInt();
		newState.externalFolderLocations.clear();
		for (int i = 0; i < length; i++) {
			String folderName = in.readUTF();
			if (folderName.length() > 0)
				newState.externalFolderLocations
						.add(Path.fromPortableString(folderName));
		}
		newState.noCleanExternalFolders = in.readBoolean();
		final int dependencyCount = in.readInt();
		newState.dependencies.clear();
		for (int i = 0; i < dependencyCount; ++i) {
			final Map<IPath, DependencyInfo> paths = new HashMap<>();
			newState.dependencies.put(Path.fromPortableString(in.readUTF()),
					paths);
			readDependencyPaths(in, paths);
		}
		newState.importProblems.clear();
		readPaths(in, newState.importProblems);
		if (ScriptBuilder.DEBUG)
			System.out.println("Successfully read state for " //$NON-NLS-1$
					+ newState.scriptProjectName);
		return newState;
	}

	void tagAsNoopBuild() {
		this.buildNumber = -1; // tag the project since it has no source
		// folders and can be skipped
	}

	boolean wasNoopBuild() {
		return buildNumber == -1;
	}

	boolean wasStructurallyChanged(IProject prereqProject, State prereqState) {
		if (prereqState != null) {
			Object o = structuralBuildTimes.get(prereqProject.getName());
			long previous = o == null ? 0 : ((Long) o).longValue();
			if (previous == prereqState.lastStructuralBuildTime)
				return false;
		}
		return true;
	}

	void write(DataOutputStream out) throws IOException {
		/**
		 * byte VERSION<br>
		 * String project name<br>
		 * int build number<br>
		 * int last structural build number
		 */
		out.writeByte(VERSION);
		out.writeUTF(scriptProjectName);
		out.writeInt(buildNumber);
		out.writeLong(lastStructuralBuildTime);

		/*
		 * ClasspathMultiDirectory[] int id String path(s)
		 */
		out.writeInt(externalFolderLocations.size());
		for (Iterator<IPath> iterator = this.externalFolderLocations
				.iterator(); iterator.hasNext();) {
			IPath path = iterator.next();
			out.writeUTF(path.toPortableString());
		}
		out.writeBoolean(this.noCleanExternalFolders);
		out.writeInt(dependencies.size());
		for (Map.Entry<IPath, Map<IPath, DependencyInfo>> entry : dependencies
				.entrySet()) {
			out.writeUTF(entry.getKey().toPortableString());
			writeDependencyPaths(out, entry.getValue());
		}
		writePaths(out, importProblems);
	}

	private static void readPaths(DataInputStream in, Collection<IPath> paths)
			throws IOException {
		final int pathCount = in.readInt();
		for (int j = 0; j < pathCount; ++j) {
			paths.add(Path.fromPortableString(in.readUTF()));
		}
	}

	private void writePaths(DataOutputStream out, Collection<IPath> paths)
			throws IOException {
		out.writeInt(paths.size());
		for (IPath path : paths) {
			out.writeUTF(path.toPortableString());
		}
	}

	private static void readDependencyPaths(DataInputStream in,
			Map<IPath, DependencyInfo> paths) throws IOException {
		final int pathCount = in.readInt();
		for (int j = 0; j < pathCount; ++j) {
			final IPath path = Path.fromPortableString(in.readUTF());
			final DependencyInfo depInfo = new DependencyInfo();
			depInfo.flags = in.readInt();
			paths.put(path, depInfo);
		}
	}

	private void writeDependencyPaths(DataOutputStream out,
			Map<IPath, DependencyInfo> paths) throws IOException {
		out.writeInt(paths.size());
		for (Map.Entry<IPath, DependencyInfo> entry : paths.entrySet()) {
			out.writeUTF(entry.getKey().toPortableString());
			out.writeInt(entry.getValue().flags);
		}
	}

	/**
	 * Returns a string representation of the receiver.
	 */
	@Override
	public String toString() {
		return "State for " + scriptProjectName //$NON-NLS-1$
				+ " (#" + buildNumber //$NON-NLS-1$
				+ " @ " + new Date(lastStructuralBuildTime) //$NON-NLS-1$
				+ ")"; //$NON-NLS-1$
	}

	/**
	 *
	 */
	public void setNoCleanExternalFolders() {
		this.noCleanExternalFolders = true;
	}

	protected void recordImportProblem(IPath path) {
		Assert.isLegal(scriptProjectName.equals(path.segment(0)));
		importProblems.add(path);
	}

	protected void recordDependency(IPath path, IPath dependency, int flags) {
		Assert.isLegal(scriptProjectName.equals(path.segment(0)));
		Assert.isLegal(!path.equals(dependency));
		Map<IPath, DependencyInfo> paths = dependencies.get(dependency);
		if (paths == null) {
			paths = new HashMap<>();
			dependencies.put(dependency, paths);
		}
		DependencyInfo depInfo = paths.get(path);
		if (depInfo == null) {
			depInfo = new DependencyInfo();
			paths.put(path, depInfo);
		}
		depInfo.flags |= flags;
	}

	protected void resetDependencies() {
		dependencies.clear();
		importProblems.clear();
	}

	protected void removeDependenciesFor(Set<IPath> paths) {
		for (Iterator<Map.Entry<IPath, Map<IPath, DependencyInfo>>> i = dependencies
				.entrySet().iterator(); i.hasNext();) {
			final Map.Entry<IPath, Map<IPath, DependencyInfo>> entry = i.next();
			if (entry.getValue().keySet().removeAll(paths)
					&& entry.getValue().isEmpty()) {
				i.remove();
			}
		}
		importProblems.removeAll(paths);
	}

	/**
	 * Finds the files which should be rebuilt for the specified changes and
	 * adds them to the newDependencies and newStructuralDependencies
	 * parameters.
	 *
	 * @param paths
	 *            input parameter - paths of all the changed files
	 * @param structuralChanges
	 *            input parameter - paths of the structurally changed files
	 *            (subset of {@code paths})
	 * @param includeImportProblems
	 *            if all the files with import problems should be included
	 * @param newDependencies
	 *            output parameter - paths of the files which should be rebuilt
	 * @param newStructuralDependencies
	 *            output parameter - paths of the files which should be treated
	 *            as structurally changed
	 */
	protected void findDependenciesOf(Collection<IPath> paths,
			Set<IPath> structuralChanges, boolean includeImportProblems,
			Collection<IPath> newDependencies,
			Collection<IPath> newStructuralDependencies) {
		if (includeImportProblems && !structuralChanges.isEmpty()) {
			newDependencies.addAll(importProblems);
		}
		for (IPath path : paths) {
			final boolean structuralChange = structuralChanges.contains(path);
			final Map<IPath, DependencyInfo> deps = dependencies.get(path);
			if (deps != null) {
				for (Map.Entry<IPath, DependencyInfo> entry : deps.entrySet()) {
					if (structuralChange || ((entry.getValue().flags
							& IBuildState.CONTENT) != 0)) {
						newDependencies.add(entry.getKey());
						if ((entry.getValue().flags
								& IBuildState.EXPORTED) != 0) {
							newStructuralDependencies.add(entry.getKey());
						}
					}
				}
			}
		}
	}

	protected Collection<IPath> getAllStructuralDependencies(
			Collection<IPath> paths) {
		if (structuralChanges == null) {
			return Collections.emptyList();
		}
		final Set<IPath> result = new HashSet<>();
		result.addAll(paths);
		result.retainAll(structuralChanges);
		if (result.isEmpty()) {
			return Collections.emptyList();
		}
		final List<IPath> queue = new ArrayList<>(result);
		while (!queue.isEmpty()) {
			final List<IPath> nextQueue = new ArrayList<>();
			for (IPath path : queue) {
				final Map<IPath, DependencyInfo> deps = dependencies.get(path);
				if (deps != null) {
					for (Map.Entry<IPath, DependencyInfo> entry : deps
							.entrySet()) {
						if (!result.contains(entry.getKey())
								&& ((entry.getValue().flags
										& IBuildState.STRUCTURAL) != 0)) {
							nextQueue.add(entry.getKey());
						}
					}
				}
			}
			result.addAll(nextQueue);
			queue.clear();
			queue.addAll(nextQueue);
		}
		return result;
	}

	void dumpDependencies() {
		System.out.println("Dependencies in " + scriptProjectName + ":");
		for (Iterator<Map.Entry<IPath, Map<IPath, DependencyInfo>>> i = dependencies
				.entrySet().iterator(); i.hasNext();) {
			final Map.Entry<IPath, Map<IPath, DependencyInfo>> entry = i.next();
			System.out
					.println("  " + entry.getKey() + " -> " + entry.getValue());
		}
	}
}
