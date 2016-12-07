/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.launching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.internal.environment.LazyFileHandle;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.LaunchingMessages;
import org.eclipse.dltk.launching.LibraryLocation;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.launching.ScriptRuntime.DefaultInterpreterEntry;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This is a container for interpreter definitions such as the interpreter
 * definitions that are stored in the workbench preferences.
 * <p>
 * An instance of this class may be obtained from an XML document by calling
 * <code>parseXMLIntoContainer</code>.
 * </p>
 * <p>
 * An instance of this class may be translated into an XML document by calling
 * <code>getAsXML</code>.
 * </p>
 * <p>
 * Clients may instantiate this class; it is not intended to be subclassed.
 * </p>
 */
@SuppressWarnings("deprecation")
public class InterpreterDefinitionsContainer {
	private static final String PATH_ATTR = "path"; //$NON-NLS-1$
	private static final String INTERPRETER_NAME_ATTR = "name"; //$NON-NLS-1$
	private static final String INTERPRETER_TAG = "interpreter"; //$NON-NLS-1$
	private static final String INTERPRETER_TYPE_TAG = "interpreterType"; //$NON-NLS-1$
	private static final String ID_ATTR = "id"; //$NON-NLS-1$
	private static final String NATURE_ATTR = "nature"; //$NON-NLS-1$
	private static final String ENVIRONMENT_ATTR = "environment"; //$NON-NLS-1$
	private static final String DEFAULT_INTERPRETER_TAG = "defaultInterpreter"; //$NON-NLS-1$
	private static final String INTERPRETER_SETTINGS_TAG = "interpreterSettings"; //$NON-NLS-1$
	private static final String VARIABLE_VALUE_ATTR = "variableValue"; //$NON-NLS-1$
	private static final String VARIABLE_NAME_ATTR = "variableName"; //$NON-NLS-1$
	private static final String LIBRARY_PATH_ATTR = "libraryPath"; //$NON-NLS-1$
	private static final String IARGS_ATTR = "iargs"; //$NON-NLS-1$
	private static final String ENVIRONMENT_VARIABLES_TAG = "environmentVariables"; //$NON-NLS-1$
	private static final String ENVIRONMENT_VARIABLE_TAG = "environmentVariable"; //$NON-NLS-1$
	private static final String LIBRARY_LOCATIONS_TAG = "libraryLocations"; //$NON-NLS-1$
	private static final String LIBRARY_LOCATION_TAG = "libraryLocation"; //$NON-NLS-1$
	private static final String ENVIRONMENT_ID = "environmentId"; //$NON-NLS-1$
	private static final String EXTENSIONS_TAG = "extensions"; //$NON-NLS-1$
	/**
	 * Map of InterpreterInstallTypes to Lists of corresponding
	 * InterpreterInstalls.
	 */
	private Map<IInterpreterInstallType, List<IInterpreterInstall>> fInterTypeToInterMap;

	/**
	 * Cached list of Interpreters in this container
	 */
	private List<IInterpreterInstall> fInterpreterList;

	// /**
	// * Interpreters managed by this container whose install locations don't
	// * actually exist.
	// */
	// private List fInvalidInterpreterList;

	/**
	 * The composite identifier of the default Interpreter. This consists of the
	 * install type ID plus an ID for the Interpreter.
	 */
	// map bind default interpreter to each nature
	private Map<DefaultInterpreterEntry, String> fDefaultInterpreterInstallCompositeID;

	/**
	 * The identifier of the connector to use for the default Interpreter.
	 */
	// map bind default connector to each nature
	private Map<DefaultInterpreterEntry, String> fDefaultInterpreterInstallConnectorTypeID;

	/**
	 * Constructs an empty Interpreter container
	 */
	public InterpreterDefinitionsContainer() {
		fInterTypeToInterMap = new HashMap<>(10);
		// fInvalidInterpreterList = new ArrayList(10);
		fInterpreterList = new ArrayList<>(10);
		fDefaultInterpreterInstallCompositeID = new HashMap<>();
		fDefaultInterpreterInstallConnectorTypeID = new HashMap<>();
	}

