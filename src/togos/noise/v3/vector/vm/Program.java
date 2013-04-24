package togos.noise.v3.vector.vm;

import java.io.PrintStream;

import togos.noise.v3.vector.util.HasMaxVectorSize;
import togos.noise.v3.vector.util.SoftThreadLocalVectorBuffer;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IVar;

public class Program
{
	/**
	 * An instance of a program (a program + all its variables)
	 * that may be run in a single thread at a time.
	 */
	public static class Instance implements HasMaxVectorSize {
		public final Program program;
		public final boolean[][] booleanVectors;
		public final int[][] integerVectors;
		public final double[][]  doubleVectors;
		public final int maxVectorSize;
		protected boolean firstRun = true;
		
		public Instance( Program program, int maxVectorSize ) {
			this.program = program;
			this.booleanVectors = new boolean[program.booleanVectorCount][maxVectorSize];
			this.integerVectors = new int[program.integerVectorCount][maxVectorSize];
			this.doubleVectors = new double[program.doubleVectorCount][maxVectorSize];
			this.maxVectorSize = maxVectorSize;
		}
		
		public int getMaxVectorSize() { return maxVectorSize; }
		
		public void setDVar( int varId, double[] data, int vectorSize ) {
			assert varId < doubleVectors.length;
			assert vectorSize <= maxVectorSize;
			assert data.length >= vectorSize;
			for( int i = vectorSize-1; i >= 0; --i ) {
				doubleVectors[varId][i] = data[i];
			}
		}
		
		public double[] getDVar( int varId ) {
			return doubleVectors[varId];
		}
		public int[] getIVector( RegisterID<IVar> reg ) {
			return integerVectors[reg.number];
		}
		
		protected void run( Instruction<RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>>[] instructions, int vectorSize ) {
			for( Instruction<RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>> instr : instructions ) {
				instr.op.apply( this, instr, vectorSize ); 
			}
		}
		
		public void run( int vectorSize ) {
			assert vectorSize <= maxVectorSize;
			if( firstRun ) {
				run( program.initInstructions, vectorSize );
			}
			run( program.runInstructions, vectorSize );
		}

		public void dumpData(PrintStream ps) {
			for( int i=0; i<booleanVectors.length; ++i ) {
				ps.printf("  %-10s % 6d :", "boolean", i);
				for( int j=0; j<maxVectorSize; ++j ) {
					ps.printf(" "+(booleanVectors[i][j] ? '1' : '0'));
				}
				ps.println();
			}
			if( booleanVectors.length > 0 ) ps.println();
			
			for( int i=0; i<integerVectors.length; ++i ) {
				ps.printf("  %-10s % 6d :", "integer", i);
				for( int j=0; j<maxVectorSize; ++j ) {
					ps.printf(" % 4d", integerVectors[i][j]);
				}
				ps.println();
			}
			if( integerVectors.length > 0 ) ps.println();
			
			for( int i=0; i<doubleVectors.length; ++i ) {
				ps.printf("  %-10s % 6d :", "double", i);
				for( int j=0; j<maxVectorSize; ++j ) {
					ps.printf(" % 8.4f", doubleVectors[i][j]);
				}
				ps.println();
			}
		}
	}
	
	public abstract static class RegisterBankID<T> {
		public final boolean isConstant;
		public final Class<T> valueType;
		public final int number;
		public final String abbreviation;
		
		private RegisterBankID( boolean isConstant, Class<T> valueType ) {
			this.isConstant = isConstant;
			this.valueType = valueType;
			
			char arenaChar = isConstant ? 'C' : 'V';
			int arenaNumber = isConstant ? 0 : 1;
			char typeChar;
			int typeNumber;
			if( valueType == Void.class ) {
				typeChar = '_';
				typeNumber = 0;
			} else if( valueType == Boolean.class ) {
				typeChar = 'B';
				typeNumber = 1;
			} else if( valueType == Integer.class ) {
				typeChar = 'I';
				typeNumber = 2;
			} else if( valueType == Double.class ) {
				typeChar = 'D';
				typeNumber = 3;
			} else {
				throw new RuntimeException("Unsupported value type: "+valueType);
			}
			this.abbreviation = arenaChar + "" + typeChar;
			this.number = (arenaNumber << 4) | typeNumber;
		}
		
		public static class None extends RegisterBankID<Void> {
			public static final None INSTANCE = new None();
			private None() { super(true, Void.class); }
		};
		public static final class IConst extends RegisterBankID<Integer> {
			public static final IConst INSTANCE = new IConst();
			private IConst() { super(true, Integer.class); }
		};
		public static final class DConst extends RegisterBankID<Double> {
			public static final DConst INSTANCE = new DConst();
			private DConst() { super(true, Double.class); }
		};
		public static class BVar extends RegisterBankID<Boolean> {
			public static final BVar INSTANCE = new BVar();
			private BVar() { super(false, Boolean.class); }
		};
		public static class IVar extends RegisterBankID<Integer> {
			public static final IVar INSTANCE = new IVar();
			private IVar() { super(false, Integer.class); }
		};
		public static class DVar extends RegisterBankID<Double> {
			public static final DVar INSTANCE = new DVar();
			private DVar() { super(false, Double.class); }
		};
	};
	
