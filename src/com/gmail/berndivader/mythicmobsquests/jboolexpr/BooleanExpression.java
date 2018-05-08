package com.gmail.berndivader.mythicmobsquests.jboolexpr;

public abstract class BooleanExpression implements IBoolean {
	BooleanExpression() {
		// Nothing
	}

	public static BooleanExpression readLR(final String booleanExpression) throws MalformedBooleanException {
		return new BooleanExpressionLR(booleanExpression);
	}
}