	/**
	 * Returns list of default interpreters natures TODO: rename
	 *
	 * @return
	 */
	public DefaultInterpreterEntry[] getInterpreterNatures() {
		Set<DefaultInterpreterEntry> s = new HashSet<>(
				fDefaultInterpreterInstallCompositeID.keySet());
		for (IInterpreterInstall install : fInterpreterList) {
			s.add(new DefaultInterpreterEntry(install.getNatureId(),
					install.getEnvironmentId()));
		}
		return s.toArray(new DefaultInterpreterEntry[s.size()]);
	}

	/**
	 * Add the specified Interpreter to the Interpreter definitions managed by
	 * this container.
	 * <p>
	 * If distinguishing valid from invalid Interpreters is important, the
	 * specified Interpreter must have already had its install location set. An
	 * invalid Interpreter is one whose install location doesn't exist.
	 * </p>
	 *
	 * @param interpreter
	 *            the Interpreter to be added to this container
	 */
	public void addInterpreter(IInterpreterInstall interpreter) {
		addInterpreter(interpreter, false);
	}

	/**
	 * Add the specified Interpreter to the Interpreter definitions managed by
	 * this container.
	 * <p>
	 * If distinguishing valid from invalid Interpreters is important, the
	 * specified Interpreter must have already had its install location set. An
	 * invalid Interpreter is one whose install location doesn't exist.
	 * </p>
	 *
	 * @param interpreter
	 *            the Interpreter to be added to this container
	 * @param overwrite
	 *            overwite interpreter install anyway
	 */
	public void addInterpreter(IInterpreterInstall interpreter,
			boolean overwrite) {
		if (fInterpreterList.contains(interpreter) && overwrite) {
			fInterpreterList.remove(interpreter);
		}
		if (!fInterpreterList.contains(interpreter) || overwrite) {
			IInterpreterInstallType interpreterInstallType = interpreter
					.getInterpreterInstallType();
			List<IInterpreterInstall> interpreterList = fInterTypeToInterMap
					.get(interpreterInstallType);
			if (interpreterList == null) {
				interpreterList = new ArrayList<>(3);
				fInterTypeToInterMap.put(interpreterInstallType,
						interpreterList);
			}
			if (interpreterList.contains(interpreter)) {
				interpreterList.remove(interpreter);
			}
			interpreterList.add(interpreter);
			// IFileHandle installLocation = Interpreter.getInstallLocation();
			// if (installLocation == null
			// || !InterpreterInstallType.validateInstallLocation(
			// installLocation).isOK()) {
			// fInvalidInterpreterList.add(Interpreter);
			// }
			fInterpreterList.add(interpreter);
		}
	}

	/**
	 * Add all Interpreter's in the specified list to the Interpreter
	 * definitions managed by this container.
	 * <p>
	 * If distinguishing valid from invalid Interpreters is important, the
	 * specified Interpreters must have already had their install locations set.
	 * An invalid Interpreter is one whose install location doesn't exist.
	 * </p>
	 *
	 * @param interpreterList
	 *            a list of Interpreters to be added to this container
	 */
	public void addInterpreterList(List<IInterpreterInstall> interpreterList) {
		Iterator<IInterpreterInstall> iterator = interpreterList.iterator();
		while (iterator.hasNext()) {
			IInterpreterInstall Interpreter = iterator.next();
			addInterpreter(Interpreter, false);
		}
	}

	/**
	 * Return a mapping of Interpreter install types to lists of Interpreters.
	 * The keys of this map are instances of
	 * <code>IInterpreterInstallType</code>. The values are instances of
	 * <code>java.util.List</code> which contain instances of
	 * <code>IInterpreterInstall</code>.
	 *
	 * @return Map the mapping of Interpreter install types to lists of
	 *         Interpreters
	 */
	public Map<IInterpreterInstallType, List<IInterpreterInstall>> getInterpreterTypeToInterpreterMap() {
		return fInterTypeToInterMap;
	}

