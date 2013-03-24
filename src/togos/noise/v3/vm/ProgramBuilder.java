package togos.noise.v3.vm;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import togos.noise.v3.vm.Program.Instruction;
import togos.noise.v3.vm.Program.Operator;
import togos.noise.v3.vm.Program.RT;
import togos.noise.v3.vm.Program.RegisterID;

public class ProgramBuilder
{
	static final RegisterID<RT.None> R_NONE = RegisterID.NONE;
	
	ArrayList<Instruction<? extends RT,? extends RT,? extends RT,? extends RT>> initInstructions = new ArrayList<Instruction<? extends RT,? extends RT,? extends RT,? extends RT>>();
	ArrayList<Instruction<? extends RT,? extends RT,? extends RT,? extends RT>> runInstructions = new ArrayList<Instruction<? extends RT,? extends RT,? extends RT,? extends RT>>();
	/** Maps constant value to its register ID */
	TreeMap<Double,RegisterID<RT.DConst>> constants = new TreeMap<Double,RegisterID<RT.DConst>>();
	/** Maps constant register ID => double variable ID */
	TreeMap<RegisterID<RT.DConst>,RegisterID<RT.DVar>> constantVariables = new TreeMap<RegisterID<RT.DConst>,RegisterID<RT.DVar>>();
	public short nextDoubleVar  = 0;
	public short nextBooleanVar = 0;
	public short nextConstant   = 0;
	
	public RegisterID<RT.DVar> newDVar() {
		return new RegisterID<RT.DVar>( nextDoubleVar++ );
	}
	public RegisterID<RT.BVar> newBVar() {
		return new RegisterID<RT.BVar>( nextBooleanVar++ );
	}
	
	public RegisterID<RT.DConst> getConstant( double v ) {
		RegisterID<RT.DConst> c = constants.get(v);
		if( c != null ) return c;
		
		c = new RegisterID<RT.DConst>(nextConstant++);
		constants.put(v, c);
		return c;
	}
	
	public RegisterID<RT.DVar> getVariable( double v ) {
		RegisterID<RT.DConst> cReg = getConstant(v);
		
		if( constantVariables.containsKey(cReg) ) {
			return constantVariables.get(cReg);
		}
		RegisterID<RT.DVar> dReg = new RegisterID<RT.DVar>(nextDoubleVar++);
		constantVariables.put( cReg, dReg );
		initInstructions.add( new Instruction<RT.DVar,RT.DConst,RT.None,RT.None>( Program.LOADCONST, dReg, cReg, R_NONE, R_NONE ));
		return dReg;
	}
	
	public RegisterID<RT.BVar> dd_b( Operator<RT.BVar,RT.DVar,RT.DVar,RT.None> op, RegisterID<RT.DVar> r1, RegisterID<RT.DVar> r2 ) {
		RegisterID<RT.BVar> newReg = newBVar();
		runInstructions.add( new Instruction<RT.BVar,RT.DVar,RT.DVar,RT.None>(op, newReg, r1, r2, R_NONE) );
		return newReg;
	}
	public RegisterID<RT.DVar> dd_d( Operator<RT.DVar,RT.DVar,RT.DVar,RT.None> op, RegisterID<RT.DVar> r1, RegisterID<RT.DVar> r2 ) {
		RegisterID<RT.DVar> newReg = newDVar();
		runInstructions.add( new Instruction<RT.DVar,RT.DVar,RT.DVar,RT.None>(op, newReg, r1, r2, R_NONE) );
		return newReg;
	}
	public RegisterID<RT.DVar> bdd_d( Operator<RT.DVar,RT.BVar,RT.DVar,RT.DVar>  op, RegisterID<RT.BVar> r1, RegisterID<RT.DVar> r2, RegisterID<RT.DVar> r3 ) {
		RegisterID<RT.DVar> newReg = newDVar();
		runInstructions.add( new Instruction<RT.DVar,RT.BVar,RT.DVar,RT.DVar>(op, newReg, r1, r2, r3) );
		return newReg;
	}
	
	@SuppressWarnings("unchecked")
	public Program toProgram() {
		double[] constants = new double[this.constants.size()];
		for( Map.Entry<Double,RegisterID<RT.DConst>> ce : this.constants.entrySet() ) {
			assert ce.getValue().number >= 0 && ce.getValue().number < constants.length;
			constants[ce.getValue().number] = ce.getKey();
		}
		
		return new Program(
			initInstructions.toArray(new Instruction[initInstructions.size()]),
			runInstructions.toArray(new Instruction[runInstructions.size()]),
			constants, nextBooleanVar, nextDoubleVar
		);
	}
}
