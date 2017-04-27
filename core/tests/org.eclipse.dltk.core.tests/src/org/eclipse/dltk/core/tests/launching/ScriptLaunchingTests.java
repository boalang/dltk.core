package org.eclipse.dltk.core.tests.launching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.dltk.debug.core.ExtendedDebugEventDetails;
import org.eclipse.dltk.debug.core.model.IScriptLineBreakpoint;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.internal.debug.core.model.ScriptDebugTarget;
import org.eclipse.dltk.internal.debug.core.model.ScriptLineBreakpoint;
import org.eclipse.dltk.internal.debug.core.model.ScriptThread;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.InterpreterSearcher;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

public abstract class ScriptLaunchingTests extends AbstractModelTests {

	protected IScriptProject scriptProject;

	protected IInterpreterInstall[] interpreterInstalls;

	public ScriptLaunchingTests(String testProjectName, String name) {
		super(testProjectName, name);
	}

	// Configuration
	@Override
	public void setUpSuite() throws Exception {
		super.setUpSuite();
		scriptProject = setUpScriptProject(getProjectName());

		final IProject project = scriptProject.getProject();
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { getNatureId() });
		project.setDescription(description, null);

		if (!hasPredefinedInterpreters()) {
			interpreterInstalls = searchInstalls(getNatureId());
		} else {
			interpreterInstalls = getPredefinedInterpreterInstalls();
		}
	}

	/**
	 * Should return predefined interpreters. Used with
	 * hasPredefinedInterpreters method as true.
	 *
	 * @return Not null array of interpreter installs.
	 */
	protected IInterpreterInstall[] getPredefinedInterpreterInstalls() {
		return new IInterpreterInstall[0];
	}

	@Override
	public void tearDownSuite() throws Exception {
		deleteProject(getProjectName());
		super.tearDownSuite();
	}

	// Helper methods
	protected ILaunchConfiguration createTestLaunchConfiguration(
			final String natureId, final String projectName,
			final String script, final String arguments) {
		return new ILaunchConfiguration() {
			@Override
			public boolean contentsEqual(ILaunchConfiguration configuration) {
				return false;
			}

			@Override
			public ILaunchConfigurationWorkingCopy copy(String name)
					throws CoreException {
				return null;
			}

			@Override
			public void delete() throws CoreException {

			}

			@Override
			public boolean exists() {
				return false;
			}

			@Override
			public boolean getAttribute(String attributeName,
					boolean defaultValue) throws CoreException {
				if (attributeName.equals(
						ScriptLaunchConfigurationConstants.ATTR_DEFAULT_BUILDPATH)) {
					return true;
				} else if (attributeName.equals(
						ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES)) {
					return true;
				}

				return defaultValue;
			}

			@Override
			public int getAttribute(String attributeName, int defaultValue)
					throws CoreException {
				if (attributeName.equals(
						ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_WAITING_TIMEOUT)) {
					return 10000;
				}
				return defaultValue;
			}

			@Override
			public List<String> getAttribute(String attributeName,
					List<String> defaultValue) throws CoreException {
				return defaultValue;
			}

			@Override
			public Set<String> getAttribute(String attributeName,
					Set<String> defaultValue) throws CoreException {
				return defaultValue;
			}

			@Override
			public Map<String, String> getAttribute(String attributeName,
					Map<String, String> defaultValue) throws CoreException {
				if (attributeName
						.equals(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES)) {
					Map<String, String> env = new HashMap<>();
					configureEnvironment(env);
					return env;
				}
				return defaultValue;
			}

			@Override
			public String getAttribute(String attributeName,
					String defaultValue) throws CoreException {

				if (attributeName.equals(
						ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME)) {
					return script;
				} else if (attributeName.equals(
						ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME)) {
					return projectName;
				} else if (attributeName.equals(
						ScriptLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY)) {
					return null;
				} else if (attributeName.equals(
						ScriptLaunchConfigurationConstants.ATTR_SCRIPT_ARGUMENTS)) {
					return arguments;
				} else if (attributeName.equals(
						ScriptLaunchConfigurationConstants.ATTR_INTERPRETER_ARGUMENTS)) {
					return "";
				} else if (attributeName.equals(
						ScriptLaunchConfigurationConstants.ATTR_SCRIPT_NATURE)) {
					return natureId;
				}
				return defaultValue;
			}

			@Override
			public Map<String, Object> getAttributes() throws CoreException {
				return null;
			}

			@Override
			public String getCategory() throws CoreException {
				return null;
			}

			@Override
			public IFile getFile() {
				return ScriptLaunchingTests.this
						.getFile(projectName + '/' + script);
			}

			@Override
			public IPath getLocation() {
				return null;
			}

			@Override
			public IResource[] getMappedResources() throws CoreException {
				return null;
			}

			@Override
			public String getMemento() throws CoreException {
				return null;
			}

			@Override
			public Set<String> getModes() throws CoreException {
				return null;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public ILaunchDelegate getPreferredDelegate(Set<String> modes)
					throws CoreException {
				return null;
			}

			@Override
			public ILaunchConfigurationType getType() throws CoreException {
				return null;
			}

			@Override
			public ILaunchConfigurationWorkingCopy getWorkingCopy()
					throws CoreException {
				return null;
			}

			@Override
			public boolean isLocal() {
				return false;
			}

			@Override
			public boolean isMigrationCandidate() throws CoreException {
				return false;
			}

			@Override
			public boolean isReadOnly() {
				return false;
			}

			@Override
			public boolean isWorkingCopy() {
				return false;
			}

			@Override
			public ILaunch launch(String mode, IProgressMonitor monitor)
					throws CoreException {
				return null;
			}

			@Override
			public ILaunch launch(String mode, IProgressMonitor monitor,
					boolean build) throws CoreException {
				return null;
			}

			@Override
			public ILaunch launch(String mode, IProgressMonitor monitor,
					boolean build, boolean register) throws CoreException {
				return null;
			}

			@Override
			public void migrate() throws CoreException {

			}

			@Override
			public boolean supportsMode(String mode) throws CoreException {
				return false;
			}

			@Override
			public <T> T getAdapter(Class<T> adapter) {
				return null;
			}

			@Override
			public boolean hasAttribute(String attributeName)
					throws CoreException {
				return false;
			}
		};
	}

	protected void configureEnvironment(Map<?, ?> env) {
	}

	public IInterpreterInstall[] searchInstalls(String natureId) {
		final List<IInterpreterInstall> installs = new ArrayList<>();
		final InterpreterSearcher searcher = new InterpreterSearcher();

		searcher.search(EnvironmentManager.getLocalEnvironment(), natureId,
				null, 1, null);

		if (searcher.hasResults()) {
			IFileHandle[] files = searcher.getFoundFiles();
			IInterpreterInstallType[] types = searcher.getFoundInstallTypes();

			for (int i = 0; i < files.length; ++i) {
				final IFileHandle file = files[i];
				final IInterpreterInstallType type = types[i];

				// // Skip useless interpreters
				// if (!isInterpreterRequired(file))
				// continue;

				String installId = getNatureId() + "_" + Integer.toString(i);
				IInterpreterInstall install = type
						.findInterpreterInstall(installId);

				if (install == null)
					install = type.createInterpreterInstall(installId);

				install.setName(file.toString());
				install.setInstallLocation(file);
				install.setLibraryLocations(null);
				install.setEnvironmentVariables(null);
				installs.add(install);
			}
		}

		return installs.toArray(new IInterpreterInstall[installs.size()]);
	}

	public void internalTestRequiredInterpreterAvailable(String name) {
		assertTrue("Interpreter " + name + " not available",
				isInterpreterAvailable(name));
	}

	private boolean isInterpreterAvailable(String interpreterName) {
		for (int i = 0; i < interpreterInstalls.length; i++) {
			IFileHandle installLocation = interpreterInstalls[i]
					.getInstallLocation();
			if (isRequiredInstall(interpreterName, installLocation)) {
				return true;
			}
		}
		return false;
	}

	private boolean isRequiredInstall(String interpreterName,
			IFileHandle installLocation) {
		String executableName = installLocation.getName();
		if (executableName.startsWith(interpreterName)) {
			return true;
		}
		return false;
	}

	public final static int SKIP_STDOUT_TEST = 1;

	protected void internalTestRun(String name) throws Exception {
		internalTestRun(name, 0);
	}

	protected void internalTestRun(String name, int flags) throws Exception {
		if (interpreterInstalls.length == 0) {
			fail("No interperters found for nature " + getNatureId());
		}

		for (int i = 0; i < interpreterInstalls.length; ++i) {
			final IInterpreterInstall install = interpreterInstalls[i];
			if (!isRequiredInstall(name, install.getInstallLocation())) {
				continue;
			}
			// System.out.println("Interpreter install location (run): "
			// + install.getInstallLocation().toString());

			// InterpretersUpdater updater = new InterpretersUpdater();
			// updater.updateInterpreterSettings(getNatureId(),
			// interpreterInstalls, install);

			final long time = System.currentTimeMillis();
			final String stdoutTest = Long.toString(time) + "_stdout";
			final String stderrTest = Long.toString(time) + "_stderr";

			final ILaunchConfiguration configuration = createLaunchConfiguration(
					stdoutTest + " " + stderrTest);

			final ILaunch launch = new Launch(configuration,
					ILaunchManager.RUN_MODE, null);

			startLaunch(launch, install);

			IProcess[] processes = launch.getProcesses();
			assertEquals(1, processes.length);

			final IProcess process = processes[0];
			final IStreamsProxy proxy = process.getStreamsProxy();
			assertNotNull(proxy);

			final IStreamMonitor outputMonitor = proxy.getOutputStreamMonitor();
			assertNotNull(outputMonitor);

			final IStreamMonitor errorMonitor = proxy.getErrorStreamMonitor();
			assertNotNull(errorMonitor);

			while (!process.isTerminated()) {
				Thread.sleep(20);
			}

			assertTrue(process.isTerminated());

			final int exitValue = process.getExitValue();
			assertEquals(0, exitValue);

			if ((flags & SKIP_STDOUT_TEST) == 0) {
				final String output = outputMonitor.getContents();
				assertEquals(stdoutTest, output);
			}

			final String error = errorMonitor.getContents();
			assertEquals(stderrTest, error);
			return;
		}
		assertTrue("Requied interpreter are't found" + name, false);
	}

	public static class DebugEventStats implements IDebugEventSetListener {
		private int suspendCount;
		private int resumeCount;
		private int beforeSuspendCount;
		private int beforeResumeCount;
		private int beforeCodeLoaded;
		private int beforeVmStarted;

		public DebugEventStats() {
			this.suspendCount = 0;
			this.resumeCount = 0;
		}

		@Override
		public void handleDebugEvents(DebugEvent[] events) {
			for (int i = 0; i < events.length; ++i) {
				DebugEvent event = events[i];

				final int kind = event.getKind();
				switch (kind) {
				case DebugEvent.RESUME:
					resumeCount += 1;
					break;
				case DebugEvent.SUSPEND:
					suspendCount += 1;
					try {
						final Object source = event.getSource();
						if (source instanceof IScriptStackFrame) {
							((IScriptStackFrame) source).resume();
						} else if (source instanceof IScriptThread) {
							((IScriptThread) source).resume();
						}
					} catch (DebugException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				case DebugEvent.CREATE:
					break;
				case DebugEvent.TERMINATE:
					break;
				case DebugEvent.CHANGE:
					break;
				case DebugEvent.MODEL_SPECIFIC:
					handleExtendedEvent(event);
					break;
				}
			}
		}

		private void handleExtendedEvent(DebugEvent event) {
			int extendedType = event.getDetail();
			switch (extendedType) {
			case ExtendedDebugEventDetails.BEFORE_VM_STARTED:
				handleBeforeVmStarted((InterpreterConfig) event.getSource());
				break;
			case ExtendedDebugEventDetails.BEFORE_CODE_LOADED:
				handleBeforeCodeLoaded((ScriptDebugTarget) event.getSource());
				break;
			case ExtendedDebugEventDetails.BEFORE_RESUME:
				handleBeforeResume((ScriptThread) event.getSource());
				break;
			case ExtendedDebugEventDetails.BEFORE_SUSPEND:
				handleBeforeSuspend((ScriptThread) event.getSource());
				break;
			}

		}

		private void handleBeforeSuspend(ScriptThread source) {
			beforeSuspendCount++;
		}

		private void handleBeforeResume(ScriptThread source) {
			beforeResumeCount++;
		}

		private void handleBeforeCodeLoaded(ScriptDebugTarget source) {
			beforeCodeLoaded++;
		}

		private void handleBeforeVmStarted(InterpreterConfig source) {
			beforeVmStarted++;
		}

		public void reset() {
			suspendCount = 0;
			resumeCount = 0;
			beforeSuspendCount = 0;
			beforeResumeCount = 0;
			beforeCodeLoaded = 0;
			beforeVmStarted = 0;
		}

		public int getSuspendCount() {
			return suspendCount;
		}

		public int getResumeCount() {
			return resumeCount;
		}

		public int getBeforeSuspendCount() {
			return beforeSuspendCount;
		}

		public int getBeforeResumeCount() {
			return beforeResumeCount;
		}

		public int getBeforeCodeLoaded() {
			return beforeCodeLoaded;
		}

		public int getBeforeVmStarted() {
			return beforeVmStarted;
		}
	}

	public DebugEventStats internalTestDebug(String name) throws Exception {
		if (interpreterInstalls.length == 0) {
			fail("No interperters found for nature " + getNatureId());
		}

		// Debug
		for (int i = 0; i < interpreterInstalls.length; ++i) {
			final IInterpreterInstall install = interpreterInstalls[i];
			if (!isRequiredInstall(name, install.getInstallLocation())) {
				continue;
			}

			DebugEventStats stats = new DebugEventStats();

			DebugPlugin.getDefault().addDebugEventListener(stats);
			// System.out.println("Interperter install location (debug): "
			// + install.getInstallLocation());

			// final InterpretersUpdater updater = new InterpretersUpdater();
			// updater.updateInterpreterSettings(getNatureId(),
			// interpreterInstalls, install);

			stats.reset();

			final ILaunch launch = new Launch(createLaunchConfiguration(""),
					ILaunchManager.DEBUG_MODE, null);

			// Setting breakpoint
			IFile file = launch.getLaunchConfiguration().getFile();
			IScriptLineBreakpoint b = new ScriptLineBreakpoint(
					getDebugModelId(), file, file.getLocation(), 1, -1, -1,
					true);

			try {
				startLaunch(launch, install);

				IProcess[] processes = launch.getProcesses();
				assertEquals(1, processes.length);

				final IProcess process = processes[0];
				while (!process.isTerminated()) {
					Thread.sleep(200);
				}

				assertTrue(process.isTerminated());
				final int exitValue = process.getExitValue();
				assertEquals(0, exitValue);
			} finally {
				b.delete();

			}
			DebugPlugin.getDefault().removeDebugEventListener(stats);
			return stats;
		}

		fail("Requied interpreter \"" + name + "\" is not found");
		return null;
	}

	protected abstract String getProjectName();

	protected abstract String getNatureId();

	protected abstract String getDebugModelId();

	protected abstract void startLaunch(ILaunch launch,
			final IInterpreterInstall install) throws CoreException;

	protected abstract ILaunchConfiguration createLaunchConfiguration(
			String arguments);

	protected boolean hasPredefinedInterpreters() {
		return false;
	}

	protected IInterpreterInstall createInstall(String path, String id,
			IInterpreterInstallType type) {
		IFileHandle file = EnvironmentManager.getLocalEnvironment()
				.getFile(new Path(path));
		if (!file.exists()) {
			return null;
		}
		IInterpreterInstall install = type.findInterpreterInstall(id);

		if (install == null)
			install = type.createInterpreterInstall(id);

		install.setName("");
		install.setInstallLocation(file);
		install.setLibraryLocations(null);
		install.setEnvironmentVariables(null);
		return install;
	}

	protected void createAddInstall(List<IInterpreterInstall> installs,
			String path, String id, IInterpreterInstallType type) {
		IInterpreterInstall install = createInstall(path, id, type);
		if (install != null) {
			installs.add(install);
		}
	}
}
