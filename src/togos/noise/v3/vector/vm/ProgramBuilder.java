package togos.noise.v3.vector.vm;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import togos.noise.v3.vector.vm.Program.Instruction;
import togos.noise.v3.vector.vm.Program.Operator;
import togos.noise.v3.vector.vm.Program.RegisterBankID;
import togos.noise.v3.vector.vm.Program.RegisterID;

public class ProgramBuilder
{
	static final RegisterID<RegisterBankID.None> R_NONE = RegisterID.NONE;
	
	ArrayList<Instruction<? extends RegisterBankID,? extends RegisterBankID,? extends RegisterBankID,? extends RegisterBankID>> initInstructions = new ArrayList<Instruction<? extends RegisterBankID,? extends RegisterBankID,? extends RegisterBankID,? extends RegisterBankID>>();
	ArrayList<Instruction<? extends RegisterBankID,? extends RegisterBankID,? extends RegisterBankID,? extends RegisterBankID>> runInstructions = new ArrayList<Instruction<? extends RegisterBankID,? extends RegisterBankID,? extends RegisterBankID,? extends RegisterBankID>>();
	/** Maps constant value to its register ID */
	TreeMap<Integer,RegisterID<RegisterBankID.IConst>> intConstRegisters = new TreeMap<Integer,RegisterID<RegisterBankID.IConst>>();
	TreeMap<Double,RegisterID<RegisterBankID.DConst>> doubleConstRegisters = new TreeMap<Double,RegisterID<RegisterBankID.DConst>>();
	/** Maps constant register ID => double variable ID */
	TreeMap<RegisterID<RegisterBankID.IConst>,RegisterID<RegisterBankID.IVar>> intConstVars = new TreeMap<RegisterID<RegisterBankID.IConst>,RegisterID<RegisterBankID.IVar>>();
	TreeMap<RegisterID<RegisterBankID.DConst>,RegisterID<RegisterBankID.DVar>> doubleConstVars = new TreeMap<RegisterID<RegisterBankID.DConst>,RegisterID<RegisterBankID.DVar>>();
	public short nextIntVar     = 0;
	public short nextDoubleVar  = 0;
	public short nextBooleanVar = 0;
	public short nextConstant   = 0;
	
	public RegisterID<RegisterBankID.DVar> newDVar() {
		return RegisterID.create( RegisterBankID.DVar.INSTANCE, nextDoubleVar++ );
	}
	public RegisterID<RegisterBankID.BVar> newBVar() {
		return RegisterID.create( RegisterBankID.BVar.INSTANCE, nextBooleanVar++ );
	}
	
	public RegisterID<RegisterBankID.IConst> getConstant( int v ) {
		RegisterID<RegisterBankID.IConst> c = intConstRegisters.get(v);
		if( c != null ) return c;
		
		c = RegisterID.create( RegisterBankID.IConst.INSTANCE, nextConstant++);
		intConstRegisters.put(v, c);
		return c;
	}
	
	public RegisterID<RegisterBankID.DConst> getConstant( double v ) {
		RegisterID<RegisterBankID.DConst> c = doubleConstRegisters.get(v);
		if( c != null ) return c;
		
		c = RegisterID.create( RegisterBankID.DConst.INSTANCE, nextConstant++);
		doubleConstRegisters.put(v, c);
		return c;
	}
	
	public RegisterID<RegisterBankID.DVar> getVariable( double v ) {
		RegisterID<RegisterBankID.DConst> cReg = getConstant(v);
		
		if( doubleConstVars.containsKey(cReg) ) {
			return doubleConstVars.get(cReg);
		}
		RegisterID<RegisterBankID.DVar> dReg = RegisterID.create( RegisterBankID.DVar.INSTANCE, nextDoubleVar++);
		doubleConstVars.put( cReg, dReg );
		initInstructions.add( new Instruction<RegisterBankID.DVar,RegisterBankID.DConst,RegisterBankID.None,RegisterBankID.None>( Program.LOADCONST, dReg, cReg, R_NONE, R_NONE ));
		return dReg;
	}
	
	public RegisterID<RegisterBankID.BVar> dd_b( Operator<RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.None> op, RegisterID<RegisterBankID.DVar> r1, RegisterID<RegisterBankID.DVar> r2 ) {
		RegisterID<RegisterBankID.BVar> newReg = newBVar();
		runInstructions.add( new Instruction<RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.None>(op, newReg, r1, r2, R_NONE) );
		return newReg;
	}
	public RegisterID<RegisterBankID.DVar> dd_d( Operator<RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.None> op, RegisterID<RegisterBankID.DVar> r1, RegisterID<RegisterBankID.DVar> r2 ) {
		RegisterID<RegisterBankID.DVar> newReg = newDVar();
		runInstructions.add( new Instruction<RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.None>(op, newReg, r1, r2, R_NONE) );
		return newReg;
	}
	public RegisterID<RegisterBankID.DVar> bdd_d( Operator<RegisterBankID.DVar,RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar>  op, RegisterID<RegisterBankID.BVar> r1, RegisterID<RegisterBankID.DVar> r2, RegisterID<RegisterBankID.DVar> r3 ) {
		RegisterID<RegisterBankID.DVar> newReg = newDVar();
		runInstructions.add( new Instruction<RegisterBankID.DVar,RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar>(op, newReg, r1, r2, r3) );
		return newReg;
	}
	
	@SuppressWarnings("unchecked")
	public Program toProgram() {
		int[] intConstValues = new int[this.intConstRegisters.size()];
		for( Map.Entry<Integer,RegisterID<RegisterBankID.IConst>> ce : this.intConstRegisters.entrySet() ) {
			assert ce.getValue().number >= 0 && ce.getValue().number < intConstValues.length;
			intConstValues[ce.getValue().number] = ce.getKey();
		}
		
		double[] doubleConstValues = new double[this.doubleConstRegisters.size()];
		for( Map.Entry<Double,RegisterID<RegisterBankID.DConst>> ce : this.doubleConstRegisters.entrySet() ) {
			assert ce.getValue().number >= 0 && ce.getValue().number < doubleConstValues.length;
			doubleConstValues[ce.getValue().number] = ce.getKey();
		}
		
		return new Program(
			initInstructions.toArray(new Instruction[initInstructions.size()]),
			runInstructions.toArray(new Instruction[runInstructions.size()]),
			intConstValues, doubleConstValues, nextBooleanVar, nextIntVar, nextDoubleVar
		);
	}
}
