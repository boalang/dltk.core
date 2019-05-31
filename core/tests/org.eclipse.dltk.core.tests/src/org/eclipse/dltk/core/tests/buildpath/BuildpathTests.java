/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.core.tests.buildpath;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.IModelMarker;
import org.eclipse.dltk.core.IModelStatus;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.dltk.core.tests.model.ModelTestsPlugin;
import org.eclipse.dltk.core.tests.model.ModifyingResourceTests;
import org.eclipse.dltk.core.tests.util.Util;
import org.eclipse.dltk.internal.core.ArchiveProjectFragment;
import org.eclipse.dltk.internal.core.BuildpathEntry;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.utils.CorePrinter;

public class BuildpathTests extends ModifyingResourceTests {

	private static final String[] TEST_NATURE = new String[] { "org.eclipse.dltk.core.tests.testnature" };

	private static final String BUILDPATH_PRJ_0 = "Buildpath0";

	private static final String BUILDPATH_PRJ_1 = "Buildpath1";

	private static final String BUILDPATH_PRJ_2 = "Buildpath2";

	private static final String BUILDPATH_PRJ_4 = "Buildpath4";

	public class TestContainer implements IBuildpathContainer {
		IPath path;

		IBuildpathEntry[] entries;

		TestContainer(IPath path, IBuildpathEntry[] entries) {
			this.path = path;
			this.entries = entries;
		}

		@Override
		public IPath getPath() {
			return this.path;
		}

		@Override
		public IBuildpathEntry[] getBuildpathEntries() {
			return this.entries;
		}

		@Override
		public String getDescription() {
			return this.path.toString();
		}

		@Override
		public int getKind() {
			return 0;
		}

	}

	public BuildpathTests(String name) {
		super(name);
	}

	@Override
	public void setUpSuite() throws Exception {
		super.setUpSuite();
		setUpScriptProject("ModelMembers", ModelTestsPlugin.PLUGIN_NAME);

		setUpScriptProject(BUILDPATH_PRJ_0, ModelTestsPlugin.PLUGIN_NAME);
		setUpScriptProject(BUILDPATH_PRJ_1, ModelTestsPlugin.PLUGIN_NAME);
		setUpScriptProject("p1", ModelTestsPlugin.PLUGIN_NAME);
		setUpScriptProject("p2", ModelTestsPlugin.PLUGIN_NAME);
		deleteProject("P");

	}

	private void assertEncodeDecodeEntry(String projectName, String expectedEncoded, IBuildpathEntry entry) {
		IScriptProject project = getScriptProject(projectName);
		String encoded = project.encodeBuildpathEntry(entry);
		assertSourceEquals("Unexpected encoded entry", expectedEncoded, encoded);
		IBuildpathEntry decoded = project.decodeBuildpathEntry(encoded);
		assertEquals("Unexpected decoded entry", entry, decoded);
	}

	protected void assertStatus(String expected, IStatus status) {
		String actual = status.getMessage();
		assertEquals(expected, actual);
	}

	protected void assertStatus(String message, String expected, IStatus status) {
		String actual = status.getMessage();
		assertEquals(message, expected, actual);
	}

