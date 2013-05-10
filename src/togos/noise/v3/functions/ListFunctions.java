package togos.noise.v3.functions;

import java.util.Collection;

import togos.lang.BaseSourceLocation;
import togos.lang.CompileError;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.BoundArgumentList;
import togos.noise.v3.program.runtime.BoundArgumentList.BoundArgument;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.runtime.Function;
import togos.noise.v3.program.runtime.LinkedListNode;

public class ListFunctions
{
	static final BaseSourceLocation BUILTIN_LOC = new BaseSourceLocation( ListFunctions.class.getName()+".java", 0, 0);
	public static final Context CONTEXT = new Context();
	
	public static final Function<LinkedListNode<Object>> CREATE_LIST = new Function<LinkedListNode<Object>>() {
		@Override public Binding<LinkedListNode<Object>> apply(BoundArgumentList args) throws CompileError {
			LinkedListNode<Binding<?>> bindings = LinkedListNode.empty();
			for( BoundArgument<?> arg : args.arguments ) {
				if( arg.name.length() > 0 ) {
					throw new CompileError("'list' takes no named arguments; got '"+arg.name+"'", arg.sLoc);
				} else {
					bindings = new LinkedListNode<Binding<?>>( arg.value, bindings );
				}
			}
			
			final LinkedListNode<Binding<?>> _bindings = bindings;
			return Binding.memoize(new Binding<LinkedListNode<Object>>(args.callLocation) {
				@SuppressWarnings("unchecked")
				@Override public Class<LinkedListNode<Object>> getValueType() {
					return (Class<LinkedListNode<Object>>)(Class<?>)LinkedListNode.class;
				}
				
				@Override public LinkedListNode<Object> getValue() throws Exception {
					LinkedListNode<Object> list = LinkedListNode.empty();
					for( Binding<?> b : _bindings ) {
						list = new LinkedListNode<Object>( b.getValue(), list );
					}
					return list;
				}
				
				@Override public Collection<Binding<?>> getDirectDependencies() {
	                return _bindings.toList();
                }
				
				@Override public boolean isConstant() throws CompileError {
					for( Binding<?> b : _bindings ) {
						if( !b.isConstant() ) return false;
					}
					return true;
				}
				
				@Override public String toSource() throws CompileError {
					String items = "";
					for( Binding<?> b : _bindings ) {
						if( items.length() > 0 ) items += ", ";
						items += b.toSource();
					}
					return "list(" + items + ")";
				}
			});
		}
	};
	
	static {
		CONTEXT.put("list", Binding.forValue(CREATE_LIST, BUILTIN_LOC) );
	}
}
