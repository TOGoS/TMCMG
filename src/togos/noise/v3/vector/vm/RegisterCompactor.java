package togos.noise.v3.vector.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import togos.noise.v3.vector.vm.Program.Instruction;
import togos.noise.v3.vector.vm.Program.RegisterBankID;
import togos.noise.v3.vector.vm.Program.RegisterBankID.DVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IVar;
import togos.noise.v3.vector.vm.Program.RegisterID;

public class RegisterCompactor
{
	static final class Variable<BankID extends RegisterBankID<?>> {
		public final RegisterID<BankID> register;
		public final int minIndex;
		public final boolean isInput, isOutput;

		public int maxIndex;
		public RegisterID<BankID> newRegister;
		
		public Variable( RegisterID<BankID> register, int lineWritten, boolean isOutput ) {
			this.register = register;
			this.minIndex = lineWritten;
			this.isInput = lineWritten == 0;
			this.isOutput = isOutput;
			if( isOutput ) {
				maxIndex = Integer.MAX_VALUE;
			}
		}
		
		public void readAt( int lineNumber ) {
			if( lineNumber > maxIndex ) maxIndex = lineNumber; 
		}
		
		/**
		 * Return true if this variable and another share a bank
		 * and need to exist at the same time.
		 */
		public boolean couldCollideWith( Variable<?> o ) {
			if( !o.newRegister.bankId.equals(newRegister.bankId) ) return false;
			
			if( maxIndex < o.minIndex ) return false;
			if( minIndex > o.maxIndex ) return false;
			
			return true;
		}
	}
	
	static final class Compaction {
		public final List<Instruction<?,?,?,?>> newInstructions;
		public final Map<RegisterID<?>,RegisterID<?>> inputMap;
		public final Map<RegisterID<?>,RegisterID<?>> outputMap;
		public final int newDVarCount;
		public final int newIVarCount;
		public final int newBVarCount;
		
		public Compaction(
			List<Instruction<?,?,?,?>> newInstructions,
			Map<RegisterID<?>,RegisterID<?>> inputMap,
			Map<RegisterID<?>,RegisterID<?>> outputMap,
			int newDVarCount,
			int newIVarCount,
			int newBVarCount
		) {
			this.newInstructions = newInstructions;
			this.inputMap = inputMap;
			this.outputMap = outputMap;
			this.newDVarCount = newDVarCount;
			this.newIVarCount = newIVarCount;
			this.newBVarCount = newBVarCount;
		}
	}
	
    protected boolean rewritable( RegisterID<?> r ) {
		if( r == RegisterID.NONE ) return false;
		if( r.bankId.isConstant ) return false;
    	return true;
    }
	
	protected <BankID extends RegisterBankID<?>> void processRead( RegisterID<BankID> r, List<Variable<?>> allVars, Map<RegisterID<?>,Variable<?>> currentVars, Set<RegisterID<?>> outputRegisters, int currentLine ) {
		if( !rewritable(r) ) return;
		
		Variable<?> v;
		if( (v = currentVars.get(r)) == null ) {
			v = new Variable<BankID>( r, 0, outputRegisters.contains(r) );
			allVars.add(v);
			currentVars.put( r, v );
		}
		v.readAt(currentLine);
	}
	
	protected List<Variable<?>> extractVariables( List<Instruction<?,?,?,?>> instructions, Set<RegisterID<?>> outputRegisters ) {
		List<Variable<?>> vars = new ArrayList<Variable<?>>();
		Map<RegisterID<?>,Variable<?>> currentVars = new HashMap<RegisterID<?>,Variable<?>>();
		
		int line = 0;
		for( Instruction<?,?,?,?> i : instructions ) {
			if( rewritable(i.dest) ) {
				@SuppressWarnings({"rawtypes","unchecked"})
				Variable<?> destVar = new Variable(i.dest, line, outputRegisters.contains(i.dest));
				currentVars.put( i.dest, destVar );
			}
			
			processRead( i.v1, vars, currentVars, outputRegisters, line );
			processRead( i.v2, vars, currentVars, outputRegisters, line );
			processRead( i.v3, vars, currentVars, outputRegisters, line );
			++line;
		}
		
		return vars;
	}
	
	
    protected <BankID extends RegisterBankID<?>> void reassign( Variable<BankID> v, Collection<Variable<?>> allVariables ) {
		short number = 0;
		boolean spaceFound = false;
		findSpace: while( !spaceFound ) {
			for( Variable<?> v0 : allVariables ) {
				if( v0.newRegister == null ) continue; // Don't care!
				
				if( v0.newRegister.number == number && v.couldCollideWith(v0) ) {
					// Collision found; increment number and start over
					++number;
					continue findSpace;
				}
			}
			
			spaceFound = true;
		}
		
		v.newRegister = new RegisterID<BankID>( v.register.bankId, number );
	}
    