	public static final class RegisterID<BankID extends RegisterBankID<?>> implements Comparable<RegisterID<?>> {
		public static <Bank extends RegisterBankID<?>> RegisterID<Bank> create( Bank bank, short number ) {
			return new RegisterID<Bank>( bank, number );
		}
		
		public static final RegisterID<RegisterBankID.None> NONE = create( RegisterBankID.None.INSTANCE, (short)0 );
		
		public final BankID bankId;
		public final short number;
		public RegisterID( BankID bankId, short number ) {
			this.bankId = bankId;
			this.number = number;
		}
		
		public String toString() {
			return bankId.abbreviation+number;
		}
		
		@Override public int hashCode() {
			return bankId.number | (number << 16);
		}
		
		public boolean equals(RegisterID<?> o) {
			return number == o.number && bankId.number == o.bankId.number;
		}
		
		@Override public boolean equals( Object o ) {
			return o instanceof RegisterID && equals((RegisterID<?>)o);
		}
		
		@Override public int compareTo(RegisterID<?> other) {
			return bankId.number < other.bankId.number ? -1 : bankId.number > other.bankId.number ? 1 :
				number < other.number ? -1 : number > other.number ? 1 : 0;
		}
	}
	
	public static final class Instruction<
		DestRT extends RegisterBankID<?>,
		V1RT extends RegisterBankID<?>,
		V2RT extends RegisterBankID<?>,
		V3RT extends RegisterBankID<?>
	> {
		//// Shortcuts for instantiating instructions of various types */
		public final Operator<DestRT, V1RT, V2RT, V3RT> op;
		/** High bits of each are used to indicate flags, low bits to indicate register number */
		public final RegisterID<DestRT> dest;
		public final RegisterID<V1RT> v1;
		public final RegisterID<V2RT> v2;
		public final RegisterID<V3RT> v3;
		
		public Instruction( Operator<DestRT,V1RT,V2RT,V3RT> op, RegisterID<DestRT> dest, RegisterID<V1RT> v1, RegisterID<V2RT> v2, RegisterID<V3RT> v3 ) {
			this.op = op;
			this.dest = dest;
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}
	}
	
	public interface Operator<
		DestRT extends RegisterBankID<?>,
		V1RT extends RegisterBankID<?>,
		V2RT extends RegisterBankID<?>,
		V3RT extends RegisterBankID<?>
	> {
		public String getName();
		public void apply( Program.Instance pi, Instruction<DestRT,V1RT,V2RT,V3RT> inst, int vectorSize );
	}
	
	/**
	 * Instructions that only need to be run once per instance (constant initialization)
	 * 
	 * TODO: Need to make sure that destination registers of initInstructions
	 * are never overwritten by any operations in runInstructions! 
	 */
	public final Instruction<RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>>[] initInstructions;
	public final Instruction<RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>>[] runInstructions;
	public final int[] integerConstants;
	public final double[] doubleConstants;
	public final int booleanVectorCount, integerVectorCount, doubleVectorCount;
	
	protected SoftThreadLocalVectorBuffer<Instance> instanceVar = new SoftThreadLocalVectorBuffer<Instance>() {
		public Instance initialValue( int vectorSize ) {
			return new Instance( Program.this, vectorSize );
		}
	};
	
	public Program(
		Instruction<RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>>[] initInstructions,
		Instruction<RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>>[] runInstructions,
		int[] intConstants, double[] doubleConstants,
		int booleanVectorCount, int integerVectorCount, int doubleVectorCount
	) {
		this.initInstructions = initInstructions;
		this.runInstructions = runInstructions;
		this.integerConstants = intConstants;
		this.doubleConstants = doubleConstants;
		this.booleanVectorCount = booleanVectorCount;
		this.doubleVectorCount = doubleVectorCount;
		this.integerVectorCount = integerVectorCount;
	}
	
	public Instance getInstance( int maxVectorSize ) {
		return instanceVar.get( maxVectorSize );
	}
	
	protected static String formatRegister( RegisterID<?> r ) {
		return r == RegisterID.NONE ? "" : r.toString();
	}
	
	protected static String formatInstruction( Instruction<?,?,?,?> i ) {
		return String.format("  %-30s  %6s  %6s  %6s  %6s",
			i.op.getName(),
			formatRegister(i.dest), formatRegister(i.v1),
			formatRegister(i.v2), formatRegister(i.v3)
		);
	}
		
	public void dump( PrintStream ps ) {
		for( int i=0; i<integerConstants.length; ++i ) {
			ps.printf("%-16s % 3d  % 11d\n", "integer-constant", i, integerConstants[i]);
		}
		if( integerConstants.length > 0 ) ps.println();
		
		for( int i=0; i<doubleConstants.length; ++i ) {
			ps.printf("%-16s % 3d  % 24.12f\n", "double-constant", i, doubleConstants[i]);
		}
		if( doubleConstants.length > 0 ) ps.println();
		
		ps.println("init:");
		for( Instruction<?,?,?,?> i : initInstructions ) ps.println( formatInstruction(i) );
		
		ps.println();
		ps.println("run:");
		for( Instruction<?,?,?,?> i : runInstructions ) ps.println( formatInstruction(i) );
	}
}
