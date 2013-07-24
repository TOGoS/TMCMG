package togos.noise.v3.vector.vm;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import togos.lang.SourceLocation;
import togos.noise.v3.program.compiler.UnvectorizableError;
import togos.noise.v3.vector.vm.Program.Instruction;
import togos.noise.v3.vector.vm.Program.Operator;
import togos.noise.v3.vector.vm.Program.RegisterBankID;
import togos.noise.v3.vector.vm.Program.RegisterBankID.BVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.DConst;
import togos.noise.v3.vector.vm.Program.RegisterBankID.DVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IConst;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.None;
import togos.noise.v3.vector.vm.Program.RegisterID;

public class ProgramBuilder
{
	static final RegisterID<None> R_NONE = RegisterID.NONE;
	
	ArrayList<Instruction<? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>>> initInstructions = new ArrayList<Instruction<? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>>>();
	ArrayList<Instruction<? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>>> runInstructions = new ArrayList<Instruction<? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>>>();
	/** Maps constant value to its register ID */
	TreeMap<Integer,RegisterID<IConst>> intConstRegisters = new TreeMap<Integer,RegisterID<IConst>>();
	TreeMap<Double,RegisterID<DConst>> doubleConstRegisters = new TreeMap<Double,RegisterID<DConst>>();
	/** Maps constant register ID => double variable ID */
	TreeMap<RegisterID<IConst>,RegisterID<IVar>> intConstVars = new TreeMap<RegisterID<IConst>,RegisterID<IVar>>();
	TreeMap<RegisterID<DConst>,RegisterID<DVar>> doubleConstVars = new TreeMap<RegisterID<DConst>,RegisterID<DVar>>();
	TreeMap<RegisterID<IConst>,RegisterID<DVar>> intAsDoubleConstVars = new TreeMap<RegisterID<IConst>,RegisterID<DVar>>();
	public short nextIntegerVector = 0;
	public short nextDoubleVector  = 0;
	public short nextBooleanVector = 0;
	public short nextIntegerConstant = 0;
	public short nextDoubleConstant  = 0;
	
	public RegisterID<BVar> newBVar() {
		return RegisterID.create( BVar.INSTANCE, nextBooleanVector++ );
	}
	public RegisterID<IVar> newIVar() {
		return RegisterID.create( IVar.INSTANCE, nextIntegerVector++ );
	}
	public RegisterID<DVar> newDVar() {
		return RegisterID.create( DVar.INSTANCE, nextDoubleVector++ );
	}
	
	public RegisterID<IConst> getConstant( int v ) {
		RegisterID<IConst> c = intConstRegisters.get(v);
		if( c != null ) return c;
		
		c = RegisterID.create( IConst.INSTANCE, nextIntegerConstant++);
		intConstRegisters.put(v, c);
		return c;
	}
	
	public RegisterID<DConst> getConstant( double v ) {
		RegisterID<DConst> c = doubleConstRegisters.get(v);
		if( c != null ) return c;
		
		c = RegisterID.create( DConst.INSTANCE, nextDoubleConstant++);
		doubleConstRegisters.put(v, c);
		return c;
	}

	public RegisterID<IVar> getVariable( int v ) {
		return getIntegerVariable( getConstant(v) );
	}
	
	public RegisterID<IVar> getIntegerVariable( RegisterID<IConst> cReg ) {
		if( intConstVars.containsKey(cReg) ) {
			return intConstVars.get(cReg);
		}
		RegisterID<IVar> vReg = newIVar();
		intConstVars.put( cReg, vReg );
		initInstructions.add( new Instruction<IVar,IConst,None,None>( Operators.LOAD_INT_CONST, vReg, cReg, R_NONE, R_NONE ));
		return vReg;
	}
	
	public RegisterID<DVar> getVariable( double v ) {
		return getDoubleVariable( getConstant(v) );
	}
	