	private <BankID extends RegisterBankID<?>> RegisterID<BankID> remap(RegisterID<BankID> oldReg, Collection<Variable<?>> variables, int programIndex) {
		if( oldReg.bankId.isConstant ) return oldReg;
		
		for( Variable<?> v : variables ) {
			if( v.register.equals(oldReg) && v.minIndex <= programIndex && v.maxIndex >= programIndex ) {
				@SuppressWarnings("unchecked")
				Variable<BankID> c = (Variable<BankID>)v;
				return c.newRegister;
			}
		}
		
		throw new RuntimeException("Couldn't find remapping for "+oldReg+" at position "+programIndex);
	}
	
	protected <
		DestRT extends RegisterBankID<?>,
		V1RT extends RegisterBankID<?>,
		V2RT extends RegisterBankID<?>,
		V3RT extends RegisterBankID<?>
	>
	Instruction<DestRT,V1RT,V2RT,V3RT> rewrite( Instruction<DestRT,V1RT,V2RT,V3RT> i, Collection<Variable<?>> variables, int pi ) {
		return Instruction.create(i.op, remap(i.dest, variables, pi), remap(i.v1, variables, pi), remap(i.v2, variables, pi), remap(i.v3, variables, pi) );
	}
	
    protected List<Instruction<?,?,?,?>> rewrite( List<Instruction<?,?,?,?>> instructions, Collection<Variable<?>> variables ) {
		List<Instruction<?,?,?,?>> compacted = rewrite(instructions, variables);
		int programIndex = 0;
		for( Instruction<?,?,?,?> i : instructions ) {
			compacted.add( rewrite(i, variables, programIndex) );
			++programIndex;
		}
		return compacted;
    }
	
	public Compaction compact(
		List<Instruction<?,?,?,?>> instructions,
		Set<RegisterID<?>> outputRegisters
	) {
		// Make list of all variables (register ID, lifetime)
		// Compact variables by finding lowest clear register for each,
		//   keeping track of original variable -> new register, and highest resulting I and D registers used
		// for variables with lifetime starting at 0, add mapping to inputMap
		// for variables listed in outputRegisters, add mapping to outputMap
		// rewrite instructions based on variable -> new register map
		// create and return Compaction object 
		
		int oldMaxD = -1, oldMaxI = -1, oldMaxB = -1;
		int maxDVar = -1, maxIVar = -1, maxBVar = -1;
		Map<RegisterID<?>,RegisterID<?>> inputMap = new HashMap<RegisterID<?>,RegisterID<?>>();
		Map<RegisterID<?>,RegisterID<?>> outputMap = new HashMap<RegisterID<?>,RegisterID<?>>();
		
		List<Variable<?>> variables = extractVariables(instructions, outputRegisters);
		for( Variable<?> v : variables ) {
			reassign(v, variables);
			
			RegisterID<?> newReg = v.newRegister;
			if( DVar.INSTANCE.equals(v.register.bankId) ) {
				maxDVar = newReg.number > maxDVar ? newReg.number : maxDVar; 
			} else if( IVar.INSTANCE.equals(v.register.bankId) ) {
				maxIVar = newReg.number > maxIVar ? newReg.number : maxIVar; 
			} else {
				// TODO: Need to deal with booleans.
				// Leaving this in for now so I can make sure it's catching that.
				throw new RuntimeException("Ignoring register "+newReg);
			}
			
			if( v.isInput  )  inputMap.put( v.register, v.newRegister );
			if( v.isOutput ) outputMap.put( v.register, v.newRegister );
		}
		
		System.err.println("Compacted "+(oldMaxD+oldMaxI+oldMaxB+3)+" down to "+(maxDVar+maxIVar+maxBVar+3));
		
		return new Compaction(
			rewrite( instructions, variables ),
			inputMap, outputMap,
			maxDVar+1, maxIVar+1, maxBVar+1
		);
	}
	
	public Program compact( Program p, Set<RegisterID<?>> outputRegisters ) {
		List<Instruction<?,?,?,?>> allInstructions = new ArrayList<Instruction<?,?,?,?>>();
		allInstructions.addAll(Arrays.asList(p.initInstructions));
		allInstructions.addAll(Arrays.asList(p.runInstructions));
		Compaction c = compact( allInstructions, outputRegisters );
		return new Program(
			new Instruction[0],
			c.newInstructions.toArray(new Instruction[c.newInstructions.size()]),
			p.integerConstants,
			p.doubleConstants,
			c.newBVarCount,
			c.newDVarCount,
			c.newIVarCount
		);
	}
}
