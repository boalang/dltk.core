/*******************************************************************************
 * Copyright (c) 2007, 2016 IBM Corporation and others.
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

import java.io.IOException;

import org.eclipse.core.runtime.Assert;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.testing.ITestSession;
import org.eclipse.dltk.testing.model.ITestElement;
import org.eclipse.dltk.testing.model.ITestElement.FailureTrace;
import org.eclipse.dltk.testing.model.ITestElement.ProgressState;
import org.eclipse.dltk.testing.model.ITestElement.Result;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TestRunSessionSerializer implements XMLReader {

	private static final String EMPTY= ""; //$NON-NLS-1$
	private static final String CDATA= "CDATA"; //$NON-NLS-1$
	private static final Attributes NO_ATTS= new AttributesImpl();


	private final TestRunSession fTestRunSession;
	private ContentHandler fHandler;
	private ErrorHandler fErrorHandler;
	
	private final NumberFormat timeFormat= new DecimalFormat("0.0##"); //$NON-NLS-1$ // not localized, parseable by Double.parseDouble(..)
	
	/**
	 * @param testRunSession the test run session to serialize
	 */
	public TestRunSessionSerializer(TestRunSession testRunSession) {
		Assert.isNotNull(testRunSession);
		fTestRunSession= testRunSession;
	}

	@Override
	public void parse(InputSource input) throws IOException, SAXException {
		if (fHandler == null)
			throw new SAXException("ContentHandler missing"); //$NON-NLS-1$

		fHandler.startDocument();
		handleTestRun(fTestRunSession);
		fHandler.endDocument();
	}

	private void handleTestRun(ITestSession testRunSession) throws SAXException {
		AttributesImpl atts= new AttributesImpl();
		addCDATA(atts, IXMLTags.ATTR_NAME, fTestRunSession.getTestRunName());
		IScriptProject project= fTestRunSession.getLaunchedProject();
		if (project != null)
			addCDATA(atts, IXMLTags.ATTR_PROJECT, project.getElementName());
		addCDATA(atts, IXMLTags.ATTR_TESTS, fTestRunSession.getTotalCount());
		addCDATA(atts, IXMLTags.ATTR_STARTED, fTestRunSession.getStartedCount());
		addCDATA(atts, IXMLTags.ATTR_FAILURES, fTestRunSession.getFailureCount());
		addCDATA(atts, IXMLTags.ATTR_ERRORS, fTestRunSession.getErrorCount());
		addCDATA(atts, IXMLTags.ATTR_IGNORED, fTestRunSession.getIgnoredCount());
		startElement(IXMLTags.NODE_TESTRUN, atts);
		
		
		TestRoot testRoot= fTestRunSession.getTestRoot();
		ITestElement[] topSuites= testRoot.getChildren();
		for (int i= 0; i < topSuites.length; i++) {
			handleTestElement(topSuites[i]);
		}
		
		endElement(IXMLTags.NODE_TESTRUN);
	}

	private void handleTestElement(ITestElement testElement) throws SAXException {
		if (testElement instanceof TestCategoryElement) {
			TestCategoryElement category = (TestCategoryElement) testElement;
			AttributesImpl atts = new AttributesImpl();
			addCDATA(atts, IXMLTags.ATTR_ID, category.getId());
			addCDATA(atts, IXMLTags.ATTR_NAME, category.getCategoryName());
			startElement(IXMLTags.NODE_CATEGORY, atts);
			ITestElement[] children = category.getChildren();
			for (int i = 0; i < children.length; i++) {
				handleTestElement(children[i]);
			}
			endElement(IXMLTags.NODE_CATEGORY);
		}
		else if (testElement instanceof TestSuiteElement) {
			TestSuiteElement testSuiteElement= (TestSuiteElement) testElement;
			
			AttributesImpl atts= new AttributesImpl();
			addCDATA(atts, IXMLTags.ATTR_NAME, testSuiteElement.getSuiteTypeName());
			if (! Double.isNaN(testSuiteElement.getElapsedTimeInSeconds()))
									addCDATA(atts, IXMLTags.ATTR_TIME, timeFormat.format(testSuiteElement.getElapsedTimeInSeconds()));
			if (testElement.getProgressState() != ProgressState.COMPLETED || testElement.getTestResult(false) != Result.UNDEFINED)
				addCDATA(atts, IXMLTags.ATTR_INCOMPLETE, Boolean.TRUE.toString());
			
			startElement(IXMLTags.NODE_TESTSUITE, atts);
			addFailure(testElement);
			
			ITestElement[] children= testSuiteElement.getChildren();
			for (int i= 0; i < children.length; i++) {
				handleTestElement(children[i]);
			}
			endElement(IXMLTags.NODE_TESTSUITE);
			
		} else if (testElement instanceof TestCaseElement) {
			TestCaseElement testCaseElement= (TestCaseElement) testElement;
			
			AttributesImpl atts= new AttributesImpl();
			addCDATA(atts, IXMLTags.ATTR_NAME, testCaseElement.getTestName());
			if (! Double.isNaN(testCaseElement.getElapsedTimeInSeconds()))
									addCDATA(atts, IXMLTags.ATTR_TIME, timeFormat.format(testCaseElement.getElapsedTimeInSeconds()));
			
			if (testElement.getProgressState() != ProgressState.COMPLETED)
				addCDATA(atts, IXMLTags.ATTR_INCOMPLETE, Boolean.TRUE.toString());
			if (testCaseElement.isIgnored())
				addCDATA(atts, IXMLTags.ATTR_IGNORED, Boolean.TRUE.toString());
			
			startElement(IXMLTags.NODE_TESTCASE, atts);
			addFailure(testElement);
			
			endElement(IXMLTags.NODE_TESTCASE);
			
		} else {
			throw new IllegalStateException(String.valueOf(testElement));
		}
		
	}

	private void addFailure(ITestElement testElement) throws SAXException {
		FailureTrace failureTrace= testElement.getFailureTrace();
		if (failureTrace != null) {
			AttributesImpl failureAtts= new AttributesImpl();
//				addCDATA(failureAtts, IXMLTags.ATTR_MESSAGE, xx);
//				addCDATA(failureAtts, IXMLTags.ATTR_TYPE, xx);
			String failureKind= testElement.getTestResult(false) == Result.ERROR ? IXMLTags.NODE_ERROR : IXMLTags.NODE_FAILURE;
			startElement(failureKind, failureAtts);
			String expected= failureTrace.getExpected();
			String actual= failureTrace.getActual();
			if (expected != null) {
				startElement(IXMLTags.NODE_EXPECTED, NO_ATTS);
				fHandler.characters(expected.toCharArray(), 0, expected.length());
				endElement(IXMLTags.NODE_EXPECTED);
			}
			if (actual != null) {
				startElement(IXMLTags.NODE_ACTUAL, NO_ATTS);
				fHandler.characters(actual.toCharArray(), 0, actual.length());
				endElement(IXMLTags.NODE_ACTUAL);
			}
			String trace= failureTrace.getTrace();
			fHandler.characters(trace.toCharArray(), 0, trace.length());
			endElement(failureKind);
		}
	}

	private void startElement(String name, Attributes atts) throws SAXException {
		fHandler.startElement(EMPTY, name, name, atts);
	}

	private void endElement(String name) throws SAXException {
		fHandler.endElement(EMPTY, name, name);
	}

	private static void addCDATA(AttributesImpl atts, String name, int value) {
		addCDATA(atts, name, Integer.toString(value));
	}
	
	private static void addCDATA(AttributesImpl atts, String name, String value) {
		atts.addAttribute(EMPTY, EMPTY, name, CDATA, value);
	}

	@Override
	public void setContentHandler(ContentHandler handler) {
		this.fHandler= handler;
	}

	@Override
	public ContentHandler getContentHandler() {
		return fHandler;
	}

	@Override
	public void setErrorHandler(ErrorHandler handler) {
		fErrorHandler= handler;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return fErrorHandler;
	}

	// ignored:

	@Override
	public void parse(String systemId) throws IOException, SAXException {
	}

	@Override
	public void setDTDHandler(DTDHandler handler) {
	}
	
	@Override
	public DTDHandler getDTDHandler() {
		return null;
	}

	@Override
	public void setEntityResolver(EntityResolver resolver) {
	}
	
	@Override
	public EntityResolver getEntityResolver() {
		return null;
	}

	@Override
	public void setProperty(java.lang.String name, java.lang.Object value) {
	}
	
	@Override
	public Object getProperty(java.lang.String name) {
		return null;
	}

	@Override
	public void setFeature(java.lang.String name, boolean value) {
	}
	
	@Override
	public boolean getFeature(java.lang.String name) {
		return false;
	}
}