	public RegisterID<DVar> getDoubleVariable( RegisterID<DConst> cReg ) {
		if( doubleConstVars.containsKey(cReg) ) {
			return doubleConstVars.get(cReg);
		}
		RegisterID<DVar> vReg = newDVar();
		doubleConstVars.put( cReg, vReg );
		initInstructions.add( new Instruction<DVar,DConst,None,None>( Operators.LOAD_DOUBLE_CONST, vReg, cReg, R_NONE, R_NONE ));
		return vReg;
	}
	
	public RegisterID<DVar> getIntAsDoubleVariable( RegisterID<IConst> cReg ) {
		if( intAsDoubleConstVars.containsKey(cReg) ) {
			return intAsDoubleConstVars.get(cReg);
		}
		RegisterID<DVar> vReg = newDVar();
		intAsDoubleConstVars.put( cReg, vReg );
		initInstructions.add( new Instruction<DVar,IConst,None,None>( Operators.LOAD_INT_CONST_AS_DOUBLE, vReg, cReg, R_NONE, R_NONE ));
		return vReg;
	}

	// TODO: This would be an opportunity for further de-duplication.
	// Map (op, source registers) to a destination reguster and cache the result.
	// Would need to create a generic getOpResult( op, r1, r2, r3 ) method.
	public RegisterID<BVar> dd_b( Operator<BVar,DVar,DVar,None> op, RegisterID<DVar> r1, RegisterID<DVar> r2 ) {
		RegisterID<BVar> newReg = newBVar();
		runInstructions.add( new Instruction<BVar,DVar,DVar,None>(op, newReg, r1, r2, R_NONE) );
		return newReg;
	}
	public RegisterID<BVar> bb_b( Operator<BVar,BVar,BVar,None> op, RegisterID<BVar> r1, RegisterID<BVar> r2 ) {
		RegisterID<BVar> newReg = newBVar();
		runInstructions.add( new Instruction<BVar,BVar,BVar,None>(op, newReg, r1, r2, R_NONE) );
		return newReg;
	}
	public RegisterID<IVar> ii_i( Operator<IVar,IVar,IVar,None> op, RegisterID<IVar> r1, RegisterID<IVar> r2 ) {
		RegisterID<IVar> newReg = newIVar();
		runInstructions.add( new Instruction<IVar,IVar,IVar,None>(op, newReg, r1, r2, R_NONE) );
		return newReg;
	}
	public RegisterID<DVar> i_d( Operator<DVar,IVar,None,None> op, RegisterID<IVar> r1 ) {
		RegisterID<DVar> newReg = newDVar();
		runInstructions.add( new Instruction<DVar,IVar,None,None>(op, newReg, r1, R_NONE, R_NONE) );
		return newReg;
	}
	public RegisterID<DVar> d_d( Operator<DVar,DVar,None,None> op, RegisterID<DVar> r1 ) {
		RegisterID<DVar> newReg = newDVar();
		runInstructions.add( new Instruction<DVar,DVar,None,None>(op, newReg, r1, R_NONE, R_NONE) );
		return newReg;
	}
	public RegisterID<DVar> dd_d( Operator<DVar,DVar,DVar,None> op, RegisterID<DVar> r1, RegisterID<DVar> r2 ) {
		RegisterID<DVar> newReg = newDVar();
		runInstructions.add( new Instruction<DVar,DVar,DVar,None>(op, newReg, r1, r2, R_NONE) );
		return newReg;
	}
	public RegisterID<DVar> ddd_d( Operator<DVar,DVar,DVar,DVar> op, RegisterID<DVar> r1, RegisterID<DVar> r2, RegisterID<DVar> r3 ) {
		RegisterID<DVar> newReg = newDVar();
		runInstructions.add( new Instruction<DVar,DVar,DVar,DVar>(op, newReg, r1, r2, r3) );
		return newReg;
	}
	public RegisterID<BVar> bbb_b( Operator<BVar,BVar,BVar,BVar>  op, RegisterID<BVar> r1, RegisterID<BVar> r2, RegisterID<BVar> r3 ) {
		RegisterID<BVar> newReg = newBVar();
		runInstructions.add( new Instruction<BVar,BVar,BVar,BVar>(op, newReg, r1, r2, r3) );
		return newReg;
	}
	public RegisterID<IVar> bii_i( Operator<IVar,BVar,IVar,IVar>  op, RegisterID<BVar> r1, RegisterID<IVar> r2, RegisterID<IVar> r3 ) {
		RegisterID<IVar> newReg = newIVar();
		runInstructions.add( new Instruction<IVar,BVar,IVar,IVar>(op, newReg, r1, r2, r3) );
		return newReg;
	}
	public RegisterID<DVar> bdd_d( Operator<DVar,BVar,DVar,DVar>  op, RegisterID<BVar> r1, RegisterID<DVar> r2, RegisterID<DVar> r3 ) {
		RegisterID<DVar> newReg = newDVar();
		runInstructions.add( new Instruction<DVar,BVar,DVar,DVar>(op, newReg, r1, r2, r3) );
		return newReg;
	}
	
