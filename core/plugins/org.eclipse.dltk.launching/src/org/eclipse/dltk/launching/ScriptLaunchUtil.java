/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.launching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.internal.launching.EnvironmentResolver;
import org.eclipse.dltk.launching.ScriptRuntime.DefaultInterpreterEntry;
import org.osgi.framework.Bundle;

public class ScriptLaunchUtil {
	// Creating of InterpreterConfig
	public static InterpreterConfig createInterpreterConfig(
			IExecutionEnvironment exeEnv, IFileHandle scriptFile,
			IFileHandle workingDirectory) {
		return createInterpreterConfig(exeEnv, scriptFile, workingDirectory,
				null);
	}

	public static InterpreterConfig createInterpreterConfig(
			IExecutionEnvironment exeEnv, IFileHandle scriptFile,
			IFileHandle workingDirectory, EnvironmentVariable[] env) {
		IPath workingDirectoryPath = null;
		if (workingDirectory != null) {
			workingDirectoryPath = new Path(workingDirectory.toOSString());
		}
		InterpreterConfig config = new InterpreterConfig(
				scriptFile.getEnvironment(), new Path(scriptFile.toOSString()),
				workingDirectoryPath);

		Map<String, String> envVars = exeEnv.getEnvironmentVariables(false);
		if (envVars != null) {
			config.addEnvVars(envVars);
			EnvironmentVariable[] resVars = EnvironmentResolver.resolve(envVars,
					env);
			if (resVars != null) {
				for (int i = 0; i < resVars.length; i++) {
					config.addEnvVar(resVars[i].getName(),
							resVars[i].getValue());
				}
			}
		}

		return config;
	}

	// Useful run methods
	public static Process runScriptWithInterpreter(IExecutionEnvironment exeEnv,
			String interpreter, InterpreterConfig config) throws CoreException {
		String[] cmdLine = config.renderCommandLine(exeEnv.getEnvironment(),
				interpreter);

		String[] environmentAsStrings = config.getEnvironmentAsStrings();
		IPath workingDirectoryPath = config.getWorkingDirectoryPath();
		if (DLTKLaunchingPlugin.TRACE_EXECUTION) {
			traceExecution("runScript with interpreter", cmdLine, //$NON-NLS-1$
					environmentAsStrings);
		}
		return exeEnv.exec(cmdLine, workingDirectoryPath, environmentAsStrings);
	}

	/**
	 * @since 2.0
	 */
	public static void traceExecution(String processLabel,
			String[] cmdLineLabel, String[] environment) {
		StringBuffer sb = new StringBuffer();
		sb.append("-----------------------------------------------\n"); //$NON-NLS-1$
		sb.append("Running ").append(processLabel).append('\n'); //$NON-NLS-1$
		// sb.append("Command line: ").append(cmdLineLabel).append('\n');
		sb.append("Command line: "); //$NON-NLS-1$
		for (int i = 0; i < cmdLineLabel.length; i++) {
			sb.append(" " + cmdLineLabel[i]); //$NON-NLS-1$
		}
		sb.append("\n"); //$NON-NLS-1$
		sb.append("Environment:\n"); //$NON-NLS-1$
		for (int i = 0; i < environment.length; i++) {
			sb.append('\t').append(environment[i]).append('\n');
		}
		sb.append("-----------------------------------------------\n"); //$NON-NLS-1$
		System.out.println(sb);
	}

	public static Process runScriptWithInterpreter(IExecutionEnvironment exeEnv,
			String interpreter, IFileHandle scriptFile,
			IFileHandle workingDirectory, String[] interpreterArgs,
			String[] scriptArgs, EnvironmentVariable[] environment)
			throws CoreException {
		InterpreterConfig config = createInterpreterConfig(exeEnv, scriptFile,
				workingDirectory, environment);

		if (scriptArgs != null) {
			config.addScriptArgs(scriptArgs);
		}

		if (interpreterArgs != null) {
			config.addInterpreterArgs(interpreterArgs);
		}

		return runScriptWithInterpreter(exeEnv, interpreter, config);
	}

	public static IInterpreterInstall getDefaultInterpreterInstall(
			String natureId, String environment) {
		return ScriptRuntime.getDefaultInterpreterInstall(
				new DefaultInterpreterEntry(natureId, environment));
	}

	public static IInterpreterInstall getProjectInterpreterInstall(
			IScriptProject project) throws CoreException {
		return ScriptRuntime.getInterpreterInstall(project);
	}

	// General run method
	public static ILaunch runScript(IInterpreterInstall install,
			InterpreterConfig config, IProgressMonitor monitor)
			throws CoreException {

		if (install == null) {
			return null;
		}

		ILaunch launch = new Launch(null, ILaunchManager.RUN_MODE, null);

		// will use 'instance scoped' interpreter here
		IInterpreterRunner runner = install
				.getInterpreterRunner(launch.getLaunchMode());

		runner.run(config, launch, monitor);

		return launch;
	}

	// Run by interpreter form project
	public static ILaunch runScript(IScriptProject project,
			InterpreterConfig config, IProgressMonitor monitor)
			throws CoreException {
		return runScript(getProjectInterpreterInstall(project), config,
				monitor);
	}