	/**
	 * Return a list of all Interpreters in this container, including any
	 * invalid Interpreters. An invalid Interpreter is one whose install
	 * location does not exist on the file system. The order of the list is not
	 * specified.
	 *
	 * @return List the data structure containing all Interpreters managed by
	 *         this container
	 */
	public List<IInterpreterInstall> getInterpreterList() {
		return fInterpreterList;
	}

	/**
	 * Return filtered list of all Interpreters in this container, including any
	 * invalid Interpreters. An invalid Interpreter is one whose install
	 * location does not exist on the file system. The order of the list is not
	 * specified.
	 *
	 * @return List the data structure containing all Interpreters managed by
	 *         this container
	 */
	public List<IInterpreterInstall> getInterpreterList(
			DefaultInterpreterEntry nature) {
		List<IInterpreterInstall> res = new ArrayList<>(
				fInterpreterList.size());
		for (IInterpreterInstall interpreter : fInterpreterList) {
			final String environmentId = interpreter.getEnvironmentId();
			if (environmentId != null
					&& interpreter.getInterpreterInstallType().getNatureId()
							.equals(nature.getNature())
					&& environmentId.equals(nature.getEnvironment()))
				res.add(interpreter);
		}
		return res;
	}

	/**
	 * Return a list of all valid Interpreters in this container. A valid
	 * Interpreter is one whose install location exists on the file system. The
	 * order of the list is not specified.
	 *
	 * @return List
	 */
	public List<IInterpreterInstall> getValidInterpreterList() {
		List<IInterpreterInstall> Interpreters = getInterpreterList();
		List<IInterpreterInstall> resultList = new ArrayList<>(
				Interpreters.size());
		resultList.addAll(Interpreters);
		// resultList.removeAll(fInvalidInterpreterList);
		return resultList;
	}

	/**
	 * Return filtered list of valid Interpreters in this container. A valid
	 * Interpreter is one whose install location exists on the file system. The
	 * order of the list is not specified.
	 *
	 * @return List
	 */
	public List<IInterpreterInstall> getValidInterpreterList(
			DefaultInterpreterEntry nature) {
		List<IInterpreterInstall> interpreters = getInterpreterList(nature);
		List<IInterpreterInstall> resultList = new ArrayList<>(
				interpreters.size());
		resultList.addAll(interpreters);
		// resultList.removeAll(fInvalidInterpreterList);
		return resultList;
	}

	/**
	 * Returns the composite ID for the default Interpreter. The composite ID
	 * consists of an ID for the Interpreter install type together with an ID
	 * for Interpreter. This is necessary because Interpreter ids by themselves
	 * are not necessarily unique across Interpreter install types.
	 *
	 * @return String returns the composite ID of the current default
	 *         Interpreter
	 */
	public String getDefaultInterpreterInstallCompositeID(
			DefaultInterpreterEntry nature) {
		return fDefaultInterpreterInstallCompositeID.get(nature);
	}

	public String[] getDefaultInterpreterInstallCompositeID() {
		Collection<String> ids = fDefaultInterpreterInstallCompositeID.values();
		return ids.toArray(new String[ids.size()]);
	}

	/**
	 * Sets the composite ID for the default Interpreter. The composite ID
	 * consists of an ID for the Interpreter install type together with an ID
	 * for Interpreter. This is necessary because Interpreter ids by themselves
	 * are not necessarily unique across Interpreter install types.
	 *
	 * @param id
	 *            identifies the new default Interpreter using a composite ID
	 */
	public void setDefaultInterpreterInstallCompositeID(
			DefaultInterpreterEntry nature, String id) {
		if (id != null)
			fDefaultInterpreterInstallCompositeID.put(nature, id);
		else
			fDefaultInterpreterInstallCompositeID.remove(nature);
	}

	/**
	 * Return the default Interpreter's connector type ID.
	 *
	 * @return String the current value of the default Interpreter's connector
	 *         type ID
	 */
	public String getDefaultInterpreterInstallConnectorTypeID(
			DefaultInterpreterEntry nature) {
		return fDefaultInterpreterInstallConnectorTypeID.get(nature);
	}

