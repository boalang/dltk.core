/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.ti.goals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.search.FieldReferenceMatch;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.MethodReferenceMatch;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.IContext;
import org.eclipse.dltk.ti.ISourceModuleContext;

public abstract class SearchBasedGoalEvaluator extends GoalEvaluator {

	private List<IGoal> possiblePositionsGoals = new ArrayList<>();
	private List<ItemReference> references = new ArrayList<>();

	private SearchRequestor requestor = new SearchRequestor() {

		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			ASTNode node = null;
			if (match instanceof FieldReferenceMatch) {
				FieldReferenceMatch match2 = (FieldReferenceMatch) match;
				node = match2.getNode();
			} else if (match instanceof MethodReferenceMatch) {
				MethodReferenceMatch match2 = (MethodReferenceMatch) match;
				node = match2.getNode();
			}
			PossiblePosition pos = new PossiblePosition(match.getResource(),
					match.getOffset(), match.getLength(), node);
			possiblePositionsGoals.add(createVerificationGoal(pos));
		}

	};

	public SearchBasedGoalEvaluator(IGoal goal) {
		super(goal);
	}

	@Override
	public IGoal[] init() {
		IGoal goal = getGoal();
		IScriptProject project = null;
		IContext context = goal.getContext();
		if (context instanceof ISourceModuleContext) {
			ISourceModuleContext basicContext = (ISourceModuleContext) goal
					.getContext();
			project = basicContext.getSourceModule().getScriptProject();
		}
		if (project == null) {
			return null;
		}
		IDLTKSearchScope scope = SearchEngine.createSearchScope(project);
		SearchPattern pattern = createSearchPattern(scope.getLanguageToolkit());
		SearchEngine engine = new SearchEngine();

		try {
			engine.search(pattern,
					new SearchParticipant[] {
							SearchEngine.getDefaultSearchParticipant() },
					scope, requestor, null);
		} catch (CoreException e) {
			e.printStackTrace();
			return IGoal.NO_GOALS;
		}

		return possiblePositionsGoals
				.toArray(new IGoal[possiblePositionsGoals.size()]);
	}

	@Override
	public IGoal[] subGoalDone(IGoal subgoal, Object result, GoalState state) {
		if (result != null && result instanceof ItemReference) {
			references.add((ItemReference) result);
		}
		return IGoal.NO_GOALS;
	}

	@Override
	public Object produceResult() {
		return references.toArray(new ItemReference[references.size()]);
	}

	protected abstract SearchPattern createSearchPattern(
			IDLTKLanguageToolkit toolkit);

	protected abstract IGoal createVerificationGoal(PossiblePosition pos);

}
