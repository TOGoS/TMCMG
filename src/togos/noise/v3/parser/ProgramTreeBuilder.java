package togos.noise.v3.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import togos.lang.ParseError;
import togos.lang.SourceLocation;
import togos.noise.v3.parser.ast.ASTNode;
import togos.noise.v3.parser.ast.InfixNode;
import togos.noise.v3.parser.ast.ParenApplicationNode;
import togos.noise.v3.parser.ast.TextNode;
import togos.noise.v3.parser.ast.VoidNode;
import togos.noise.v3.program.structure.ArgumentList;
import togos.noise.v3.program.structure.Block;
import togos.noise.v3.program.structure.Constant;
import togos.noise.v3.program.structure.Expression;
import togos.noise.v3.program.structure.FunctionApplication;
import togos.noise.v3.program.structure.FunctionDefinition;
import togos.noise.v3.program.structure.ParameterList;
import togos.noise.v3.program.structure.SymbolReference;

public class ProgramTreeBuilder
{
	protected void flatten(ASTNode n, String operator, List<ASTNode> dest) {
		if( n instanceof VoidNode ) {
			// To flatten to an empty list is the purpose of void nodes.
			// Therefore do nothing!
		} else if( n instanceof InfixNode && operator.equals(((InfixNode)n).operator) ) {
			flatten( (InfixNode)n, dest );
		} else {
			dest.add(n);
		}
	}
	
	protected List<ASTNode> flatten(ASTNode n, String operator) {
		ArrayList<ASTNode> l = new ArrayList<ASTNode>();
		flatten( n, operator, l );
		return l;
	}
	
	protected void flatten(InfixNode n, List<ASTNode> dest) {
		flatten( n.n1, n.operator, dest );
		flatten( n.n2, n.operator, dest );
	}
	
	protected List<ASTNode> flatten( InfixNode n ) {
		ArrayList<ASTNode> l = new ArrayList<ASTNode>();
		flatten( n, l );
		return l;
	}
	
	protected ArgumentList parseArgumentList( ASTNode listNode, SourceLocation callLoc ) throws ParseError {
		ArgumentList argList = new ArgumentList( callLoc, listNode );
		for( ASTNode argNode : flatten( listNode, "," ) ) {
			if( argNode instanceof InfixNode && "@".equals(((InfixNode)argNode).operator) ) {
				InfixNode namedArgNode = (InfixNode)argNode;
				if( !(namedArgNode.n1 instanceof TextNode) ) {
					throw new ParseError( "Named argument key must be a symbol, but got a "+namedArgNode.n1.getClass(), namedArgNode.n1);
				}
				argList.add( ((TextNode)namedArgNode.n1).text, parseExpression(namedArgNode.n2), argNode );
			} else {
				argList.add( parseExpression(argNode) );
			}
		}
	    return argList;
    }
	
	protected ParameterList parseParameterList( ASTNode listNode ) throws ParseError {
		ParameterList paramList = new ParameterList( listNode );
		for( ASTNode argNode : flatten( listNode, "," ) ) {
			String name = null;
			boolean slurpy;
			Expression<?> defaultValue;
			if( argNode instanceof InfixNode && "@".equals(((InfixNode)argNode).operator) ) {
				InfixNode namedArgNode = (InfixNode)argNode;
				if( !(namedArgNode.n1 instanceof TextNode) ) {
					throw new ParseError( "Named argument key must be a symbol, but got a "+namedArgNode.n1.getClass(), namedArgNode.n1);
				}
				defaultValue = parseExpression(namedArgNode.n2);
			} else if( argNode instanceof TextNode ) {
				name = ((TextNode)argNode).text;
				defaultValue = null;
			} else {
				throw new ParseError( "Parameter specification must be a symbol or a symbol @ default-value.  Got a "+argNode.getClass(), argNode);
			}
			
			if( name.endsWith("...") ) {
				name = name.substring(0, name.length()-3);
				slurpy = true;
			} else {
				slurpy = false;
			}
			
			if( slurpy && defaultValue != null ) {
				throw new ParseError("Can't provide default values for slurpy parameters", defaultValue.sLoc);
			}
			
			paramList.add( name, slurpy, defaultValue, argNode );
		}
	    return paramList;
    }
	
	public static final class Definition {
		public final String name;
		public final ASTNode value;
		public Definition( String name, ASTNode value ) {
			this.name = name;
			this.value = value;
		}
	}
	