	/**
	 * Set the default Interpreter's connector type ID.
	 *
	 * @param id
	 *            the new value of the default Interpreter's connector type ID
	 */
	public void setDefaultInterpreterInstallConnectorTypeID(
			DefaultInterpreterEntry nature, String id) {
		fDefaultInterpreterInstallConnectorTypeID.put(nature, id);
	}

	/**
	 * Return the Interpreter definitions contained in this object as a String
	 * of XML. The String is suitable for storing in the workbench preferences.
	 * <p>
	 * The resulting XML is compatible with the static method
	 * <code>parseXMLIntoContainer</code>.
	 * </p>
	 *
	 * @return String the results of flattening this object into XML
	 * @throws IOException
	 *             if this method fails. Reasons include:
	 *             <ul>
	 *             <li>serialization of the XML document failed</li>
	 *             </ul>
	 * @throws ParserConfigurationException
	 *             if creation of the XML document failed
	 * @throws TransformerException
	 *             if serialization of the XML document failed
	 */
	public String getAsXML() throws ParserConfigurationException, IOException,
			TransformerException {

		// Create the Document and the top-level node
		Document doc = DLTKLaunchingPlugin.getDocument();
		Element config = doc.createElement(INTERPRETER_SETTINGS_TAG);
		doc.appendChild(config);

		final Comparator<DefaultInterpreterEntry> comparator = (entry0,
				entry1) -> {
			String k0 = entry0.getEnvironment() + ":" + entry0.getNature(); //$NON-NLS-1$
			String k1 = entry1.getEnvironment() + ":" + entry1.getNature(); //$NON-NLS-1$
			return k0.compareTo(k1);
		};

		// Set the defaultInterpreter attribute on the top-level node
		List<DefaultInterpreterEntry> keys = new ArrayList<>();
		keys.addAll(fDefaultInterpreterInstallCompositeID.keySet());
		Collections.sort(keys, comparator);

		for (Iterator<DefaultInterpreterEntry> iter = keys.iterator(); iter
				.hasNext();) {
			DefaultInterpreterEntry entry = iter.next();
			Element defaulte = doc.createElement(DEFAULT_INTERPRETER_TAG);
			config.appendChild(defaulte);
			defaulte.setAttribute(NATURE_ATTR, entry.getNature());
			defaulte.setAttribute(ENVIRONMENT_ATTR, entry.getEnvironment());
			defaulte.setAttribute(ID_ATTR,
					fDefaultInterpreterInstallCompositeID.get(entry));
		}

		List<DefaultInterpreterEntry> keys2 = new ArrayList<>();
		keys2.addAll(fDefaultInterpreterInstallConnectorTypeID.keySet());
		Collections.sort(keys2, comparator);

		// Set the defaultInterpreterConnector attribute on the top-level node
		for (Iterator<DefaultInterpreterEntry> iter = keys2.iterator(); iter
				.hasNext();) {
			DefaultInterpreterEntry entry = iter.next();
			Element defaulte = doc.createElement("defaultInterpreterConnector"); //$NON-NLS-1$
			config.appendChild(defaulte);
			defaulte.setAttribute(NATURE_ATTR, entry.getNature());
			defaulte.setAttribute(ENVIRONMENT_ATTR, entry.getEnvironment());
			defaulte.setAttribute(ID_ATTR,
					fDefaultInterpreterInstallConnectorTypeID.get(entry));
		}

		// Create a node for each install type represented in this container
		Set<IInterpreterInstallType> InterpreterInstallTypeSet = getInterpreterTypeToInterpreterMap()
				.keySet();
		Iterator<IInterpreterInstallType> keyIterator = InterpreterInstallTypeSet
				.iterator();
		while (keyIterator.hasNext()) {
			IInterpreterInstallType InterpreterInstallType = keyIterator.next();
			Element InterpreterTypeElement = interpreterTypeAsElement(doc,
					InterpreterInstallType);
			config.appendChild(InterpreterTypeElement);
		}

		// Serialize the Document and return the resulting String
		return DLTKLaunchingPlugin.serializeDocument(doc);
	}

