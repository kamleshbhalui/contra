import java.util.List;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
                    
public class Tokenizer
{    
    public boolean IsOp(char chr)
    {   
        boolean addOp = chr == '+' || chr == '-';
        boolean mulOp = chr == '*' || chr == '/';
        
        boolean compOp = chr == '<' || chr == '>' || chr == '=';
        boolean lgicOp = chr == '!' || chr == '|' || chr == '&';
        
        return addOp || mulOp || compOp || lgicOp;
    }

    public TokenType FindOpType(char firstOperator, char nextChar)
    {
        TokenType type = TokenType.UNKNOWN;
        switch(firstOperator)
        {
            case '+':
                type = TokenType.ADD;
            break;
            case '-':
                type = TokenType.SUBTRACT;
            break;
            case '*':
                type = TokenType.MULTIPLY;
            break;
            case '/':
                type = TokenType.DIVIDE;
            break;
            case '<':
                type = TokenType.LESS;
                if (nextChar == '=') type = TokenType.LESSEQUAL;
            break;            
            case '>':
                type = TokenType.GREATER;
                if (nextChar == '=') type = TokenType.GREATEREQUAL;
            break;
            case '=':
                type = TokenType.ASSIGNMENT;
                if (nextChar == '=') type = TokenType.EQUAL;
            break;            
            case '!':
                type = TokenType.NOT;
                if (nextChar == '=') type = TokenType.NOTEQUAL;
            break;
            case '|':
                type = TokenType.OR;
            break;
            case '&':
                type = TokenType.AND;
            break;
        }
        return type;
    }

    public boolean IsParen(char chr)
    {   
        boolean prntOp = chr == '(' || chr == ')';
        boolean brktOp = chr == '[' || chr == ']';        
        
        return prntOp || brktOp;
    }

    public TokenType FindParenType(char chr)
    {
        TokenType type = TokenType.UNKNOWN;
        switch(chr)
        {
            case '(':
                type = TokenType.LEFT_PAREN;
            break;
            case ')':
                type = TokenType.RIGHT_PAREN;
            break;        
            case '[':
                type = TokenType.LEFT_BRACKET;
            break;
            case ']':
                type = TokenType.RIGHT_BRACKET;
            break;
        }
        return type;
    }

    public boolean IsPunc(char chr)
    {   
        boolean puncOp = chr == ',';
        return puncOp;        
    }
        
    public TokenType FindPuncType(char firstOperator)
    {
        TokenType type = TokenType.UNKNOWN;
        switch(firstOperator)
        {
            case ',':
                type = TokenType.COMMA;
            break;
        }
        return type;
    }
                      
    public TokenType FindStatementType(String str)
    {
        TokenType type = TokenType.UNKNOWN;        
        switch(str)
        {
            case "script":
                type = TokenType.SCRIPT;
            break;                
            case "end":
                type = TokenType.END;
            break;        
            case "while":
                type = TokenType.WHILE;
            break;
            case "if":
                type = TokenType.IF;
            break;            
            case "else":
                type = TokenType.ELSE;
            break;
            case "def":
                type = TokenType.DEF;
            break;                                  
            default:
                type = TokenType.KEYWORD;
        }
        return type;
    }

    public List<Token> Tokenize(String source)
    {
        List<Token> tokens = new ArrayList<Token>();
        
        Token token = null;
        String tokenText = "";
        char firstOperator = '\0';      
        TokenizeState state = TokenizeState.DEFAULT;
        
        for (int index = 0; index < source.length(); index++)
        {
            char chr = source.charAt(index); 
            switch (state) 
            {
                case DEFAULT:
                    if (IsOp(chr))
                    {
                        firstOperator = chr;
                        TokenType opType = FindOpType(firstOperator, '\0');
                        token = new Token(Character.toString(chr), opType);                        
                        state = TokenizeState.OPERATOR;
                    }                    
                    else if (Character.isDigit(chr))
                    {
                        tokenText += chr;
                        state = TokenizeState.NUMBER;
                    }
                    else if (IsParen(chr))
                    {
                        TokenType parenType = FindParenType(chr);
                        tokens.add(new Token(Character.toString(chr), parenType));
                    }
                    else if (IsPunc(chr))
                    {
                        TokenType puncType = FindPuncType(chr);
                        tokens.add(new Token(Character.toString(chr), puncType));
                    }                                                                    
                    else if (chr == '"')
                    {
                        state = TokenizeState.STRING;
                    }
                    else if (chr == '#')
                    {
                        state = TokenizeState.COMMENT;
                    }
                    else if (Character.isLetter(chr))
                    {
                        tokenText += chr;
                        state = TokenizeState.KEYWORD;
                    }                    
                break;
                case OPERATOR:
                        if (IsOp(chr))
                        {                            
                            TokenType opType = FindOpType(firstOperator, chr);
                            token = new Token(Character.toString(firstOperator)
                                              + Character.toString(chr), opType);
                        }
                        else
                        {                                                 
                            tokens.add(token);
                            state = TokenizeState.DEFAULT;                            
                            index--;
                        }                        
                break;
                case NUMBER:
                    if (Character.isDigit(chr))
                    {
                        tokenText += chr;
                    }
                    else
                    {
                        tokens.add(new Token(tokenText, TokenType.NUMBER));
                        tokenText = "";
                        state = TokenizeState.DEFAULT;
                        index--;
                    }
                break;                                
                case STRING:
                    if (chr == '"')
                    {
                        tokens.add(new Token(tokenText, TokenType.STRING));
                        tokenText = "";
                        state = TokenizeState.DEFAULT;
                    }
                    else
                    {
                        tokenText += chr;
                    }
                break;
                case COMMENT:
                    if (chr == '\n') 
                        state = TokenizeState.DEFAULT;
                break;                
                case KEYWORD:
                        if (Character.isLetterOrDigit(chr))
                        {
                            tokenText += chr;
                        }
                        else
                        {
                            TokenType type = FindStatementType(tokenText);
                            tokens.add(new Token(tokenText, type));
                            tokenText = "";
                            state = TokenizeState.DEFAULT;
                            index--;
                        }
                break;                
            }
        }
        return tokens;
    }
}