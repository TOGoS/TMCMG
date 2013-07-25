package togos.noise.v3.vector.vm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import togos.noise.v3.vector.vm.Program.Instruction;
import togos.noise.v3.vector.vm.Program.RegisterID;

public class RegisterCompactor
{
	static final class Variable {
		public final RegisterID<?> register;
		public final int minIndex;
		public final boolean isInput, isOutput;

		public int maxIndex;
		public RegisterID<?> newRegister;
		
		public Variable( RegisterID<?> register, int lineWritten, boolean isOutput ) {
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
		public boolean couldCollideWith( Variable o ) {
			if( !o.newRegister.bankId.equals(newRegister.bankId) ) return false;
			
			if( maxIndex < o.minIndex ) return false;
			if( minIndex > o.maxIndex ) return false;
			
			return true;
		}
	}
	
	static final class Compaction {
		HashMap<RegisterID<?>,RegisterID<?>> inputMap;
		HashMap<RegisterID<?>,RegisterID<?>> outputMap;
		Instruction<?,?,?,?> newInstructions;
		int newDVarCount;
		int newIVarCount;
	}
	
    protected boolean rewritable( RegisterID<?> r ) {
		if( r == RegisterID.NONE ) return false;
		if( r.bankId.isConstant ) return false;
    	return true;
    }
	
	protected void processRead( RegisterID<?> r, List<Variable> allVars, Map<RegisterID<?>,Variable> currentVars, Set<RegisterID<?>> outputRegisters, int currentLine ) {
		if( !rewritable(r) ) return;
		
		Variable v;
		if( (v = currentVars.get(r)) == null ) {
			v = new Variable( r, 0, outputRegisters.contains(r) );
			allVars.add(v);
			currentVars.put( r, v );
		}
		v.readAt(currentLine);
	}
	
	protected List<Variable> extractVariables( List<Instruction<?,?,?,?>> instructions, Set<RegisterID<?>> outputRegisters ) {
		List<Variable> vars = new ArrayList<Variable>();
		Map<RegisterID<?>,Variable> currentVars = new HashMap<RegisterID<?>,Variable>();
		
		int line = 0;
		for( Instruction<?,?,?,?> i : instructions ) {
			if( rewritable(i.dest) ) {
				Variable destVar = new Variable(i.dest, line, outputRegisters.contains(i.dest));
				currentVars.put( i.dest, destVar );
			}
			
			processRead( i.v1, vars, currentVars, outputRegisters, line );
			processRead( i.v2, vars, currentVars, outputRegisters, line );
			processRead( i.v3, vars, currentVars, outputRegisters, line );
			++line;
		}
		
		return vars;
	}
	
	
    protected void reassign( Variable v, Collection<Variable> allVariables ) {
		short number = 0;
		boolean spaceFound = false;
		findSpace: while( !spaceFound ) {
			for( Variable v0 : allVariables ) {
				if( v0.newRegister == null ) continue; // Don't care!
				
				if( v0.newRegister.number == number && v.couldCollideWith(v0) ) {
					// Collision found; increment number and start over
					++number;
					continue findSpace;
				}
			}
			
			spaceFound = true;
		}
		
		@SuppressWarnings({"rawtypes","unchecked"})
		RegisterID newReg = new RegisterID( v.register.bankId, number );
		v.newRegister = newReg;
	}
    
    protected List<Instruction<?,?,?,?>> rewrite( List<Instruction<?,?,?,?>> instructions, Collection<Variable> variables ) {
    	List<Instruction<?,?,?,?>> rewrite = new ArrayList<Instruction<?,?,?,?>>();
    	throw new RuntimeException("Not yet implemented");
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
		
		int maxDVar = -1, maxIVar = -1;
		Map<RegisterID<?>,RegisterID<?>> inputMap = new HashMap<RegisterID<?>,RegisterID<?>>();
		Map<RegisterID<?>,RegisterID<?>> outputMap = new HashMap<RegisterID<?>,RegisterID<?>>();
		
		List<Variable> variables = extractVariables(instructions, outputRegisters);
		for( Variable v : variables ) {
			reassign(v, variables);
			
			RegisterID<?> newReg = v.newRegister;
			if( "VI".equals(v.register.bankId.abbreviation) ) {
				maxDVar = newReg.number > maxDVar ? newReg.number : maxDVar; 
			} else if( "VI".equals(v.register.bankId.abbreviation) ) {
				maxIVar = newReg.number > maxIVar ? newReg.number : maxIVar; 
			} else {
				// TODO: Need to deal with booleans.
				// Leaving this in for now so I can make sure it's catching that.
				throw new RuntimeException("Ignoring register "+newReg);
			}
			
			if( v.isInput  )  inputMap.put( v.register, v.newRegister );
			if( v.isOutput ) outputMap.put( v.register, v.newRegister );
		}
		
		List<Instruction<?,?,?,?>> compacted = rewrite(instructions, variables);
		
		throw new RuntimeException("Not yet implemented");
	}
}