	/**
	 * Create and return a node for the specified Interpreter install type in
	 * the specified Document.
	 */
	private Element interpreterTypeAsElement(Document doc,
			IInterpreterInstallType InterpreterType) {

		// Create a node for the Interpreter type and set its 'id' attribute
		Element element = doc.createElement(INTERPRETER_TYPE_TAG);
		element.setAttribute(ID_ATTR, InterpreterType.getId());

		// For each Interpreter of the specified type, create a subordinate node
		// for it
		List<IInterpreterInstall> InterpreterList = getInterpreterTypeToInterpreterMap()
				.get(InterpreterType);
		Iterator<IInterpreterInstall> InterpreterIterator = InterpreterList
				.iterator();
		while (InterpreterIterator.hasNext()) {
			IInterpreterInstall Interpreter = InterpreterIterator.next();
			Element InterpreterElement = interpreterAsElement(doc, Interpreter);
			element.appendChild(InterpreterElement);
		}

		return element;
	}

	/**
	 * Create and return a node for the specified Interpreter in the specified
	 * Document.
	 */
	private Element interpreterAsElement(Document doc,
			IInterpreterInstall interpreter) {

		// Create the node for the Interpreter and set its 'id' & 'name'
		// attributes
		Element element = doc.createElement(INTERPRETER_TAG);
		element.setAttribute(ID_ATTR, interpreter.getId());
		element.setAttribute(INTERPRETER_NAME_ATTR, interpreter.getName());
		element.setAttribute(ENVIRONMENT_ID,
				interpreter.getInstallLocation().getEnvironmentId());

		// Determine and set the 'path' attribute for the Interpreter
		String installPath = ""; //$NON-NLS-1$
		IFileHandle installLocation = interpreter.getRawInstallLocation();
		if (installLocation != null) {
			installPath = installLocation.getPath().toPortableString();
		}
		element.setAttribute(PATH_ATTR, installPath);

		// If the 'libraryLocations' attribute is specified, create a node for
		// it
		LibraryLocation[] libraryLocations = interpreter.getLibraryLocations();
		if (libraryLocations != null) {
			Element libLocationElement = libraryLocationsAsElement(doc,
					libraryLocations);
			element.appendChild(libLocationElement);
		}

		EnvironmentVariable[] environmentVariables = interpreter
				.getEnvironmentVariables();
		if (environmentVariables != null) {
			Element environmentVariableElement = environmentVariablesAsElement(
					doc, environmentVariables);
			element.appendChild(environmentVariableElement);
		}

		final String InterpreterArgs = interpreter.getInterpreterArgs();
		if (InterpreterArgs != null && InterpreterArgs.length() > 0) {
			element.setAttribute(IARGS_ATTR, InterpreterArgs);
		}

		if (interpreter instanceof IInterpreterInstallExtensionContainer) {
			String extensions = ((IInterpreterInstallExtensionContainer) interpreter)
					.saveExtensions();
			if (extensions != null && extensions.length() != 0) {
				final Element extensionsElement = doc
						.createElement(EXTENSIONS_TAG);
				extensionsElement
						.appendChild(doc.createCDATASection(extensions));
				element.appendChild(extensionsElement);
			}
		}

		return element;
	}

	/**
	 * Create and return a 'libraryLocations' node. This node owns subordinate
	 * nodes that list individual library locations.
	 */
	private static Element libraryLocationsAsElement(Document doc,
			LibraryLocation[] locations) {
		Element root = doc.createElement(LIBRARY_LOCATIONS_TAG);
		for (int i = 0; i < locations.length; i++) {
			Element element = doc.createElement(LIBRARY_LOCATION_TAG);
			element.setAttribute(LIBRARY_PATH_ATTR,
					locations[i].getLibraryPath().toString());
			root.appendChild(element);
		}
		return root;
	}

