package org.iota.qupla.statement.helper;

import org.iota.qupla.expression.base.BaseExpr;
import org.iota.qupla.expression.constant.ConstExpr;
import org.iota.qupla.parser.Token;
import org.iota.qupla.parser.Tokenizer;

public class TritVectorDef extends BaseExpr
{
  public BaseExpr typeExpr;

  public TritVectorDef(final TritVectorDef copy)
  {
    super(copy);

    typeExpr = clone(copy.typeExpr);
  }

  public TritVectorDef(final Tokenizer tokenizer, final Token identifier)
  {
    super(tokenizer, identifier);

    name = identifier == null ? null : identifier.text;

    expect(tokenizer, Token.TOK_ARRAY_OPEN, "'['");

    typeExpr = new ConstExpr(tokenizer).optimize();

    expect(tokenizer, Token.TOK_ARRAY_CLOSE, "']'");
  }

  @Override
  public void analyze()
  {
    typeExpr.analyze();
    typeInfo = typeExpr.typeInfo;
    size = typeExpr.size;
    if (size <= 0)
    {
      error("Invalid trit vector size value");
    }
  }

  @Override
  public BaseExpr append()
  {
    if (name != null)
    {
      append(name).append(" ");
    }

    return append("[").append(typeExpr).append("]");
  }

  @Override
  public BaseExpr clone()
  {
    return new TritVectorDef(this);
  }
}
