package togos.noise.v3.program.structure;

import java.util.ArrayList;
import java.util.Collection;

import togos.lang.CompileError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.compiler.ExpressionVectorProgramCompiler;
import togos.noise.v3.program.compiler.UnvectorizableError;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.BoundArgumentList;
import togos.noise.v3.program.runtime.BoundArgumentList.BoundArgument;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.runtime.Function;
import togos.noise.v3.vector.vm.Program.RegisterID;

public class FunctionApplication extends Expression<Object>
{
	final Expression<?> function;
	final ArgumentList argumentList;
	
	public FunctionApplication( Expression<?> function, ArgumentList argumentList, SourceLocation sLoc ) {
	    super(sLoc);
	    this.function = function;
	    this.argumentList = argumentList;
    }
	
    @Override
    public Binding<Object> bind( final Context context ) throws CompileError {
    	@SuppressWarnings("rawtypes")
		final Binding<? extends Function> functionBinding = Binding.cast( function.bind(context), Function.class );
    	final BoundArgumentList boundArgumentList = argumentList.evaluate(context);
		
		return Binding.memoize( new Binding<Object>( sLoc ) {
			@Override public boolean isConstant() throws CompileError {
				if( !functionBinding.isConstant() ) return false;
				for( BoundArgument<?> bArg : boundArgumentList.arguments ) {
					if( !bArg.value.isConstant() ) return false;
				}
	            return true;
            }
			
			@Override public Object getValue() throws Exception {
				return functionBinding.getValue().apply( boundArgumentList ).getValue();
            }
			
			@Override public Class<Object> getValueType() throws CompileError {
				return null;
            }
			
			@Override public Collection<Binding<?>> getDirectDependencies() {
				ArrayList<Binding<?>> dependencies = new ArrayList<Binding<?>>();
				dependencies.add(functionBinding);
				for( BoundArgument<?> bArg : boundArgumentList.arguments ) {
					dependencies.add( bArg.value );
				}
				return dependencies;
	        }
			
			@Override public String toSource() throws CompileError {
				if( functionBinding.isConstant() ) {
					try {
						return functionBinding.getValue().apply( boundArgumentList ).toSource();
					} catch( CompileError e ) {
						throw e;
					} catch( Exception e ) {
						// "Error while flatteing function call for stringification"
						throw new CompileError(e, sLoc);
					}
				} else {
					throw new CompileError("Cannot 'toSource' function application because function is not constant", sLoc);
				}
			}
			
			@Override public RegisterID<?> toVectorProgram(
				ExpressionVectorProgramCompiler compiler
			) throws CompileError {
				if( !functionBinding.isConstant() ) {
					throw new UnvectorizableError(
						"Cannot vectorize function application because the function to be applied is not constant", sLoc
					);
				}
				Function<?> function;
				try {
					function = functionBinding.getValue();
				} catch( Exception e ) {
					throw new CompileError( e, sLoc );
				}
				return function.apply( boundArgumentList ).toVectorProgram(compiler);
			} 
		});
    }
	
	@Override public String toString() {
		return function.toAtomicString() + "(" + argumentList.toString() + ")";
	}
	@Override public String toAtomicString() {
		return toString();
	}
}
