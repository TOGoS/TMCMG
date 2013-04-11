package togos.noise.v3.program.compiler;

import java.util.HashMap;
import java.util.Map;

import togos.lang.CompileError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.vector.vm.Program.RegisterID;
import togos.noise.v3.vector.vm.ProgramBuilder;

public class ExpressionVectorProgramCompiler
{
	static final class TypeTranslationKey {
		final String expressionSource;
		final Class<?> targetType;
		public TypeTranslationKey( String expressionSource, Class<?> targetType ) {
			assert expressionSource != null;
			assert targetType != null;
			this.expressionSource = expressionSource;
			this.targetType = targetType;
		}
		
		@Override public int hashCode() {
			return expressionSource.hashCode() + targetType.hashCode();
		}
		
		public boolean equals(TypeTranslationKey obj) {
			return
				expressionSource.equals( obj.expressionSource ) &&
				targetType.equals( obj.targetType );
		}
		
		@Override public boolean equals(Object obj) {
			return obj instanceof TypeTranslationKey && equals((TypeTranslationKey)obj); 
		}
	}
	
	public ProgramBuilder pb = new ProgramBuilder();
	
	protected Map<String, RegisterID<?>> variableRegisters = new HashMap<String, RegisterID<?>>();
	protected Map<String, RegisterID<?>> expressionResultRegisters = new HashMap<String, RegisterID<?>>();
	protected Map<TypeTranslationKey, RegisterID<?>> translatedExpressionResultRegisters = new HashMap<TypeTranslationKey, RegisterID<?>>(); 
	
	protected RegisterID<?> createVariableRegister( Class<?> type ) {
		if( type == Double.class ) {
			return pb.newDVar();
		} else if( type == Integer.class ) {
			return pb.newIVar();
		} else if( type == Boolean.class ) {
			return pb.newIVar();
		} else {
			throw new RuntimeException( "Cannot create vector register for type: '"+type.getName()+"'");
		}
	}
	
	public RegisterID<?> declareVariable( String name, Class<?> type ) {
		if( variableRegisters.containsKey(name) ) {
			throw new RuntimeException( "Cannot redeclare variable: '"+name+"'" );
		}
		RegisterID<?> reg = createVariableRegister( type );
		variableRegisters.put(name, reg);
		return reg;
	}
	
	public RegisterID<?> getVariableRegister(String variableId) {
		RegisterID<?> reg = variableRegisters.get(variableId);
		if( reg == null ) throw new RuntimeException("Undefined variable '"+variableId+"'");
		return reg;
	}
	
	public RegisterID<?> compile( Binding<?> b ) throws CompileError {
		String key = b.toSource();
		RegisterID<?> reg = expressionResultRegisters.get(key);
		if( reg == null ) {
			expressionResultRegisters.put(key, reg = b.toVectorProgram(this));
		}
		assert reg != null;
		return reg;
	}
	
	public RegisterID<?> compile( Binding<?> b, Class<?> targetType ) throws CompileError {
		TypeTranslationKey key = new TypeTranslationKey( b.toSource(), targetType);
		RegisterID<?> reg = translatedExpressionResultRegisters.get(key);
		if( reg != null ) return reg;
		
		reg = pb.translate( compile( b ), targetType, b.sLoc );
		translatedExpressionResultRegisters.put(key, reg);
		return reg;
	}
	
	public RegisterID<?> compileConstant( Object value, SourceLocation sLoc ) throws CompileError {
		if( value instanceof Number && ((Number)value).doubleValue() == ((Number)value).intValue() ) {
			return pb.getConstant( ((Number)value).intValue() );
		} else if( value instanceof Double || value instanceof Float ) {
			return pb.getConstant( ((Number)value).doubleValue() );
		} else {
			throw new UnvectorizableError("Cannot compile constant of class "+value.getClass()+" to vector program", sLoc);
		}
	}
}
