package togos.noise.v3.vector.vm;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

public class Program
{
	/**
	 * An instance of a program (a program + all its variables)
	 * that may be run in a single thread at a time.
	 */
	public static class Instance {
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
		
		public void setDVar( int varId, double[] data, int vectorSize ) {
			assert varId < doubleVectors.length;
			assert vectorSize < maxVectorSize;
			assert data.length >= vectorSize;
			for( int i = vectorSize-1; i >= 0; --i ) {
				doubleVectors[varId][i] = data[i];
			}
		}
		
		public double[] getDVar( int varId ) {
			return doubleVectors[varId];
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
	}
	
	public abstract static class RegisterBankID<T> {
		public final boolean isConstant;
		public final Class<T> valueType;
		public final int number;
		
		private RegisterBankID( boolean isConstant, Class<T> valueType ) {
			this.isConstant = isConstant;
			this.valueType = valueType;
			
			int arenaNumber = isConstant ? 0 : 1;
			int typeNumber;
			if( valueType == Void.class ) {
				typeNumber = 0;
			} else if( valueType == Boolean.class ) {
				typeNumber = 1;
			} else if( valueType == Integer.class ) {
				typeNumber = 2;
			} else if( valueType == Double.class ) {
				typeNumber = 3;
			} else {
				throw new RuntimeException("Unsupported value type: "+valueType);
			}
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
		
		@Override
		public int compareTo(RegisterID<?> other) {
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
	public final int[] intConstants;
	public final double[] doubleConstants;
	public final int booleanVectorCount, integerVectorCount, doubleVectorCount;
	
	protected ThreadLocal<Reference<Instance>> instances = new ThreadLocal<Reference<Instance>>();
	
	public Program(
		Instruction<RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>>[] initInstructions,
		Instruction<RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>,RegisterBankID<?>>[] runInstructions,
		int[] intConstants, double[] doubleConstants,
		int booleanVectorCount, int integerVectorCount, int doubleVectorCount
	) {
		this.initInstructions = initInstructions;
		this.runInstructions = runInstructions;
		this.intConstants = intConstants;
		this.doubleConstants = doubleConstants;
		this.booleanVectorCount = booleanVectorCount;
		this.doubleVectorCount = doubleVectorCount;
		this.integerVectorCount = integerVectorCount;
	}
	
	public Instance getInstance( int maxVectorSize ) {
		Reference<Instance> ref = instances.get();
		Instance inst = ref == null ? null : ref.get();
		if( inst == null || inst.maxVectorSize < maxVectorSize ) {
			inst = new Instance( this, maxVectorSize );
			instances.set(new SoftReference<Instance>(inst));
		}
		return inst;
	}
}
