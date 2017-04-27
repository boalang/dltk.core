package org.eclipse.dltk.core.tests.launching;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.osgi.util.NLS;

public class PathFilesContainer {
	private static final String PATH = "path";
	private IEnvironment environment;

	public PathFilesContainer(IEnvironment environment) {
		this.environment = environment;
	}

	public void accept(IFileVisitor visitor) {
		accept(visitor, new NullProgressMonitor());
	}

	public void accept(IFileVisitor visitor, IProgressMonitor monitor) {
		accept(visitor, -1, monitor);
	}

	public void accept(IFileVisitor visitor, int deep,
			IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return;
		}

		// Path variable
		Map<?, ?> env = DebugPlugin.getDefault().getLaunchManager()
				.getNativeEnvironmentCasePreserved();

		String path = null;
		final Iterator<?> it = env.keySet().iterator();
		while (it.hasNext()) {
			final String name = (String) it.next();
			if (name.compareToIgnoreCase(PATH) == 0) { // $NON-NLS-1$
				path = (String) env.get(name);
			}
		}

		if (path == null) {
			return;
		}

		// Folder list
		final String separator = Platform.getOS().equals(Platform.OS_WIN32)
				? ";" //$NON-NLS-1$
				: ":"; // $NON-NLS-1$

		final List<IPath> folders = new ArrayList<>();
		String[] res = path.split(separator);
		for (int i = 0; i < res.length; i++) {
			folders.add(Path.fromOSString(res[i]));
		}

		ArrayList<IFileHandle> searchedFiles = new ArrayList<>();
		final Iterator<IPath> iter = folders.iterator();
		while (iter.hasNext()) {
			final IPath folder = iter.next();

			if (folder != null) {
				IFileHandle f = environment.getFile(folder);
				if (f.isDirectory()) {
					visitFolder(visitor, f, monitor, deep, searchedFiles);
				}
			}
		}
	}

	/**
	 * Searches the specified directory recursively for installed Interpreters,
	 * adding each detected Interpreter to the <code>found</code> list. Any
	 * directories specified in the <code>ignore</code> are not traversed.
	 *
	 * @param visitor
	 *
	 * @param ry
	 * @param found
	 * @param types
	 * @param ignore
	 * @param deep
	 *            deepness of search. -1 if infinite.
	 * @param searchedFiles
	 */
	protected void visitFolder(IFileVisitor visitor, IFileHandle directory,
			IProgressMonitor monitor, int deep,
			ArrayList<IFileHandle> searchedFiles) {
		if (deep == 0) {
			return;
		}

		if (monitor.isCanceled()) {
			return;
		}

		if (searchedFiles.contains(directory)) {
			return;
		}

		IFileHandle[] childFiles = directory.getChildren();
		if (childFiles == null) {
			return;
		}

		List<?> subDirs = new ArrayList<>();
		for (int i = 0; i < childFiles.length; i++) {
			if (monitor.isCanceled()) {
				return;
			}

			final IFileHandle file = childFiles[i];

			monitor.subTask(NLS.bind("Searching {0}", file.getCanonicalPath()));

			// Check if file is a symlink
			if (file.isDirectory()
					&& (!file.getCanonicalPath().equals(file.toOSString()))) {
				continue;
			}

			if (visitor.visit(file)) {
				while (!subDirs.isEmpty()) {
					IFileHandle subDir = (IFileHandle) subDirs.remove(0);
					visitFolder(visitor, subDir, monitor, deep - 1,
							searchedFiles);
				}
			}
		}

		searchedFiles.add(directory);
	}
}