	public RegisterID<?> select( RegisterID<BVar> selector, RegisterID<?> onFalse, RegisterID<?> onTrue, SourceLocation sLoc ) throws UnvectorizableError {
		if( onFalse.bankId == onTrue.bankId ) {
			if( onFalse.bankId == IVar.INSTANCE ) {
				return bii_i( Operators.SELECT_INTEGER, selector, (RegisterID<IVar>)onFalse, (RegisterID<IVar>)onTrue );
			} else 	if( onFalse.bankId == DVar.INSTANCE ) {
				return bdd_d( Operators.SELECT_DOUBLE, selector, (RegisterID<DVar>)onFalse, (RegisterID<DVar>)onTrue );
			} else if( onFalse.bankId == BVar.INSTANCE ) {
				return bbb_b( Operators.SELECT_BOOLEAN, selector, (RegisterID<BVar>)onFalse, (RegisterID<BVar>)onTrue );
			} else {
				throw new UnvectorizableError("No selector operator for operand types "+onFalse.bankId.valueType+" and "+onTrue.bankId.valueType, sLoc);
			}
		} else if( Number.class.isAssignableFrom(onFalse.bankId.valueType) && Number.class.isAssignableFrom(onTrue.bankId.valueType) ) {
			return bdd_d( Operators.SELECT_DOUBLE, selector, (RegisterID<DVar>)translate(onFalse, Double.class, sLoc), (RegisterID<DVar>)translate(onTrue, Double.class, sLoc) );
		} else {
			throw new UnvectorizableError("Selection restult registers are not of compatible types", sLoc);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Program toProgram() {
		int[] intConstValues = new int[this.intConstRegisters.size()];
		for( Map.Entry<Integer,RegisterID<IConst>> ce : this.intConstRegisters.entrySet() ) {
			assert ce.getValue().number >= 0 && ce.getValue().number < intConstValues.length;
			intConstValues[ce.getValue().number] = ce.getKey();
		}
		
		double[] doubleConstValues = new double[this.doubleConstRegisters.size()];
		for( Map.Entry<Double,RegisterID<DConst>> ce : this.doubleConstRegisters.entrySet() ) {
			assert ce.getValue().number >= 0 && ce.getValue().number < doubleConstValues.length;
			doubleConstValues[ce.getValue().number] = ce.getKey();
		}
		
		return new Program(
			initInstructions.toArray(new Instruction[initInstructions.size()]),
			runInstructions.toArray(new Instruction[runInstructions.size()]),
			intConstValues, doubleConstValues,
			nextBooleanVector, nextIntegerVector, nextDoubleVector
		);
	}
	
	public RegisterID<?> translate(RegisterID<?> reg, Class<?> targetType, SourceLocation sLoc)
			throws UnvectorizableError
	{
		if( reg.bankId.valueType == targetType ) {
			return reg;
		} else if( reg.bankId.valueType == Integer.class && targetType == Double.class ) {
			if( reg.bankId.isConstant ) {
				return getIntAsDoubleVariable( (RegisterID<IConst>)reg );
			} else {
				return i_d( Operators.INT_TO_DOUBLE, (RegisterID<IVar>)reg );
			}
		} else {
			throw new UnvectorizableError("Cannot write vector program to translate from "+reg.bankId.valueType+" to "+targetType, sLoc);
		}
	}
}