	// Run by default interpreter
	public static ILaunch runScript(String natureId, String environment,
			InterpreterConfig config, IProgressMonitor monitor)
			throws CoreException {
		IInterpreterInstall install = getDefaultInterpreterInstall(natureId,
				environment);
		EnvironmentVariable[] variables = EnvironmentResolver.resolve(
				config.getEnvVars(), install.getEnvironmentVariables());
		if (variables != null) {
			for (int i = 0; i < variables.length; i++) {
				config.addEnvVar(variables[i].getName(),
						variables[i].getValue());
			}
		}
		return runScript(install, config, monitor);
	}

	// Script file
	public static ILaunch runScript(String natureId, IFileHandle scriptFile,
			IFileHandle workingDirectory, String[] interpreterArgs,
			String[] scriptArgs, IProgressMonitor monitor)
			throws CoreException {
		IEnvironment environment = scriptFile.getEnvironment();
		IExecutionEnvironment execEnvironment = environment
				.getAdapter(IExecutionEnvironment.class);
		InterpreterConfig config = createInterpreterConfig(execEnvironment,
				scriptFile, workingDirectory);

		if (interpreterArgs != null) {
			config.addInterpreterArgs(interpreterArgs);
		}

		if (scriptArgs != null) {
			config.addScriptArgs(scriptArgs);
		}

		return runScript(natureId, environment.getId(), config, monitor);
	}

	/**
	 * @since 2.0
	 */
	public static ILaunch runScript(IInterpreterInstall install,
			IFileHandle scriptFile, IFileHandle workingDirectory,
			String[] interpreterArgs, String[] scriptArgs,
			IProgressMonitor monitor) throws CoreException {
		IEnvironment environment = scriptFile.getEnvironment();
		IExecutionEnvironment execEnvironment = environment
				.getAdapter(IExecutionEnvironment.class);
		InterpreterConfig config = createInterpreterConfig(execEnvironment,
				scriptFile, workingDirectory);

		if (interpreterArgs != null) {
			config.addInterpreterArgs(interpreterArgs);
		}

		if (scriptArgs != null) {
			config.addScriptArgs(scriptArgs);
		}
		EnvironmentVariable[] variables = EnvironmentResolver.resolve(
				config.getEnvVars(), install.getEnvironmentVariables());
		if (variables != null) {
			for (int i = 0; i < variables.length; i++) {
				config.addEnvVar(variables[i].getName(),
						variables[i].getValue());
			}
		}
		return runScript(install, config, monitor);
	}

	/**
	 * @since 2.0
	 */
	public static class ErrorStreamReaderThread extends Thread {

		final InputStream stream;

		/**
		 * @param stream
		 */
		public ErrorStreamReaderThread(InputStream stream) {
			this.stream = stream;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[256];
			try {
				while (stream.read(buffer) != -1) {
					// ignore
				}
			} catch (IOException e) {
				// ignore
			}
		}

	}

	/**
	 * @since 2.0
	 */
	public static class OutputStreamReaderThread extends Thread {

		final InputStream stream;
		final StringBuffer output;
		final IProgressMonitor monitor;

		public OutputStreamReaderThread(InputStream stream, StringBuffer output,
				IProgressMonitor monitor) {
			this.stream = stream;
			this.output = output;
			this.monitor = monitor;
		}

		@Override
		public void run() {
			BufferedReader input = null;
			try {
				input = new BufferedReader(new InputStreamReader(stream));
				String line;
				while ((line = input.readLine()) != null) {
					output.append(line);
					output.append("\n"); //$NON-NLS-1$
					monitor.worked(1);
				}
			} catch (IOException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						if (DLTKCore.DEBUG) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * Read content from specified stream.
	 * 
	 * @return Return empty string in error.
	 */
	public static String runEmbeddedScriptReadContent(
			IExecutionEnvironment exeEnv, String scriptPath, Bundle bundle,
			IFileHandle installLocations, final IProgressMonitor monitor) {
		IDeployment deployment = exeEnv.createDeployment();
		if (deployment == null) {
			// happens if RSE is not initialized yet or no connection
			// established
			return null;
		}
		try {
			IFileHandle builderFile = deployment
					.getFile(deployment.add(bundle, scriptPath));
			InterpreterConfig config = ScriptLaunchUtil.createInterpreterConfig(
					exeEnv, builderFile, builderFile.getParent());
			config.removeEnvVar("DISPLAY"); //$NON-NLS-1$
			final StringBuffer source = new StringBuffer();
			final Process process = ScriptLaunchUtil.runScriptWithInterpreter(
					exeEnv, installLocations.toOSString(), config);
			try {
				process.getOutputStream().close();
				new ErrorStreamReaderThread(process.getErrorStream()).start();
				new OutputStreamReaderThread(process.getInputStream(), source,
						monitor).start();
				final Thread waitThread = new Thread() {
					@Override
					public void run() {
						try {
							process.waitFor();
						} catch (InterruptedException e) {
							// ignore
						}
					}
				};
				try {
					waitThread.start();
					waitThread.join(10000);
				} catch (InterruptedException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			} finally {
				process.destroy();
			}
			return source.toString();
		} catch (IOException e1) {
			if (DLTKCore.DEBUG) {
				e1.printStackTrace();
			}
			return null;
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return null;
		} finally {
			deployment.dispose();
		}
	}
}