	public Definition parseDefinition( InfixNode defOp ) throws ParseError {
		assert "=".equals(defOp.operator);
		
		String defName;
		ASTNode defValue;
		if( defOp.n1 instanceof TextNode ) {
			defName = ((TextNode)defOp.n1).text;
			defValue = defOp.n2;
		} else if( defOp.n1 instanceof ParenApplicationNode ) {
			ASTNode defFunNameNode = ((ParenApplicationNode)defOp.n1).function;
			if( !(defFunNameNode instanceof TextNode) ) {
				throw new ParseError("Defined function name must be a symbol", defFunNameNode);
			}
			defName = ((TextNode)defFunNameNode).text;
			defValue = new InfixNode("->", ((ParenApplicationNode)defOp.n1).argumentList, defOp.n2, defOp);
		} else {
			throw new ParseError("Invalid lvalue for definition: "+defOp.n1.getClass(), defOp.n1);
		}
		return new Definition( defName, defValue );
	}
	
	protected Block<Object> parseBlock( InfixNode ast ) throws ParseError {
		assert ";".equals(ast.operator);
		HashMap<String,Expression<? extends Object>> definitions = new HashMap<String,Expression<?>>();
		ASTNode blockValueNode = null;
		List<ASTNode> blockParts = flatten(ast);
		for( ASTNode bp : blockParts ) {
			if( bp instanceof InfixNode && "=".equals(((InfixNode)bp).operator) ) {
				Definition def = parseDefinition( (InfixNode)bp );
				definitions.put(def.name, parseExpression(def.value));
			} else {
				if( blockValueNode != null ) {
					throw new ParseError("More than one value defined for block", bp);
				}
				blockValueNode = bp;
			}
		}
		if( blockValueNode == null ) {
			throw new ParseError("Block has no value", ast);
		}
		return new Block<Object>( definitions, parseExpression(blockValueNode), ast );
	}
	
	protected static final Pattern hexIntegerPattern = Pattern.compile("([+-])?0x([\\da-fA-F]+)");
	protected static final Pattern binIntegerPattern = Pattern.compile("([+-])?0b([10]+)");
	protected static final Pattern integerPattern = Pattern.compile("[+-]?(\\d+)");
	protected static final Pattern numberPattern = Pattern.compile("[+-]?(\\d*\\.\\d+)");
	
	private static int sign( String s ) {
		if( s == null || s.length() == 0 ) return 1;
		if( "-".equals(s) ) return -1;
		if( "+".equals(s) ) return +1;
		throw new RuntimeException("Invalid sign: '"+s+"'" );
	}
	
	private static Expression<?> parseSymbol( TextNode ast ) {
		Matcher m;
		if( integerPattern.matcher(ast.text).matches() ) {
			return Constant.forValue( Long.valueOf(ast.text), Long.class, ast );
		} else if( numberPattern.matcher(ast.text).matches() ) {
			return Constant.forValue( Double.valueOf(ast.text), Double.class, ast );
		} else if( (m = hexIntegerPattern.matcher(ast.text)).matches() ) {
			return Constant.forValue( sign(m.group(1)) * Long.valueOf(m.group(2), 16), Long.class, ast );
		} else if( (m = binIntegerPattern.matcher(ast.text)).matches() ) {
			return Constant.forValue( sign(m.group(1)) * Long.valueOf(m.group(2), 2), Long.class, ast );
		} else {
			return new SymbolReference( ast.text, ast );
		}
    }
	
	public Expression<?> parseExpression( ASTNode ast ) throws ParseError {
		if( ast instanceof InfixNode ) {
			InfixNode opApp = (InfixNode)ast;
			if( ";".equals(opApp.operator) ) {
				return parseBlock(opApp);
			} else if( ",".equals(opApp.operator) ) {
				throw new ParseError("Comma not allowed, here", opApp);
			} else if( "=".equals(opApp.operator) ) {
				throw new ParseError("Definition not allowed, here (hint: you need more semicolons)", opApp);
			} else if( "->".equals(opApp.operator) ) {
				return new FunctionDefinition<Object>(
					parseParameterList(opApp.n1),
					parseExpression(opApp.n2),
					ast
				);
			} else {
				return new FunctionApplication(
					new SymbolReference(opApp.operator, opApp),
					new ArgumentList(parseExpression(opApp.n1), parseExpression(opApp.n2), opApp, opApp),
					opApp
				);
			}
		} else if( ast instanceof ParenApplicationNode ) {
			ParenApplicationNode pan = (ParenApplicationNode)ast;
			return new FunctionApplication(
				parseExpression(pan.function),
				parseArgumentList(pan.argumentList, pan),
				ast
			);
		} else if( ast instanceof TextNode ) {
			switch( ((TextNode)ast).type ) {
			case BAREWORD:
				return parseSymbol( (TextNode)ast );
			case SINGLE_QUOTED: case DOUBLE_QUOTED:
				return new Constant<String>( ((TextNode)ast).text, String.class, ast );
			default:
				throw new RuntimeException("Unrecognised TextNode type: '"+((TextNode)ast).type+"'");
			}
		} else {
			throw new ParseError("Don't know how to parse this "+ast.getClass(), ast);
		}
	}
}
