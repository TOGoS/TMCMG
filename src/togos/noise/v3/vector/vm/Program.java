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
		
		protected void run( Instruction<RegisterBankID,RegisterBankID,RegisterBankID,RegisterBankID>[] instructions, int vectorSize ) {
			for( Instruction<RegisterBankID,RegisterBankID,RegisterBankID,RegisterBankID> instr : instructions ) {
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
	
	public abstract static class RegisterBankID {
		public final boolean isConstant;
		public final Class<?> valueType;
		public final int number;
		
		private RegisterBankID( boolean isConstant, Class<?> valueType ) {
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
		
		public static class None extends RegisterBankID {
			public static final None INSTANCE = new None();
			private None() { super(true, Void.class); }
		};
		public static final class IConst extends RegisterBankID {
			public static final IConst INSTANCE = new IConst();
			private IConst() { super(true, Integer.class); }
		};
		public static final class DConst extends RegisterBankID {
			public static final DConst INSTANCE = new DConst();
			private DConst() { super(true, Double.class); }
		};
		public static class BVar extends RegisterBankID {
			public static final BVar INSTANCE = new BVar();
			private BVar() { super(false, Boolean.class); }
		};
		public static class IVar extends RegisterBankID {
			public static final IVar INSTANCE = new IVar();
			private IVar() { super(false, Integer.class); }
		};
		public static class DVar extends RegisterBankID {
			public static final DVar INSTANCE = new DVar();
			private DVar() { super(false, Double.class); }
		};
	};
	
	public static final class RegisterID<BankID extends RegisterBankID> implements Comparable<RegisterID<?>> {
		public static <Bank extends RegisterBankID> RegisterID<Bank> create( Bank bank, short number ) {
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
		DestRT extends RegisterBankID,
		V1RT extends RegisterBankID,
		V2RT extends RegisterBankID,
		V3RT extends RegisterBankID
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
	
	interface Operator<
		DestRT extends RegisterBankID,
		V1RT extends RegisterBankID,
		V2RT extends RegisterBankID,
		V3RT extends RegisterBankID
	> {
		public void apply( Program.Instance pi, Instruction<DestRT,V1RT,V2RT,V3RT> inst, int vectorSize );
	}
	
	static abstract class OperatorDaDa_Da implements Operator<RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.None> {
		protected abstract void apply(double[] dest, double[] i1, double[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.None> inst, int vectorSize ) {
			apply(
				pi.doubleVectors[inst.dest.number],
				pi.doubleVectors[inst.v1.number],
				pi.doubleVectors[inst.v2.number],
				vectorSize
			);
		}
	}
	
	static abstract class OperatorBaBa_Ba implements Operator<RegisterBankID.BVar,RegisterBankID.BVar,RegisterBankID.BVar,RegisterBankID.None> {
		protected abstract void apply(boolean[] dest, boolean[] i1, boolean[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<RegisterBankID.BVar,RegisterBankID.BVar,RegisterBankID.BVar,RegisterBankID.None> inst, int vectorSize ) {
			apply(
				pi.booleanVectors[inst.dest.number],
				pi.booleanVectors[inst.v1.number],
				pi.booleanVectors[inst.v2.number],
				vectorSize
			);
		}
	}
	
	static abstract class OperatorIaIa_Ia implements Operator<RegisterBankID.IVar,RegisterBankID.IVar,RegisterBankID.IVar,RegisterBankID.None> {
		protected abstract void apply(int[] dest, int[] i1, int[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<RegisterBankID.IVar,RegisterBankID.IVar,RegisterBankID.IVar,RegisterBankID.None> inst, int vectorSize ) {
			apply(
				pi.integerVectors[inst.dest.number],
				pi.integerVectors[inst.v1.number],
				pi.integerVectors[inst.v2.number],
				vectorSize
			);
		}
	}
	
	static abstract class OperatorDaDa_Ba implements Operator<RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.None> {
		protected abstract void apply(boolean[] dest, double[] i1, double[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.None> inst, int vectorSize ) {
			apply(
				pi.booleanVectors[inst.dest.number],
				pi.doubleVectors[inst.v1.number],
				pi.doubleVectors[inst.v2.number],
				vectorSize
			);
		}
	}
	
	static abstract class OperatorBaDaDa_Da implements Operator<RegisterBankID.DVar,RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar> {
		protected abstract void apply(double[] dest, boolean[] i1, double[] i2, double[] i3, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<RegisterBankID.DVar,RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar> inst, int vectorSize ) {
			apply(
				pi.doubleVectors[inst.dest.number],
				pi.booleanVectors[inst.v1.number],
				pi.doubleVectors[inst.v2.number],
				pi.doubleVectors[inst.v3.number],
				vectorSize
			);
		}
	}
	
	public static final Operator<RegisterBankID.DVar,RegisterBankID.DConst,RegisterBankID.None,RegisterBankID.None> LOAD_DOUBLE_CONST = new Operator<RegisterBankID.DVar,RegisterBankID.DConst,RegisterBankID.None,RegisterBankID.None>() {
		@Override
		public void apply(Instance pi, Instruction<RegisterBankID.DVar,RegisterBankID.DConst,RegisterBankID.None,RegisterBankID.None> inst, int vectorSize) {
			double[] dest = pi.doubleVectors[inst.dest.number];
			double constVal = pi.program.doubleConstants[inst.v1.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = constVal;
		}
	};
	public static final Operator<RegisterBankID.DVar,RegisterBankID.IConst,RegisterBankID.None,RegisterBankID.None> LOAD_INT_CONST_AS_DOUBLE = new Operator<RegisterBankID.DVar,RegisterBankID.IConst,RegisterBankID.None,RegisterBankID.None>() {
		@Override
		public void apply(Instance pi, Instruction<RegisterBankID.DVar,RegisterBankID.IConst,RegisterBankID.None,RegisterBankID.None> inst, int vectorSize) {
			double[] dest = pi.doubleVectors[inst.dest.number];
			double constVal = pi.program.intConstants[inst.v1.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = constVal;
		}
	};
	public static final Operator<RegisterBankID.IVar,RegisterBankID.IConst,RegisterBankID.None,RegisterBankID.None> LOAD_INT_CONST = new Operator<RegisterBankID.IVar,RegisterBankID.IConst,RegisterBankID.None,RegisterBankID.None>() {
		@Override
		public void apply(Instance pi, Instruction<RegisterBankID.IVar,RegisterBankID.IConst,RegisterBankID.None,RegisterBankID.None> inst, int vectorSize) {
			int[] dest = pi.integerVectors[inst.dest.number];
			int constVal = pi.program.intConstants[inst.v1.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = constVal;
		}
	};
	
	public static final Operator<RegisterBankID.DVar,RegisterBankID.IVar,RegisterBankID.None,RegisterBankID.None> INT_TO_DOUBLE = new Operator<RegisterBankID.DVar,RegisterBankID.IVar,RegisterBankID.None,RegisterBankID.None>() {
		@Override
		public void apply(Instance pi, Instruction<RegisterBankID.DVar,RegisterBankID.IVar,RegisterBankID.None,RegisterBankID.None> inst, int vectorSize) {
			int[] src = pi.integerVectors[inst.v1.number];
			double[] dest = pi.doubleVectors[inst.dest.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = src[i];
		}
	};
	
	public static final OperatorBaBa_Ba LOGICAL_OR = new OperatorBaBa_Ba() {
		public void apply( boolean[] dest, boolean[] i1, boolean[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] || i2[i];
		}
	};
	public static final OperatorBaBa_Ba LOGICAL_AND = new OperatorBaBa_Ba() {
		public void apply( boolean[] dest, boolean[] i1, boolean[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] && !i2[i];
		}
	};
	public static final OperatorBaBa_Ba LOGICAL_XOR = new OperatorBaBa_Ba() {
		public void apply( boolean[] dest, boolean[] i1, boolean[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] ? !i2[i] : i2[i];
		}
	};

	public static final OperatorIaIa_Ia BITWISE_OR = new OperatorIaIa_Ia() {
		public void apply( int[] dest, int[] i1, int[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] | i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITWISE_AND = new OperatorIaIa_Ia() {
		public void apply( int[] dest, int[] i1, int[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] & i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITWISE_XOR = new OperatorIaIa_Ia() {
		public void apply( int[] dest, int[] i1, int[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] ^ i2[i];
		}
	};
	
	public static final OperatorDaDa_Da ADD = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] i1, double[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] + i2[i];
		}
	};
	public static final OperatorDaDa_Da SUBTRACT = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] i1, double[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] - i2[i];
		}
	};
	public static final OperatorDaDa_Da MULTIPLY = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] i1, double[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] * i2[i];
		}
	};
	public static final OperatorDaDa_Da DIVIDE = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] i1, double[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] / i2[i];
		}
	};
	public static final OperatorDaDa_Da POWER = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] i1, double[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = Math.pow(i1[i], i2[i]);
		}
	};
	public static final OperatorDaDa_Da MOD = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] num, double[] den, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) {
				if( num[i] == 0 || den[i] == 0 ) dest[i] = 0;
				else {
					boolean invert = false;
					if( num[i] < 0 ) invert = !invert;
					if( den[i] < 0 ) invert = !invert;
					double v1 = invert ? -num[i] : num[i];
					double factor = Math.floor(v1 / den[i]);
					v1 -= factor*den[i];
					dest[i] = invert ? -v1 : v1;
				}
			}
		}
	};
	// TODO: (FAST)SINE, MOD, IF
	
	/**
	 * Instructions that only need to be run once per instance (constant initialization)
	 * 
	 * TODO: Need to make sure that destination registers of initInstructions
	 * are never overwritten by any operations in runInstructions! 
	 */
	public final Instruction<RegisterBankID,RegisterBankID,RegisterBankID,RegisterBankID>[] initInstructions;
	public final Instruction<RegisterBankID,RegisterBankID,RegisterBankID,RegisterBankID>[] runInstructions;
	public final int[] intConstants;
	public final double[] doubleConstants;
	public final int booleanVectorCount, integerVectorCount, doubleVectorCount;
	
	protected ThreadLocal<Reference<Instance>> instances = new ThreadLocal<Reference<Instance>>();
	
	public Program(
		Instruction<RegisterBankID,RegisterBankID,RegisterBankID,RegisterBankID>[] initInstructions,
		Instruction<RegisterBankID,RegisterBankID,RegisterBankID,RegisterBankID>[] runInstructions,
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
