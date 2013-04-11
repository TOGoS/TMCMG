package togos.noise.v3.program.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import togos.lang.CompileError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.compiler.ExpressionVectorProgramCompiler;
import togos.noise.v3.program.runtime.BoundArgumentList.BoundArgument;
import togos.noise.v3.program.structure.FunctionDefinition;
import togos.noise.v3.program.structure.ParameterList.Parameter;
import togos.noise.v3.vector.vm.Program.RegisterID;

public class Closure<V> implements Function<V>
{
	final FunctionDefinition<V> function;
	final Context context;
	
	public Closure( FunctionDefinition<V> function, Context context ) {
		this.function = function;
		this.context = context;
	}
	
	static class ListBinding<V> extends Binding<LinkedListNode<V>> {
		public final ArrayList<Binding<? extends V>> valueBindings = new ArrayList<Binding<? extends V>>();
		
		public ListBinding( SourceLocation sLoc ) {
			super(sLoc);
		}
		
		@Override public boolean isConstant() throws CompileError {
			for( Binding<?> b : valueBindings ) if(!b.isConstant()) return false;
	        return true;
        }
		
		@Override public LinkedListNode<V> getValue() throws Exception {
			LinkedListNode<V> n = null;
			for( int i = valueBindings.size()-1; i >= 0; --i ) {
				n = new LinkedListNode<V>( valueBindings.get(i).getValue(), n );
			}
	        return n;
        }
		
		public void add( Binding<? extends V> value ) {
			valueBindings.add(value);
        }
		
		@Override
		public RegisterID<?> toVectorProgram(
			ExpressionVectorProgramCompiler compiler
		) throws CompileError {
			throw new CompileError("List binding cannot be converted to a vector program", sLoc);
		}
		
    	@SuppressWarnings("unchecked")
        @Override
        public Class<? extends LinkedListNode<V>> getValueType() throws CompileError {
			return (Class<? extends LinkedListNode<V>>)LinkedListNode.class;
        }
    	
		@Override
		public String toSource() throws CompileError {
			String s = "";
			for( int i=0; i<valueBindings.size(); ++i ) {
				if( i > 0 ) s += ", ";
				s += valueBindings.get(i).toSource();
			}
			return "list("+s+")";
		}
	}
	
	public Binding<? extends V> apply( BoundArgumentList args ) throws CompileError {
		Context newContext = new Context(context);
		
		Map<String,Parameter<?>> parameterMap = function.parameterList.getParameterMap();
		List<Parameter<?>> parameters = function.parameterList.parameters;
		Map<String,Binding<?>> newValues = new HashMap<String,Binding<?>>();
		
		for( Parameter<?> p : parameters ) {
			if( p.slurpy ) {
				newValues.put( p.name, new ListBinding<Object>(p.sLoc) );
			}
		}
		
		int i = 0;
		boolean namedArgumentsEncountered = false;
		// TODO: Deal with positional slurpy arguments
		for( BoundArgument<?> arg : args.arguments ) {
			String name;
			if( "".equals(arg.name) ) {
				if( namedArgumentsEncountered ) {
					throw new CompileError( "Cannot give positional arguments after named ones", arg.sLoc );
				}
				if( i >= parameters.size() ) {
					throw new CompileError( "Too many positional arguments", arg.sLoc );
				}
				name = parameters.get(i).name;
				++i;
			} else {
				namedArgumentsEncountered = true;
				name = arg.name;
			}
			if( !parameterMap.containsKey(name) ) {
				throw new CompileError("Undefined parameter '"+name+"'", arg.sLoc);
			}
			if( parameterMap.get(name).slurpy ) {
				@SuppressWarnings("unchecked")
                ListBinding<Object> listBinding = (ListBinding<Object>)newValues.get(name);
				listBinding.add(arg.value);
			} else {
				newValues.put( name, arg.value );
			}
		}
		
		for( Parameter<?> p : parameters ) {
			if( !newValues.containsKey(p.name) ) {
				if( p.defaultValue != null ) {
					newValues.put( p.name, p.defaultValue.bind(context) );
				} else {
					throw new CompileError("Parameter '"+p.name+"' is unbound", args.argListLocation);
				}
			}
		}
		
		newContext.putAll(newValues);
		return function.definition.bind(newContext);
	}
	
	public String toString() {
		return "Closure of "+function;
	}
}
