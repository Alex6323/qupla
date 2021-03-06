package org.iota.qupla.expression;

import org.iota.qupla.expression.base.BaseExpr;
import org.iota.qupla.expression.base.BaseSubExpr;
import org.iota.qupla.parser.Token;
import org.iota.qupla.parser.Tokenizer;

public class PostfixExpr extends BaseSubExpr
{
  public PostfixExpr(final PostfixExpr copy)
  {
    super(copy);
  }

  public PostfixExpr(final Tokenizer tokenizer)
  {
    super(tokenizer);

    switch (tokenizer.tokenId())
    {
    case Token.TOK_FUNC_OPEN:
      expr = new SubExpr(tokenizer);
      return;

    case Token.TOK_FLOAT:
    case Token.TOK_NUMBER:
    case Token.TOK_MINUS:
      expr = new IntegerExpr(tokenizer);
      return;
    }

    final Token varName = expect(tokenizer, Token.TOK_NAME, "variable name");
    name = varName.text;

    switch (tokenizer.tokenId())
    {
    case Token.TOK_TEMPL_OPEN:
    case Token.TOK_FUNC_OPEN:
      expr = new FuncExpr(tokenizer, varName);
      return;

    case Token.TOK_GROUP_OPEN:
      expr = new TypeExpr(tokenizer, varName);
      return;
    }

    for (int i = scope.size() - 1; i >= 0; i--)
    {
      final BaseExpr var = scope.get(i);
      if (var.name.equals(name))
      {
        expr = new SliceExpr(tokenizer, varName);
        return;
      }
    }

    expr = new LutExpr(tokenizer, varName);
  }

  @Override
  public BaseExpr append()
  {
    return append(expr);
  }

  @Override
  public BaseExpr clone()
  {
    return new PostfixExpr(this);
  }

  @Override
  public BaseExpr optimize()
  {
    return expr;
  }
}
