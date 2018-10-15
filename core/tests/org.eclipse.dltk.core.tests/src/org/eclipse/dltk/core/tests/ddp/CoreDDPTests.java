/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.core.tests.ddp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.expressions.NumericLiteral;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ti.DefaultTypeInferencer;
import org.eclipse.dltk.ti.GoalState;
import org.eclipse.dltk.ti.IGoalEvaluatorFactory;
import org.eclipse.dltk.ti.ITypeInferencer;
import org.eclipse.dltk.ti.goals.ExpressionTypeGoal;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.junit.Test;

public class CoreDDPTests {

	private static final class FixedAnswerGoalEvaluator extends GoalEvaluator {
		private final IEvaluatedType answer;

		private FixedAnswerGoalEvaluator(IGoal goal, IEvaluatedType answer) {
			super(goal);
			this.answer = answer;
		}

		@Override
		public Object produceResult() {
			return answer;
		}

		@Override
		public IGoal[] init() {
			return IGoal.NO_GOALS;
		}

		@Override
		public IGoal[] subGoalDone(IGoal goal2, Object result,
				GoalState state) {
			return IGoal.NO_GOALS;
		}

	}

	private static final class SingleDependentGoalEvaluator
			extends GoalEvaluator {
		private final IEvaluatedType answer;

		private final IGoal[] dependents;

		// private int state = 0;

		private int produceCalls = 0;

		private int produceTypeCalls = 0;

		private SingleDependentGoalEvaluator(IGoal goal, IGoal dependent,
				IEvaluatedType answer) {
			super(goal);
			this.dependents = new IGoal[] { dependent };
			this.answer = answer;
		}

		private SingleDependentGoalEvaluator(IGoal goal, IGoal[] dependents,
				Object answer) {
			super(goal);
			this.dependents = dependents;
			this.answer = (IEvaluatedType) answer;
		}

		@Override
		public IGoal[] init() {
			++produceCalls;
			return dependents;
		}

		@Override
		public IGoal[] subGoalDone(IGoal goal2, Object result,
				GoalState _state) {
			++produceCalls;
			assertTrue(
					result instanceof MyNum || _state == GoalState.RECURSIVE);
			return IGoal.NO_GOALS;
		}

		@Override
		public Object produceResult() {
			++produceTypeCalls;
			return answer;
		}

		public void assertState() {
			assertEquals(1, produceTypeCalls);
			assertEquals(1 + dependents.length, produceCalls);
		}

	}

	class MyNum implements IEvaluatedType {

		@Override
		public String toString() {
			return "MyNum";
		}

		@Override
		public String getTypeName() {
			return "MyNum";
		}

		@Override
		public boolean subtypeOf(IEvaluatedType type) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	@Test
	public void testSimple() throws Exception {
		// y = 2; x = y; x?
		final Expression x = new SimpleReference(0, 0, "x");
		final Expression y = new SimpleReference(0, 0, "y");
		final Expression num = new NumericLiteral(0, 0, 0);

		IGoalEvaluatorFactory factory = goal -> {
			if (goal instanceof ExpressionTypeGoal) {
				ExpressionTypeGoal egoal = (ExpressionTypeGoal) goal;
				ASTNode expr = egoal.getExpression();
				if (expr == x)
					return new SingleDependentGoalEvaluator(goal,
							new ExpressionTypeGoal(null, y), new MyNum());
				if (expr == y)
					return new SingleDependentGoalEvaluator(goal,
							new ExpressionTypeGoal(null, num), new MyNum());
				if (expr == num)
					return new FixedAnswerGoalEvaluator(goal, new MyNum());
			}
			return null;
		};

		final ITypeInferencer man = new DefaultTypeInferencer(factory);

		ExpressionTypeGoal rootGoal = new ExpressionTypeGoal(null, x);
		IEvaluatedType answer = man.evaluateType(rootGoal, -1);

		assertTrue(answer instanceof MyNum);
	}

	@Test
	public void testCycles() throws Exception {
		final Expression x = new SimpleReference(0, 0, "x");
		final Expression y = new SimpleReference(0, 0, "y");
		final Expression z = new SimpleReference(0, 0, "z");
		final Expression num = new NumericLiteral(0, 0, 0);

		final Collection<GoalEvaluator> evaluators = new ArrayList<>();
		IGoalEvaluatorFactory factory = new IGoalEvaluatorFactory() {

			public GoalEvaluator createEvaluator2(IGoal goal) {
				if (goal instanceof ExpressionTypeGoal) {
					ExpressionTypeGoal egoal = (ExpressionTypeGoal) goal;
					ASTNode expr = egoal.getExpression();
					if (expr == x)
						return new SingleDependentGoalEvaluator(goal,
								new IGoal[] { new ExpressionTypeGoal(null, y),
										new ExpressionTypeGoal(null, z) },
								new MyNum());
					if (expr == y)
						return new SingleDependentGoalEvaluator(goal,
								new IGoal[] { new ExpressionTypeGoal(null, z) },
								new MyNum());
					if (expr == z)
						return new SingleDependentGoalEvaluator(goal,
								new IGoal[] { new ExpressionTypeGoal(null, num),
										new ExpressionTypeGoal(null, y) },
								new MyNum());
					if (expr == num)
						return new FixedAnswerGoalEvaluator(goal, new MyNum());
				}
				return null;
			}

			@Override
			public GoalEvaluator createEvaluator(IGoal goal) {
				GoalEvaluator result = createEvaluator2(goal);
				if (result != null)
					evaluators.add(result);
				return result;
			}

		};

		final ITypeInferencer man = new DefaultTypeInferencer(factory);

		ExpressionTypeGoal rootGoal = new ExpressionTypeGoal(null, x);
		IEvaluatedType answer = man.evaluateType(rootGoal, -1);

		assertTrue(answer instanceof MyNum);
		for (Iterator<GoalEvaluator> iter = evaluators.iterator(); iter
				.hasNext();) {
			GoalEvaluator ev = iter.next();
			if (ev instanceof SingleDependentGoalEvaluator) {
				SingleDependentGoalEvaluator sdge = (SingleDependentGoalEvaluator) ev;
				sdge.assertState();
			}
		}
	}
}
