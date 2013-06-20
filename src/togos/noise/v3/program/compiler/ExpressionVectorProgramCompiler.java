package togos.noise.v3.program.compiler;

import java.util.HashMap;
import java.util.Map;

import togos.lang.CompileError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.vector.vm.Program.RegisterBankID;
import togos.noise.v3.vector.vm.Program.RegisterBankID.BVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.DConst;
import togos.noise.v3.vector.vm.Program.RegisterBankID.DVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IConst;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IVar;
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
	
	@SuppressWarnings("unchecked")
	protected <T extends RegisterBankID<?>> RegisterID<T> createVariableRegister( T bank ) {
		if( bank == BVar.INSTANCE ) {
			return (RegisterID<T>)pb.newBVar();
		} else if( bank == IVar.INSTANCE ) {
			return (RegisterID<T>)pb.newIVar();
		} else if( bank == DVar.INSTANCE ) {
			return (RegisterID<T>)pb.newDVar();
		} else {
			throw new RuntimeException( "Cannot create vector register for bank: '"+bank+"'");
		}
	}
	
	public <T extends RegisterBankID<?>> RegisterID<T> declareVariable( String name, T bank ) {
		if( variableRegisters.containsKey(name) ) {
			throw new RuntimeException( "Cannot redeclare variable: '"+name+"'" );
		}
		RegisterID<T> reg = createVariableRegister( bank );
		variableRegisters.put(name, reg);
		return reg;
	}
	
	public RegisterID<?> getVariableRegister(String variableId) {
		RegisterID<?> reg = variableRegisters.get(variableId);
		if( reg == null ) throw new RuntimeException("Undefined variable '"+variableId+"'");
		return reg;
	}
	
	public RegisterID<?> compileConstant( Object value, SourceLocation sLoc ) throws CompileError {
		if( value instanceof Number && ((Number)value).doubleValue() == ((Number)value).intValue() ) {
			// Treat any constant that fits in an int as an int;
			// it can always be expanded to a double later
			return pb.getConstant( ((Number)value).intValue() );
		} else if( value instanceof Number ) {
			return pb.getConstant( ((Number)value).doubleValue() );
		} else {
			throw new UnvectorizableError("Cannot compile constant of class "+value.getClass()+" to vector program", sLoc);
		}
	}
	
	protected RegisterID<?> _compile( Binding<?> b ) throws CompileError {
		if( b.isConstant() ) {
			try {
				return compileConstant(b.getValue(), b.sLoc);
			} catch( CompileError e ) {
				throw e;
			} catch( Exception e ) {
				throw new CompileError("Error while evaluating supposedly constant expression", b.sLoc);
			}
		} else {
			return b.toVectorProgram(this);
		}
	}
	
	public RegisterID<?> toVector( RegisterID<?> reg ) {
		if( !reg.bankId.isConstant ) return reg;
		
		if( reg.bankId.valueType == Boolean.class ) {
			throw new RuntimeException("Somehow got a constant boolean register!");
		} else if( reg.bankId.valueType == Integer.class ) {
			return pb.getIntegerVariable( (RegisterID<IConst>)reg );
		} else if( reg.bankId.valueType == Double.class ) {
			return pb.getDoubleVariable( (RegisterID<DConst>)reg );
		}
		
		return null;
	}
	
	//// The following will only return vector/variable registers ////
	
	public RegisterID<?> compile( Binding<?> b ) throws CompileError {
		String key = b.getCalculationId();
		RegisterID<?> reg = expressionResultRegisters.get(key);
		if( reg == null ) {
			expressionResultRegisters.put(key, reg = toVector(_compile(b)));
		}
		assert reg != null;
		return reg;
	}
	
	public <T extends RegisterBankID<?>> RegisterID<T> compile( Binding<?> b, T targetRegisterBank ) throws CompileError {
		RegisterID<?> reg = compile( b, targetRegisterBank.valueType );
		if( reg.bankId != targetRegisterBank ) {
			// TODO: Is it really expected or are we being optimistic?
			// maybe this would be the place to force it to be (or except if they're incompatible).
			throw new RuntimeException("Binding compiled to unexpected register bank "+reg.bankId+" (expected "+targetRegisterBank+")");
		} else {
			@SuppressWarnings("unchecked")
			RegisterID<T> res = (RegisterID<T>)reg;
			return res;
		}
	}
	
	public RegisterID<?> compile( Binding<?> b, Class<?> targetType ) throws CompileError {
		TypeTranslationKey key = new TypeTranslationKey( b.getCalculationId(), targetType);
		RegisterID<?> reg = translatedExpressionResultRegisters.get(key);
		if( reg != null ) return reg;
		
		reg = toVector( pb.translate( compile( b ), targetType, b.sLoc ) );
		translatedExpressionResultRegisters.put(key, reg);
		return reg;
	}
}
