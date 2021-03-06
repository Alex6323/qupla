package org.iota.qupla.expression;

import org.iota.qupla.expression.base.BaseExpr;
import org.iota.qupla.expression.constant.ConstNumber;
import org.iota.qupla.parser.Token;
import org.iota.qupla.parser.Tokenizer;

public class JoinExpr extends BaseExpr
{
  public BaseExpr limit;

  public JoinExpr(final JoinExpr copy)
  {
    super(copy);

    limit = clone(copy.limit);
  }

  public JoinExpr(final Tokenizer tokenizer)
  {
    super(tokenizer);

    expect(tokenizer, Token.TOK_JOIN, "join");

    final Token varName = expect(tokenizer, Token.TOK_NAME, "environment name");
    name = varName.text;

    if (tokenizer.tokenId() == Token.TOK_LIMIT)
    {
      tokenizer.nextToken();
      limit = new ConstNumber(tokenizer);
    }
  }

  @Override
  public void analyze()
  {
    if (limit != null)
    {
      limit.analyze();
    }
  }

  @Override
  public BaseExpr append()
  {
    append("join ").append(name);
    if (limit != null)
    {
      append(" limit ").append(limit);
    }

    return this;
  }

  @Override
  public BaseExpr clone()
  {
    return new JoinExpr(this);
  }

}