	private static Element environmentVariablesAsElement(Document doc,
			EnvironmentVariable[] variables) {
		Element root = doc.createElement(ENVIRONMENT_VARIABLES_TAG);
		for (int i = 0; i < variables.length; i++) {
			Element element = doc.createElement(ENVIRONMENT_VARIABLE_TAG);
			element.setAttribute(VARIABLE_NAME_ATTR, variables[i].getName());
			element.setAttribute(VARIABLE_VALUE_ATTR, variables[i].getValue());
			root.appendChild(element);
		}
		return root;
	}

	public static InterpreterDefinitionsContainer parseXMLIntoContainer(
			Reader inputStream) throws IOException {
		InterpreterDefinitionsContainer container = new InterpreterDefinitionsContainer();
		parseXMLIntoContainer(inputStream, container);
		return container;
	}

	/**
	 * Parse the Interpreter definitions contained in the specified InputStream
	 * into the specified container.
	 * <p>
	 * The Interpreters in the returned container are instances of
	 * <code>InterpreterStandin</code>.
	 * </p>
	 * <p>
	 * This method has no side-effects. That is, no notifications are sent for
	 * Interpreter adds, changes, deletes, and the workbench preferences are not
	 * affected.
	 * </p>
	 * <p>
	 * If the <code>getAsXML</code> method is called on the returned container
	 * object, the resulting XML will be sematically equivalent (though not
	 * necessarily syntactically equivalent) as the XML contained in
	 * <code>inputStream</code>.
	 * </p>
	 *
	 * @param inputStream
	 *            the <code>InputStream</code> containing XML that declares a
	 *            set of Interpreters and a default Interpreter
	 * @param container
	 *            the container to add the Interpreter defs to
	 * @return InterpreterDefinitionsContainer a container for the Interpreter
	 *         objects declared in <code>inputStream</code>
	 * @throws IOException
	 *             if this method fails. Reasons include:
	 *             <ul>
	 *             <li>the XML in <code>inputStream</code> was badly
	 *             formatted</li>
	 *             <li>the top-level node was not 'InterpreterSettings'</li>
	 *             </ul>
	 *
	 */
	public static void parseXMLIntoContainer(Reader inputStream,
			InterpreterDefinitionsContainer container) throws IOException {

		Element config = null;
		// Wrapper the stream for efficient parsing
		try (BufferedReader stream = new BufferedReader(inputStream)) {

			// Do the parsing and obtain the top-level node
			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			parser.setErrorHandler(new DefaultHandler());
			config = parser.parse(new InputSource(stream)).getDocumentElement();
		} catch (SAXException e) {
			throw new IOException(LaunchingMessages.ScriptRuntime_badFormat);
		} catch (ParserConfigurationException e) {
			throw new IOException(LaunchingMessages.ScriptRuntime_badFormat);
		}

		// If the top-level node wasn't what we expected, bail out
		if (!config.getNodeName().equalsIgnoreCase(INTERPRETER_SETTINGS_TAG)) {
			throw new IOException(LaunchingMessages.ScriptRuntime_badFormat);
		}

		// Traverse the parsed structure and populate the InterpreterType to
		// Interpreter Map
		NodeList list = config.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element interpreterTypeElement = (Element) node;
				if (interpreterTypeElement.getNodeName()
						.equalsIgnoreCase(INTERPRETER_TYPE_TAG)) {
					populateInterpreterTypes(interpreterTypeElement, container);
				}
				if (interpreterTypeElement.getNodeName()
						.equalsIgnoreCase(DEFAULT_INTERPRETER_TAG)) {
					String nature = interpreterTypeElement
							.getAttribute(NATURE_ATTR);
					String id = interpreterTypeElement.getAttribute(ID_ATTR);
					String environment = interpreterTypeElement
							.getAttribute(ENVIRONMENT_ATTR);
					if ("".equals(environment)) { //$NON-NLS-1$
						environment = LocalEnvironment.ENVIRONMENT_ID;
					}
					DefaultInterpreterEntry entry = new DefaultInterpreterEntry(
							nature, environment);
					container.setDefaultInterpreterInstallCompositeID(entry,
							id);
				}
				if (interpreterTypeElement.getNodeName()
						.equalsIgnoreCase("defaultInterpreterConnector")) { //$NON-NLS-1$
					String nature = interpreterTypeElement
							.getAttribute(NATURE_ATTR);
					String environment = interpreterTypeElement
							.getAttribute(ENVIRONMENT_ATTR);
					String id = interpreterTypeElement.getAttribute(ID_ATTR);
					DefaultInterpreterEntry entry = new DefaultInterpreterEntry(
							nature, environment);
					container.setDefaultInterpreterInstallConnectorTypeID(entry,
							id);
				}
			}
		}
	}

	/**
	 * For the specified Interpreter type node, parse all subordinate
	 * Interpreter definitions and add them to the specified container.
	 */
	private static void populateInterpreterTypes(Element interpreterTypeElement,
			InterpreterDefinitionsContainer container) {

		// Retrieve the 'id' attribute and the corresponding Interpreter type
		// object
		String id = interpreterTypeElement.getAttribute(ID_ATTR);
		IInterpreterInstallType InterpreterType = ScriptRuntime
				.getInterpreterInstallType(id);
		if (InterpreterType != null) {

			// For each Interpreter child node, populate the container with a
			// subordinate node
			NodeList InterpreterNodeList = interpreterTypeElement
					.getChildNodes();
			for (int i = 0; i < InterpreterNodeList.getLength(); ++i) {
				Node InterpreterNode = InterpreterNodeList.item(i);
				short type = InterpreterNode.getNodeType();
				if (type == Node.ELEMENT_NODE) {
					Element InterpreterElement = (Element) InterpreterNode;
					if (InterpreterElement.getNodeName()
							.equalsIgnoreCase(INTERPRETER_TAG)) {
						populateInterpreterForType(InterpreterType,
								InterpreterElement, container);
					}
				}
			}
		} else {
			DLTKLaunchingPlugin.log(NLS.bind(
					"Interpreter type element with unknown id \"{0}\".", id)); //$NON-NLS-1$
		}
	}

	/**
	 * Parse the specified Interpreter node, create a InterpreterStandin for it,
	 * and add this to the specified container.
	 */
	private static void populateInterpreterForType(
			IInterpreterInstallType installType, Element element,
			InterpreterDefinitionsContainer container) {
		String id = element.getAttribute(ID_ATTR);
		if (id != null) {

			// Retrieve the 'path' attribute. If none, skip this node.
			String installPath = element.getAttribute(PATH_ATTR);
			if (installPath == null) {
				return;
			}

			String envId = element.getAttribute(ENVIRONMENT_ID);
			// IEnvironment env = EnvironmentManager.getEnvironmentById(envId);
			// if (env == null) {
			// return;
			// }

			// Create a InterpreterStandin for the node and set its 'name' &
			// 'installLocation' attributes
			InterpreterStandin standin = new InterpreterStandin(installType,
					id);
			standin.setName(element.getAttribute(INTERPRETER_NAME_ATTR));
			// IFileHandle installLocation = env.getFile(new Path(installPath));
			// standin.setInstallLocation(installLocation);
			standin.setInstallLocation(
					new LazyFileHandle(envId, new Path(installPath)));

			container.addInterpreter(standin, false);

			// Look for subordinate nodes. These may be 'libraryLocation',
			// 'libraryLocations' or 'versionInfo'.
			NodeList list = element.getChildNodes();
			int length = list.getLength();
			for (int i = 0; i < length; ++i) {
				Node node = list.item(i);
				short type = node.getNodeType();
				if (type == Node.ELEMENT_NODE) {
					Element subElement = (Element) node;
					String subElementName = subElement.getNodeName();
					if (subElementName.equals(LIBRARY_LOCATION_TAG)) {
						LibraryLocation loc = getLibraryLocation(subElement);
						standin.setLibraryLocations(
								new LibraryLocation[] { loc });
					} else if (subElementName.equals(LIBRARY_LOCATIONS_TAG)) {
						setLibraryLocations(standin, subElement);
					} else if (subElementName
							.equals(ENVIRONMENT_VARIABLE_TAG)) {
						EnvironmentVariable var = getEnvironmentVariable(
								subElement);
						standin.setEnvironmentVariables(
								new EnvironmentVariable[] { var });
					} else if (subElementName
							.equals(ENVIRONMENT_VARIABLES_TAG)) {
						setEnvironmentVariables(standin, subElement);
					} else if (subElementName.equals(EXTENSIONS_TAG)) {
						StringBuffer body = null;
						NodeList children = subElement.getChildNodes();
						for (int j = 0; j < children.getLength(); ++j) {
							Node child = children.item(j);
							if (child
									.getNodeType() == Node.CDATA_SECTION_NODE) {
								if (body == null) {
									body = new StringBuffer();
								}
								body.append(((CharacterData) child).getData());
							}
						}
						if (body != null) {
							standin.loadExtensions(body.toString());
						}
					}
				}
			}

			// Interpreter Arguments
			String args = element.getAttribute(IARGS_ATTR);
			if (args != null && args.length() > 0) {
				standin.setInterpreterArgs(args);
			}
		} else {
			DLTKLaunchingPlugin.log(
					"id attribute missing from Interpreter element specification."); //$NON-NLS-1$
		}
	}

	/**
	 * Create & return a LibraryLocation object populated from the attribute
	 * values in the specified node.
	 */
	private static LibraryLocation getLibraryLocation(
			Element libLocationElement) {
		String interpreterEnvironmentArchive = libLocationElement
				.getAttribute(LIBRARY_PATH_ATTR);
		if (interpreterEnvironmentArchive != null) {
			return new LibraryLocation(
					Path.fromPortableString(interpreterEnvironmentArchive));
		}
		DLTKLaunchingPlugin
				.log("Library location element is specified incorrectly."); //$NON-NLS-1$
		return null;
	}

	private static EnvironmentVariable getEnvironmentVariable(
			Element libLocationElement) {
		String name = libLocationElement.getAttribute(VARIABLE_NAME_ATTR);
		String value = libLocationElement.getAttribute(VARIABLE_VALUE_ATTR);
		if (name != null && value != null) {
			return new EnvironmentVariable(name, value);
		}
		DLTKLaunchingPlugin
				.log("Environment variable element is specified incorrectly."); //$NON-NLS-1$
		return null;
	}

	/**
	 * Set the LibraryLocations on the specified Interpreter, by extracting the
	 * subordinate nodes from the specified 'lirbaryLocations' node.
	 */
	private static void setLibraryLocations(IInterpreterInstall interpreter,
			Element libLocationsElement) {
		NodeList list = libLocationsElement.getChildNodes();
		int length = list.getLength();
		List<LibraryLocation> locations = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element libraryLocationElement = (Element) node;
				if (libraryLocationElement.getNodeName()
						.equals(LIBRARY_LOCATION_TAG)) {
					locations.add(getLibraryLocation(libraryLocationElement));
				}
			}
		}
		interpreter.setLibraryLocations(
				locations.toArray(new LibraryLocation[locations.size()]));
	}

	private static void setEnvironmentVariables(IInterpreterInstall interpreter,
			Element environmentVariablesElement) {
		NodeList list = environmentVariablesElement.getChildNodes();
		int length = list.getLength();
		List<EnvironmentVariable> variables = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element envVarElement = (Element) node;
				if (envVarElement.getNodeName()
						.equals(ENVIRONMENT_VARIABLE_TAG)) {
					variables.add(getEnvironmentVariable(envVarElement));
				}
			}
		}
		interpreter.setEnvironmentVariables(
				variables.toArray(new EnvironmentVariable[variables.size()]));
	}

	/**
	 * Removes the Interpreter from this container.
	 *
	 * @param interpreter
	 *            Interpreter intall
	 */
	public void removeInterpreter(IInterpreterInstall interpreter) {
		fInterpreterList.remove(interpreter);
		// fInvalidInterpreterList.remove(Interpreter);
		List<IInterpreterInstall> list = fInterTypeToInterMap
				.get(interpreter.getInterpreterInstallType());
		if (list != null) {
			list.remove(interpreter);
		}
	}

}
