package togos.noise.v3.vector.vm;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	public static int totalProgramLength = 0; // Used for debugging 
	
	ArrayList<Instruction<? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>>> initInstructions = new ArrayList<Instruction<? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>>>();
	ArrayList<Instruction<? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>>> runInstructions = new ArrayList<Instruction<? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>,? extends RegisterBankID<?>>>();
	/** Maps constant value to its register ID */
	TreeMap<Integer,RegisterID<IConst>> intConstRegisters = new TreeMap<Integer,RegisterID<IConst>>();
	TreeMap<Double,RegisterID<DConst>> doubleConstRegisters = new TreeMap<Double,RegisterID<DConst>>();
	/** Maps constant register ID => double variable ID */
	TreeMap<RegisterID<IConst>,RegisterID<IVar>> intConstVars = new TreeMap<RegisterID<IConst>,RegisterID<IVar>>();
	TreeMap<RegisterID<DConst>,RegisterID<DVar>> doubleConstVars = new TreeMap<RegisterID<DConst>,RegisterID<DVar>>();
	TreeMap<RegisterID<IConst>,RegisterID<DVar>> intAsDoubleConstVars = new TreeMap<RegisterID<IConst>,RegisterID<DVar>>();
	TreeMap<RegisterID<IVar>,RegisterID<DVar>> intAsDoubleVars = new TreeMap<RegisterID<IVar>,RegisterID<DVar>>();
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
	
	@SuppressWarnings("unchecked")
    public <BankID extends RegisterBankID<?>> RegisterID<BankID> newVar( BankID bank ) {
		if( RegisterBankID.DVar.INSTANCE.equals(bank) ) {
			return (RegisterID<BankID>) newDVar();
		} else if( RegisterBankID.IVar.INSTANCE.equals(bank) ) {
			return (RegisterID<BankID>) newIVar();
		} else if( RegisterBankID.BVar.INSTANCE.equals(bank) ) {
			return (RegisterID<BankID>) newBVar();
		} else {
			// In theory this could also handle constant registers
			throw new RuntimeException("Don't know how to allocate new register for bank "+bank);
		}
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
	
	protected static final class OpInstance {
		public final Operator<?,?,?,?> op;
		public final RegisterID<?> r1, r2, r3;
		public OpInstance( Operator<?,?,?,?> op, RegisterID<?> r1, RegisterID<?> r2, RegisterID<?> r3 ) {
			this.op = op; this.r1 = r1; this.r2 = r2; this.r3 = r3;
		}
		@Override public boolean equals( Object oth ) {
			if( oth instanceof OpInstance ) {
				OpInstance oi = (OpInstance)oth;
				return op.equals(oi.op) && r1.equals(oi.r1) && r2.equals(oi.r2) && r3.equals(oi.r3);
			}
			return false;
		}
		@Override public int hashCode() {
			return op.hashCode() | (r1.hashCode() << 4) | (r2.hashCode() << 8) | (r3.hashCode() << 12);  
		}
	}
	
	protected HashMap<OpInstance, RegisterID<?>> opResults = new HashMap<OpInstance, RegisterID<?>>();
	
	protected int opResultCacheHits, opResultCacheMisses;
	
    public
		<DestRT extends RegisterBankID<?>, V1RT extends RegisterBankID<?>, V2RT extends RegisterBankID<?>, V3RT extends RegisterBankID<?>>
		RegisterID<DestRT> getOpResult( DestRT resultBank, Operator<DestRT,V1RT,V2RT,V3RT> op, RegisterID<V1RT> r1, RegisterID<V2RT> r2, RegisterID<V3RT> r3 )
	{
		OpInstance oi = new OpInstance( op, r1, r2, r3 );
		
		@SuppressWarnings("unchecked")
		RegisterID<DestRT> result = (RegisterID<DestRT>) opResults.get(oi);
		if( result != null && !resultBank.equals(result.bankId) ) {
			throw new RuntimeException("Result bank from cached op instance result does not match expected: "+result.bankId +" != "+resultBank);
		}
		
		if( result == null ) {
			result = newVar( resultBank );
			runInstructions.add( Instruction.create(op, result, r1, r2, r3) );
			
			opResults.put(oi, result);
			++opResultCacheMisses;
		} else {
			++opResultCacheHits;
		}
		return result;
	}
	
	public RegisterID<BVar> dd_b( Operator<BVar,DVar,DVar,None> op, RegisterID<DVar> r1, RegisterID<DVar> r2 ) {
		return getOpResult( BVar.INSTANCE, op, r1, r2, R_NONE );
	}
	public RegisterID<BVar> bb_b( Operator<BVar,BVar,BVar,None> op, RegisterID<BVar> r1, RegisterID<BVar> r2 ) {
		return getOpResult( BVar.INSTANCE, op, r1, r2, R_NONE );
	}
	public RegisterID<IVar> ii_i( Operator<IVar,IVar,IVar,None> op, RegisterID<IVar> r1, RegisterID<IVar> r2 ) {
		return getOpResult( IVar.INSTANCE, op, r1, r2, R_NONE );
	}
	public RegisterID<DVar> i_d( Operator<DVar,IVar,None,None> op, RegisterID<IVar> r1 ) {
		return getOpResult( DVar.INSTANCE, op, r1, R_NONE, R_NONE );
	}
	public RegisterID<DVar> d_d( Operator<DVar,DVar,None,None> op, RegisterID<DVar> r1 ) {
		return getOpResult( DVar.INSTANCE, op, r1, R_NONE, R_NONE );
	}
	public RegisterID<DVar> dd_d( Operator<DVar,DVar,DVar,None> op, RegisterID<DVar> r1, RegisterID<DVar> r2 ) {
		return getOpResult( DVar.INSTANCE, op, r1, r2, R_NONE );
	}
	public RegisterID<DVar> ddd_d( Operator<DVar,DVar,DVar,DVar> op, RegisterID<DVar> r1, RegisterID<DVar> r2, RegisterID<DVar> r3 ) {
		return getOpResult( DVar.INSTANCE, op, r1, r2, r3 );
	}
	public RegisterID<BVar> bbb_b( Operator<BVar,BVar,BVar,BVar>  op, RegisterID<BVar> r1, RegisterID<BVar> r2, RegisterID<BVar> r3 ) {
		return getOpResult( BVar.INSTANCE, op, r1, r2, r3 );
	}
	public RegisterID<IVar> bii_i( Operator<IVar,BVar,IVar,IVar>  op, RegisterID<BVar> r1, RegisterID<IVar> r2, RegisterID<IVar> r3 ) {
		return getOpResult( IVar.INSTANCE, op, r1, r2, r3 );
	}
	public RegisterID<DVar> bdd_d( Operator<DVar,BVar,DVar,DVar>  op, RegisterID<BVar> r1, RegisterID<DVar> r2, RegisterID<DVar> r3 ) {
		return getOpResult( DVar.INSTANCE, op, r1, r2, r3 );
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
		
		/*
		int programLength = initInstructions.size() + runInstructions.size();
		System.err.println("ProgramBuilder stats");
		System.err.println("  Op result cache");
		System.err.println("    Hits   = "+opResultCacheHits);
		System.err.println("    Misses = "+opResultCacheMisses);
		System.err.println("  Program length = "+programLength+" instructions");
		totalProgramLength += programLength;
		*/
		
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
		}
		
		if( reg.bankId.valueType == Integer.class && targetType == Double.class ) {
			if( reg.bankId.isConstant ) {
				return getIntAsDoubleVariable( reg.castToBank(IConst.INSTANCE) );
			}
			
			RegisterID<IVar> iVar = reg.castToBank(IVar.INSTANCE);
			RegisterID<DVar> dVar = intAsDoubleVars.get(iVar);
			if( dVar == null ) {
				dVar = i_d( Operators.INT_TO_DOUBLE, iVar );
				intAsDoubleVars.put( iVar, dVar);
			}
			return dVar;
		}
		
		throw new UnvectorizableError("Cannot write vector program to translate from "+reg.bankId.valueType+" to "+targetType, sLoc);
	}
}
