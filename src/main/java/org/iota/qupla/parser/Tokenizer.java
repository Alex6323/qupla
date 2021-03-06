package org.iota.qupla.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.iota.qupla.exception.CodeException;

public class Tokenizer
{
  private static final HashMap<String, Integer> symbolMap = new HashMap<>();
  private static final ArrayList<String> symbols = new ArrayList<>();
  private static final HashMap<String, Integer> tokenMap = new HashMap<>();
  private static final String[] tokens = new String[50];

  public int colNr;
  public int lineNr;
  public final ArrayList<String> lines = new ArrayList<>();
  public Module module;
  private Token token;

  private static void addToken(final int id, final String name)
  {
    tokens[id] = name;
    tokenMap.put(name, id);
  }

  public Token currentToken()
  {
    return token;
  }

  public Token nextToken()
  {
    // end of file?
    if (lineNr == lines.size())
    {
      token = new Token();
      token.source = module.currentSource;
      token.lineNr = lineNr;
      token.id = Token.TOK_EOF;
      return token;
    }

    final String line = lines.get(lineNr);

    // skip whitespace
    while (colNr < line.length() && Character.isWhitespace(line.charAt(colNr)))
    {
      colNr++;
    }

    // end of line?
    if (colNr == line.length())
    {
      // process next line instead
      lineNr++;
      colNr = 0;
      return nextToken();
    }

    // found start of next token
    token = new Token();
    token.source = module.currentSource;
    token.lineNr = lineNr;
    token.colNr = colNr;
    token.text = line.substring(colNr);

    // first parse multi-character tokens

    // skip comment-to-end-of-line
    if (token.text.startsWith("//"))
    {
      lineNr++;
      colNr = 0;
      return nextToken();
    }

    // parse single-character tokens
    final Integer tokenId = tokenMap.get(token.text.substring(0, 1));
    if (tokenId != null)
    {
      token.id = tokenId;
      token.text = tokens[tokenId];
      colNr++;
      return token;
    }

    // number?
    char c = token.text.charAt(0);
    if (Character.isDigit(c))
    {
      token.id = Token.TOK_NUMBER;
      for (int i = 0; i < token.text.length(); i++)
      {
        c = token.text.charAt(i);
        if (Character.isDigit(c))
        {
          continue;
        }

        if (token.id == Token.TOK_NUMBER && c == '.')
        {
          token.id = Token.TOK_FLOAT;
          continue;
        }

        token.text = token.text.substring(0, i);
        break;
      }

      colNr += token.text.length();
      return token;
    }

    // identifier?
    if (Character.isLetter(c) || c == '_')
    {
      token.id = Token.TOK_NAME;
      for (int i = 1; i < token.text.length(); i++)
      {
        c = token.text.charAt(i);
        if (Character.isLetterOrDigit(c) || Character.isDigit(c) || c == '_')
        {
          continue;
        }

        token.text = token.text.substring(0, i);
        break;
      }

      colNr += token.text.length();

      // check for keywords
      final Integer keyword = tokenMap.get(token.text);
      if (keyword != null)
      {
        token.id = keyword;
        return token;
      }

      final Integer symbol = symbolMap.get(token.text);
      if (symbol != null)
      {
        token.text = symbols.get(symbol);
        token.symbol = symbol;
      }
      else
      {
        token.symbol = symbols.size();
        symbolMap.put(token.text, token.symbol);
        symbols.add(token.text);
      }

      return token;
    }

    throw new CodeException(token, "Invalid character: '" + c + "'");
  }

  public ArrayList<String> readFile(final File in)
  {
    try
    {
      lines.clear();
      if (in.exists())
      {
        final BufferedReader br = new BufferedReader(new FileReader(in));
        for (String line = br.readLine(); line != null; line = br.readLine())
        {
          lines.add(line);
        }
        br.close();
      }
    }
    catch (final Exception e)
    {
      e.printStackTrace();
    }

    lineNr = 0;
    colNr = 0;
    return lines;
  }

  @Override
  public String toString()
  {
    final String line = lineNr < lines.size() ? lines.get(lineNr).substring(colNr) : "";
    return (token == null ? "" : token.text) + " | " + line;
  }

  public int tokenId()
  {
    return token.id;
  }

  static
  {
    addToken(Token.TOK_AFFECT, "affect");
    addToken(Token.TOK_DELAY, "delay");
    addToken(Token.TOK_EVAL, "eval");
    addToken(Token.TOK_FUNC, "func");
    addToken(Token.TOK_IMPORT, "import");
    addToken(Token.TOK_JOIN, "join");
    addToken(Token.TOK_LIMIT, "limit");
    addToken(Token.TOK_LUT, "lut");
    addToken(Token.TOK_RETURN, "return");
    addToken(Token.TOK_STATE, "state");
    addToken(Token.TOK_TEMPLATE, "template");
    addToken(Token.TOK_TEST, "test");
    addToken(Token.TOK_TYPE, "type");
    addToken(Token.TOK_USE, "use");

    addToken(Token.TOK_ARRAY_OPEN, "[");
    addToken(Token.TOK_ARRAY_CLOSE, "]");
    addToken(Token.TOK_FUNC_OPEN, "(");
    addToken(Token.TOK_FUNC_CLOSE, ")");
    addToken(Token.TOK_TEMPL_OPEN, "<");
    addToken(Token.TOK_TEMPL_CLOSE, ">");
    addToken(Token.TOK_GROUP_OPEN, "{");
    addToken(Token.TOK_GROUP_CLOSE, "}");
    addToken(Token.TOK_PLUS, "+");
    addToken(Token.TOK_MINUS, "-");
    addToken(Token.TOK_MUL, "*");
    addToken(Token.TOK_DIV, "/");
    addToken(Token.TOK_MOD, "%");
    addToken(Token.TOK_EQUAL, "=");
    addToken(Token.TOK_DOT, ".");
    addToken(Token.TOK_COMMA, ",");
    addToken(Token.TOK_CONCAT, "&");
    addToken(Token.TOK_MERGE, "|");
    addToken(Token.TOK_QUESTION, "?");
    addToken(Token.TOK_COLON, ":");
  }
}