	protected File createFile(File parent, String name, String content) throws IOException {
		File file = new File(parent, name);
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(content.getBytes());
		}
		/*
		 * Need to change the time stamp to realize that the file has been modified
		 */
		file.setLastModified(System.currentTimeMillis() + 2000);
		return file;
	}

	protected File createFolder(File parent, String name) {
		File file = new File(parent, name);
		file.mkdirs();
		return file;
	}

	protected int numberOfCycleMarkers(IScriptProject scriptProject) throws CoreException {
		IMarker[] markers = scriptProject.getProject().findMarkers(IModelMarker.BUILDPATH_PROBLEM_MARKER, false,
				IResource.DEPTH_ZERO);
		int result = 0;
		for (IMarker marker : markers) {
			String cycleAttr = (String) marker.getAttribute(IModelMarker.CYCLE_DETECTED);
			if (cycleAttr != null && cycleAttr.equals("true")) { //$NON-NLS-1$
				result++;
			}
		}
		return result;
	}

	protected int numberOfBuildpathProblems(IScriptProject scriptProject) throws CoreException {
		IMarker[] markers = scriptProject.getProject().findMarkers(IModelMarker.BUILDPATH_PROBLEM_MARKER, false,
				IResource.DEPTH_ZERO);
		return markers.length;
	}

	@Override
	public void tearDownSuite() throws Exception {
		deleteProject("ModelMembers");
		deleteProject(BUILDPATH_PRJ_0);
		deleteProject(BUILDPATH_PRJ_1);
		deleteProject("p1");
		deleteProject("p2");
		super.tearDownSuite();
	}

	public void test001() throws ModelException {
		ScriptProject project = (ScriptProject) getScriptProject(BUILDPATH_PRJ_0);
		assertNotNull(project);
		IBuildpathEntry entrys[] = project.getRawBuildpath();
		assertEquals(3, entrys.length);
		assertEquals(IBuildpathEntry.BPE_SOURCE, entrys[0].getEntryKind());
		assertEquals(IBuildpathEntry.BPE_PROJECT, entrys[1].getEntryKind());
		assertEquals(IBuildpathEntry.BPE_SOURCE, entrys[2].getEntryKind());
		IModelElement[] children = project.getChildren();
		assertEquals("Sould be 2 accessible project fragments", 2, children.length);
		assertTrue(children[0] instanceof IProjectFragment);
		assertTrue(children[1] instanceof IProjectFragment);
		IProjectFragment fr0 = (IProjectFragment) children[0];
		IProjectFragment fr1 = (IProjectFragment) children[1];

		children = fr0.getChildren();
		assertEquals(1, children.length);
		assertTrue(children[0] instanceof IScriptFolder);
		IModelElement[] folderChildren = ((IScriptFolder) children[0]).getChildren();
		assertEquals(2, folderChildren.length);
		assertTrue(folderChildren[0] instanceof ISourceModule);
		assertEquals("X.txt", ((ISourceModule) (folderChildren[0])).getElementName());
		assertEquals("X2.txt", ((ISourceModule) (folderChildren[1])).getElementName());
		assertTrue(folderChildren[1] instanceof ISourceModule);

		children = fr1.getChildren();
		assertEquals(1, children.length);
		assertTrue(children[0] instanceof IScriptFolder);
		folderChildren = ((IScriptFolder) children[0]).getChildren();
		assertEquals(1, folderChildren.length);
		assertTrue(folderChildren[0] instanceof ISourceModule);
		assertEquals("X3.txt", ((ISourceModule) (folderChildren[0])).getElementName());
	}

	public void test002() throws ModelException {
		ScriptProject project = (ScriptProject) getScriptProject(BUILDPATH_PRJ_0);
		assertNotNull(project);
	}

	/**
	 * Library BuildpathEntry test
	 *
	 * @throws Exception
	 */
	public void test004() throws Exception {
		try {
			setUpScriptProject(BUILDPATH_PRJ_2, ModelTestsPlugin.PLUGIN_NAME);
			IScriptProject project = getScriptProject(BUILDPATH_PRJ_2);
			assertNotNull(project);
			IBuildpathEntry entrys[] = project.getRawBuildpath();
			assertEquals(1, entrys.length);
			assertEquals(IBuildpathEntry.BPE_LIBRARY, entrys[0].getEntryKind());
			IProjectFragment[] fragments = project.getProjectFragments();
			assertEquals(1, fragments.length);
			assertTrue(fragments[0] instanceof ArchiveProjectFragment);

			CorePrinter printer = new CorePrinter(System.out);
			((ScriptProject) project).printNode(printer);
			printer.flush();
		} finally {
			deleteProject(BUILDPATH_PRJ_2);
		}
	}

	/**
	 * External folder Library BuildpathEntry test
	 *
	 * @throws Exception
	 */
	public void test005() throws Exception {
		try {
			URL url = ModelTestsPlugin.getDefault().getBundle().getEntry("workspace/Buildpath3");
			URL res = FileLocator.resolve(url);
			IPath filePath = new Path(res.getFile());
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			IEnvironment env = LocalEnvironment.getInstance();
			newCP[originalCP.length] = DLTKCore.newExtLibraryEntry(EnvironmentPathUtils.getFullPath(env, filePath));

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("OK", status);

			proj.setRawBuildpath(newCP, null);

			IProjectFragment[] fragments = proj.getProjectFragments();
			assertEquals(2, fragments.length);
			// System.out.println("Model:");
			CorePrinter printer = new CorePrinter(System.out, true);
			((ScriptProject) proj).printNode(printer);
			printer.flush();

		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	public void test006() throws Exception {
		try {
			URL url = ModelTestsPlugin.getDefault().getBundle().getEntry("/workspace/Buildpath3");
			URL res = FileLocator.resolve(url);

			IPath localPath = new Path("Testie").append(res.getFile().substring(1));
			IPath contPath = localPath;
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newContainerEntry(contPath);

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("OK", status);

			proj.setRawBuildpath(newCP, null);

			IProjectFragment[] fragments = proj.getProjectFragments();
			assertEquals(2, fragments.length);

			// System.out.println("Model:");
			CorePrinter printer = new CorePrinter(System.out, true);
			((ScriptProject) proj).printNode(printer);
			printer.flush();
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/*
	 * Ensures that a source Buildpath entry can be encoded and decoded.
	 */
	public void testEncodeDecodeEntry01() {
		assertEncodeDecodeEntry("P", "<buildpathentry kind=\"src\" path=\"src\"/>\n",
				DLTKCore.newSourceEntry(new Path("/P/src")));
	}

	/*
	 * Ensures that a source Buildpath entry with all possible attributes can be
	 * encoded and decoded.
	 */
	public void testEncodeDecodeEntry02() {
		assertEncodeDecodeEntry("P",
				"<buildpathentry excluding=\"**/X.java\" including=\"**/Y.java\" kind=\"src\" path=\"src\">\n"
						+ "	<attributes>\n" + "		<attribute name=\"attrName\" value=\"some value\"/>\n"
						+ "	</attributes>\n" + "</buildpathentry>\n",
				DLTKCore.newSourceEntry(new Path("/P/src"), new IPath[] { new Path("**/Y.java") },
						new IPath[] { new Path("**/X.java") },
						new IBuildpathAttribute[] { DLTKCore.newBuildpathAttribute("attrName", "some value") }));
	}

	/*
	 * Ensures that a project Buildpath entry can be encoded and decoded.
	 */
	public void testEncodeDecodeEntry03() {
		assertEncodeDecodeEntry("P1", "<buildpathentry kind=\"prj\" path=\"/P2\"/>\n",
				DLTKCore.newProjectEntry(new Path("/P2")));
	}

	/**
	 * Ensures that adding an empty Buildpath container generates the correct
	 * deltas.
	 */
	public void testEmptyContainer() throws CoreException {
		try {
			IScriptProject proj = createScriptProject("P", TEST_NATURE, null);

			startDeltas();

			// create container
			DLTKCore.setBuildpathContainer(new Path("container/default"), new IScriptProject[] { proj },
					new IBuildpathContainer[] {
							new TestContainer(new Path("container/default"), new IBuildpathEntry[] {}) },
					null);

			// set P's Buildpath with this container
			IBuildpathEntry container = DLTKCore.newContainerEntry(new Path("container/default"), true);
			proj.setRawBuildpath(new IBuildpathEntry[] { container }, null);

			assertDeltas("Unexpected delta",
					"P[*]: {CONTENT | BUILDPATH CHANGED}\n" + "	ResourceDelta(/P/.buildpath)[*]");
		} finally {
			stopDeltas();
			AbstractModelTests.deleteProject("P");
		}
	}

	/*
	 * Ensures that a non existing source folder cannot be put on the Buildpath.
	 * (regression test for bug 66512 Invalid Buildpath entry not rejected)
	 */
	public void testInvalidSourceFolder() throws CoreException {
		try {
			createScriptProject("P1i", TEST_NATURE, new String[] { "" });
			IScriptProject proj = createScriptProject("P2i", TEST_NATURE, new String[] { "" },
					new String[] { "/P1i/src1/src2" });
			assertMarkers("Unexpected markers", "Illegal path for required project: '/P1i/src1/src2' in project P2i",
					proj);
		} finally {
			deleteProject("P1i");
			deleteProject("P2i");
		}
	}

	/**
	 * Should detect duplicate entries on the Buildpath
	 */
	public void testBuildpathValidation01() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = newCP[0];

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("should have detected duplicate entries on the buildpath",
					"Build path contains duplicate entry: \'src\' for project P", status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	public void testBuildpathLibraryValidation01() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("Pv0", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			IEnvironment env = LocalEnvironment.getInstance();
			IPath libPath = EnvironmentPathUtils.getFullPath(env, new Path("/opt2"));
			newCP[originalCP.length] = DLTKCore.newExtLibraryEntry(libPath);

			IModelStatus status = BuildpathEntry.validateBuildpathEntry(proj, newCP[originalCP.length], false);

			assertStatus("should detect not pressent folders",
					"Required library cannot denote external folder or archive: \'"
							+ EnvironmentPathUtils.getLocalPath(libPath).toString() + "\' for project Pv0",
					status);
		} finally {
			AbstractModelTests.deleteProject("Pv0");
		}
	}

	/**
	 * Should detect nested source folders on the Buildpath
	 */
	public void testBuildpathValidation02() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newSourceEntry(new Path("/P"));

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);
			assertStatus("should have detected nested source folders on the buildpath",
					"Cannot nest \'P/src\' inside \'P\'. To enable the nesting exclude \'src/\' from \'P\'", status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Should detect library folder nested inside source folder on the Buildpath
	 */
	public void testBuildpathValidation03() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newLibraryEntry(new Path("/P/src/lib"));

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("should have detected library folder nested inside source folder on the Buildpath",
					"Cannot nest \'P/src/lib\' inside \'P/src\'. To enable the nesting exclude \'lib/\' from \'P/src\'",
					status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	public void testClasspathValidation04() throws CoreException {

		IScriptProject[] p = null;
		try {

			p = new IScriptProject[] {
					AbstractModelTests.createScriptProject("P0var", TEST_NATURE, new String[] { "src0" }),
					AbstractModelTests.createScriptProject("P1var", TEST_NATURE, new String[] { "src1" }), };

			DLTKCore.setBuildpathVariable("var", new Path("/P1var"), null);

			IBuildpathEntry[] newBuildpath = new IBuildpathEntry[] { DLTKCore.newSourceEntry(new Path("/P0var/src0")),
					DLTKCore.newVariableEntry(new Path("var/src1")), };

			// validate Buildpath
			IModelStatus status = BuildpathEntry.validateBuildpath(p[0], newBuildpath);
			assertStatus("should not detect external source folder through a variable on the buildpath", "OK", status);

		} finally {
			AbstractModelTests.deleteProject("P0var");
			AbstractModelTests.deleteProject("P1var");
		}
	}

	public void testBuildpathValidation05() throws CoreException {

		IScriptProject[] p = null;
		try {

			p = new IScriptProject[] {
					AbstractModelTests.createScriptProject("P0v", TEST_NATURE, new String[] { "src0", "src1" }),
					AbstractModelTests.createScriptProject("P1v", TEST_NATURE, new String[] { "src1" }), };

			DLTKCore.setBuildpathContainer(new Path("container/default"), new IScriptProject[] { p[0] },
					new IBuildpathContainer[] { new TestContainer(new Path("container/default"),
							new IBuildpathEntry[] { DLTKCore.newSourceEntry(new Path("/P0v/src0")) }) },
					null);

			IBuildpathEntry[] newBuildpath = new IBuildpathEntry[] { DLTKCore.newSourceEntry(new Path("/P0v/src1")),
					DLTKCore.newContainerEntry(new Path("container/default")), };

			// validate Buildpath
			IModelStatus status = BuildpathEntry.validateBuildpath(p[0], newBuildpath);
			assertStatus("should not have detected external source folder through a container on the Buildpath", "OK",
					status);

			// validate Buildpath entry
			status = BuildpathEntry.validateBuildpathEntry(p[0], newBuildpath[1], true);
			assertStatus("should have detected external source folder through a container on the Buildpath",
					"Invalid buildpath container: \'container/default\' in project P0v", status);

		} finally {
			AbstractModelTests.deleteProject("P0v");
			AbstractModelTests.deleteProject("P1v");
		}
	}

	public void testBuildpathValidation06() throws CoreException {

		IScriptProject[] p = null;
		try {

			p = new IScriptProject[] {
					AbstractModelTests.createScriptProject("P0", TEST_NATURE, new String[] { "src" }), };

			// validate Buildpath entry
			IBuildpathEntry[] newBuildpath = new IBuildpathEntry[] { DLTKCore.newSourceEntry(new Path("/P0")),
					DLTKCore.newSourceEntry(new Path("/P0/src")), };

			IModelStatus status = BuildpathEntry.validateBuildpath(p[0], newBuildpath);
			assertStatus("should have detected nested source folder",
					"Cannot nest \'P0/src\' inside \'P0\'. To enable the nesting exclude \'src/\' from \'P0\'", status);
		} finally {
			AbstractModelTests.deleteProject("P0");
		}
	}

	/**
	 * Should allow nested source folders on the Buildpath as long as the outer
	 * folder excludes the inner one.
	 */
	public void testBuildpathValidation07() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newSourceEntry(new Path("/P"), new IPath[] { new Path("src/") });

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("should have allowed nested source folders with exclusion on the buildpath", "OK", status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Should allow a nested binary folder in a source folder on the buildpath as
	 * long as the outer folder excludes the inner one.
	 */
	public void testBuildpathValidation08() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] {});
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newSourceEntry(new Path("/P"), new IPath[] { new Path("lib/") });

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("should have allowed nested lib folders with exclusion on the buildpath", "OK", status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Should not allow nested source folders on the Buildpath if exclusion filter
	 * has no trailing slash.
	 */
	public void testBuildpathValidation15() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newSourceEntry(new Path("/P"), new IPath[] { new Path("**/src") });

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("End exclusion filter \'src\' with / to fully exclude \'P/src\'", status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Should not allow exclusion patterns if project preference disallow them
	 */
	public void testBuildpathValidation21() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] {});
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newSourceEntry(new Path("/P/src"), new IPath[] { new Path("**/src") });

			Map<String, String> options = new Hashtable<>(5);
			options.put(DLTKCore.CORE_ENABLE_BUILDPATH_EXCLUSION_PATTERNS, DLTKCore.DISABLED);
			proj.setOptions(options);
			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus(
					"Inclusion or exclusion patterns are disabled in project P, cannot selectively include or exclude from entry: \'src\'",
					status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * 33207 - Reject output folder that coincidate with distinct source folder but
	 * 36465 - Unable to create multiple source folders when not using bin for
	 * output default output scenarii is still tolerated
	 */
	public void testBuildpathValidation23() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] {});
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 2];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newSourceEntry(new Path("/P/"), new IPath[] { new Path("src/") },
					BuildpathEntry.EXCLUDE_NONE);
			newCP[originalCP.length + 1] = DLTKCore.newSourceEntry(new Path("/P/src"), new IPath[0],
					BuildpathEntry.EXCLUDE_NONE);

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("Cannot nest 'P/src' inside 'P/'. To enable the nesting exclude 'src/' from 'P/'", status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Should not allow nested source folders on the buildpath if the outer folder
	 * includes the inner one.
	 */
	public void testBuildpathValidation34() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newSourceEntry(new Path("/P"), new IPath[] { new Path("src/") },
					new IPath[0], null);

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("should not have allowed nested source folders with inclusion on the buildpath",
					"Cannot nest \'P/src\' inside \'P\'. To enable the nesting exclude \'src/\' from \'P\'", status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Should allow nested source folders on the buildpath if inclusion filter has
	 * no trailing slash.
	 */
	public void testBuildpathValidation36() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newSourceEntry(new Path("/P"), new IPath[] { new Path("**/src") },
					new Path[0], null);

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("OK", status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Should not allow inclusion patterns if project preference disallow them
	 */
	public void testBuildpathValidation37() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] {});
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newSourceEntry(new Path("/P/src"), new IPath[] { new Path("**/src") },
					new Path[0], null);

			Map<String, String> options = new Hashtable<>(5);
			options.put(DLTKCore.CORE_ENABLE_BUILDPATH_EXCLUSION_PATTERNS, DLTKCore.DISABLED);
			proj.setOptions(options);
			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus(
					"Inclusion or exclusion patterns are disabled in project P, cannot selectively include or exclude from entry: \'src\'",
					status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/*
	 * Should detect nested source folders on the buildpath and indicate the
	 * preference if disabled (regression test for bug 122615 validate buildpath
	 * propose to exlude a source folder even though exlusion patterns are disabled)
	 */
	public void testBuildpathValidation42() throws CoreException {
		try {
			IScriptProject proj = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			proj.setOption(DLTKCore.CORE_ENABLE_BUILDPATH_EXCLUSION_PATTERNS, DLTKCore.DISABLED);
			IBuildpathEntry[] originalCP = proj.getRawBuildpath();

			IBuildpathEntry[] newCP = new IBuildpathEntry[originalCP.length + 1];
			System.arraycopy(originalCP, 0, newCP, 0, originalCP.length);
			newCP[originalCP.length] = DLTKCore.newSourceEntry(new Path("/P"));

			IModelStatus status = BuildpathEntry.validateBuildpath(proj, newCP);

			assertStatus("should have detected nested source folders on the buildpath",
					"Cannot nest \'P/src\' inside \'P\'. To allow the nesting enable use of exclusion patterns in the preferences of project \'P\' and exclude \'src/\' from \'P\'",
					status);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Setting the buildpath with two entries specifying the same path should fail.
	 */
	public void testBuildpathWithDuplicateEntries() throws CoreException {
		try {
			IScriptProject project = createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] cp = project.getRawBuildpath();
			IBuildpathEntry[] newCp = new IBuildpathEntry[cp.length * 2];
			System.arraycopy(cp, 0, newCp, 0, cp.length);
			System.arraycopy(cp, 0, newCp, cp.length, cp.length);
			try {
				project.setRawBuildpath(newCp, null);
			} catch (ModelException jme) {
				return;
			}
			assertTrue("Setting the buildpath with two entries specifying the same path should fail", false);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Adding an entry to the buildpath for a project that does not exist should not
	 * break the model. The buildpath should contain the entry, but the root should
	 * not appear in the children.
	 */
	public void testBuildpathWithNonExistentProjectEntry() throws CoreException {
		try {
			IScriptProject project = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalPath = project.getRawBuildpath();
			IProjectFragment[] originalRoots = project.getProjectFragments();

			IBuildpathEntry[] newPath = new IBuildpathEntry[originalPath.length + 1];
			System.arraycopy(originalPath, 0, newPath, 0, originalPath.length);

			IBuildpathEntry newEntry = DLTKCore.newProjectEntry(new Path("/NoProject"), false);
			newPath[originalPath.length] = newEntry;

			project.setRawBuildpath(newPath, null);

			IBuildpathEntry[] getPath = project.getRawBuildpath();
			assertArrayEquals(getPath, newPath);

			IProjectFragment[] newRoots = project.getProjectFragments();
			assertArrayEquals(originalRoots, newRoots);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Adding an entry to the buildpath for a folder that does not exist should not
	 * break the model. The buildpath should contain the entry, but the root should
	 * not appear in the children.
	 */
	public void testBuildpathWithNonExistentSourceEntry() throws CoreException {
		try {
			IScriptProject project = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "src" });
			IBuildpathEntry[] originalPath = project.getRawBuildpath();
			IProjectFragment[] originalRoots = project.getProjectFragments();

			IBuildpathEntry[] newPath = new IBuildpathEntry[originalPath.length + 1];
			System.arraycopy(originalPath, 0, newPath, 0, originalPath.length);

			IBuildpathEntry newEntry = DLTKCore.newSourceEntry(new Path("/P/moreSource"));
			newPath[originalPath.length] = newEntry;

			project.setRawBuildpath(newPath, null);

			IBuildpathEntry[] getPath = project.getRawBuildpath();
			assertArrayEquals(getPath, newPath);

			IProjectFragment[] newRoots = project.getProjectFragments();
			assertArrayEquals(originalRoots, newRoots);
		} finally {
			AbstractModelTests.deleteProject("P");
		}
	}

	/**
	 * Ensure that missing projects are properly reported.
	 */
	public void testMissingProjectReport1() throws CoreException {
		final String name1 = "p1miss";
		final String name2 = "p2miss";
		try {
			IScriptProject p1 = createScriptProject(name1, TEST_NATURE, new String[] { "" });
			assertEquals(0, numberOfBuildpathProblems(p1));
			IScriptProject p2 = createScriptProject(name2, TEST_NATURE, new String[] { "" });
			assertEquals(0, numberOfBuildpathProblems(p2));
			// Add dependency
			IBuildpathEntry[] originalP2CP = p2.getRawBuildpath();
			// Add P1 as a prerequesite of P2
			int length = originalP2CP.length;
			IBuildpathEntry[] newCP = new IBuildpathEntry[length + 1];
			System.arraycopy(originalP2CP, 0, newCP, 0, length);
			newCP[length] = DLTKCore.newProjectEntry(p1.getProject().getFullPath(), false);
			p2.setRawBuildpath(newCP, null);
			waitForAutoBuild(); // wait for markers to be created
			assertEquals(0, numberOfBuildpathProblems(p1));
			assertEquals(0, numberOfBuildpathProblems(p2));
			p1.getProject().close(new NullProgressMonitor());
			waitForAutoBuild(); // wait for markers to be created
			assertEquals(1, numberOfBuildpathProblems(p2));
		} finally {
			deleteProjects(new String[] { name1, name2 });
		}
	}

	/**
	 * Ensure that missing projects are properly reported.
	 */
	public void testMissingProjectReport2() throws CoreException, IOException {
		final String name1 = "miss1";
		final String name2 = "miss2";
		try {
			IScriptProject p2 = setUpScriptProject(name2, ModelTestsPlugin.PLUGIN_NAME);
			waitForAutoBuild();
			assertEquals(1, numberOfBuildpathProblems(p2));
			IScriptProject p1 = setUpScriptProject(name1, ModelTestsPlugin.PLUGIN_NAME);
			waitForAutoBuild();
			assertEquals(0, numberOfBuildpathProblems(p1));
			assertEquals(0, numberOfBuildpathProblems(p2));
		} finally {
			deleteProjects(new String[] { name1, name2 });
		}
	}

	/**
	 * Ensure that cycle are properly reported.
	 */
	public void testCycleReport() throws CoreException {

		try {
			IScriptProject p1 = AbstractModelTests.createScriptProject("p1_", TEST_NATURE, new String[] { "" });
			IScriptProject p2 = AbstractModelTests.createScriptProject("p2_", TEST_NATURE, new String[] { "" });
			IScriptProject p3 = AbstractModelTests.createScriptProject("p3_", TEST_NATURE, new String[] { "" },
					new String[] { "/p2_" });

			// Ensure no cycle reported
			IScriptProject[] projects = { p1, p2, p3 };
			int cycleMarkerCount = 0;
			for (int i = 0; i < projects.length; i++) {
				cycleMarkerCount += this.numberOfCycleMarkers(projects[i]);
			}
			assertTrue("Should have no cycle markers", cycleMarkerCount == 0);

			// Add cycle
			IBuildpathEntry[] originalP1CP = p1.getRawBuildpath();
			IBuildpathEntry[] originalP2CP = p2.getRawBuildpath();

			// Add P1 as a prerequesite of P2
			int length = originalP2CP.length;
			IBuildpathEntry[] newCP = new IBuildpathEntry[length + 1];
			System.arraycopy(originalP2CP, 0, newCP, 0, length);
			newCP[length] = DLTKCore.newProjectEntry(p1.getProject().getFullPath(), false);
			p2.setRawBuildpath(newCP, null);

			// Add P3 as a prerequesite of P1
			length = originalP1CP.length;
			newCP = new IBuildpathEntry[length + 1];
			System.arraycopy(originalP1CP, 0, newCP, 0, length);
			newCP[length] = DLTKCore.newProjectEntry(p3.getProject().getFullPath(), false);
			p1.setRawBuildpath(newCP, null);

			waitForAutoBuild(); // wait for cycle markers to be created
			cycleMarkerCount = 0;
			for (IScriptProject project : projects) {
				cycleMarkerCount += numberOfCycleMarkers(project);
			}
			assertEquals("Unexpected number of projects involved in a buildpath cycle", 3, cycleMarkerCount);

		} finally {
			// cleanup
			deleteProjects(new String[] { "p1_", "p2_", "p3_" });
		}
	}

	public void testScriptFolderExclude() throws CoreException {
		try {
			// create
			final IScriptProject project = createScriptProject("A_", TEST_NATURE, new String[] { "" });
			project.getProject().getFolder("src").create(true, true, null);
			// find folder & test it
			final IScriptFolder folder = project.findScriptFolder(new Path("/A_/src"));
			assertNotNull(folder);
			assertTrue(folder.exists());
			// change buildpath
			final IBuildpathEntry entry1 = DLTKCore.newSourceEntry(new Path("/A_/src"));
			final IBuildpathEntry entry2 = DLTKCore.newSourceEntry(new Path("/A_"), new IPath[0],
					new IPath[] { new Path("src/") });
			project.setRawBuildpath(new IBuildpathEntry[] { entry1, entry2 }, null);
			// test folder after change
			assertFalse(folder.exists());
		} finally {
			deleteProject("A_");
		}
	}

	/**
	 * Setting the buildpath to empty should result in no entries, and a delta with
	 * removed roots.
	 */
	public void testEmptyBuildpath() throws CoreException {
		IScriptProject project = AbstractModelTests.createScriptProject("P", TEST_NATURE, new String[] { "" });
		try {
			startDeltas();
			setBuildpath(project, new IBuildpathEntry[] {});
			IBuildpathEntry[] cp = project.getRawBuildpath();
			assertTrue("buildpath should have no entries", cp.length == 0);

			// ensure the deltas are correct
			assertDeltas("Unexpected delta",
					"P[*]: {CHILDREN | CONTENT | BUILDPATH CHANGED | RESOLVED BUILDPATH CHANGED}\n"
							+ "	<project root>[*]: {REMOVED FROM BUILDPATH}\n" + "	ResourceDelta(/P/.buildpath)[*]");
		} finally {
			stopDeltas();
			AbstractModelTests.deleteProject("P");
		}
	}

	/*
	 * Ensures that a source folder that contains character that must be encoded can
	 * be written. (regression test for bug
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=70193)
	 */
	public void testEncoding() throws CoreException, IOException {
		try {
			createScriptProject("P", TEST_NATURE, new String[] { "src\u3400" });
			IFile file = getFile("/P/.buildpath");
			String encodedContents = new String(
					org.eclipse.dltk.internal.core.util.Util.getResourceContentsAsCharArray(file, "UTF-8"));
			encodedContents = Util.convertToIndependantLineDelimiter(encodedContents);
			assertEquals(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<buildpath>\n"
							+ "	<buildpathentry kind=\"src\" path=\"src\u3400\"/>\n" + "</buildpath>\n",
					encodedContents);
		} finally {
			deleteProject("P");
		}
	}

	/**
	 * Tests the cross project Buildpath setting
	 */
	public void testBuildpathCrossProject() throws CoreException {
		IScriptProject project = AbstractModelTests.createScriptProject("P1c", TEST_NATURE, new String[] { "" });
		AbstractModelTests.createScriptProject("P2c", TEST_NATURE, new String[] {});
		try {
			startDeltas();
			IProjectFragment oldRoot = getProjectFragment("P1c", "");
			IBuildpathEntry projectEntry = DLTKCore.newProjectEntry(new Path("/P2c"), false);
			IBuildpathEntry[] newBuildpath = new IBuildpathEntry[] { projectEntry };
			project.setRawBuildpath(newBuildpath, null);
			project.getProjectFragments();
			IModelElementDelta removedDelta = getDeltaFor(oldRoot, true);
			assertDeltas("Unexpected delta", "<project root>[*]: {REMOVED FROM BUILDPATH}", removedDelta);
		} finally {
			stopDeltas();
			this.deleteProjects(new String[] { "P1c", "P2c" });
		}
	}

	public void testGetProjectFragmentByResource() throws CoreException, IOException {
		try {
			setUpScriptProject(BUILDPATH_PRJ_4, ModelTestsPlugin.PLUGIN_NAME);
			String folderName = "library";
			IModelElement element = DLTKCore
					.create(getWorkspaceRoot().getFolder(new Path(BUILDPATH_PRJ_4 + "/" + folderName)));
			assertNotNull(element);
			assertEquals(IModelElement.PROJECT_FRAGMENT, element.getElementType());
			assertEquals(folderName, element.getElementName());
		} finally {
			deleteProject(BUILDPATH_PRJ_4);
		}
	}
}
