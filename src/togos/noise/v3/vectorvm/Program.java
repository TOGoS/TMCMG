package togos.noise.v3.vectorvm;

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
		public final boolean[][] bVars;
		public final double[][]  dVars;
		public final int maxVectorSize;
		protected boolean firstRun = true;
		
		public Instance( Program program, int maxVectorSize ) {
			this.program = program;
			this.bVars = new boolean[program.booleanVarCount][maxVectorSize];
			this.dVars = new double[program.doubleVarCount][maxVectorSize];
			this.maxVectorSize = maxVectorSize;
		}
		
		public void setDVar( int varId, double[] data, int vectorSize ) {
			assert varId < dVars.length;
			assert vectorSize < maxVectorSize;
			assert data.length >= vectorSize;
			for( int i = vectorSize-1; i >= 0; --i ) {
				dVars[varId][i] = data[i];
			}
		}
		
		public double[] getDVar( int varId ) {
			return dVars[varId];
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
		public final int number;
		protected RegisterBankID( int number ) {
			this.number = number;
		}
		
		public static final class DConst extends RegisterBankID {
			public static final DConst INSTANCE = new DConst();
			private DConst() { super(1); }
		};
		public static class BVar extends RegisterBankID {
			static final BVar INSTANCE = new BVar();
			private BVar() { super(2); }
		};
		public static class DVar extends RegisterBankID {
			static final DVar INSTANCE = new DVar();
			private DVar() { super(3); }
		};
		public static class None extends RegisterBankID {
			static final None INSTANCE = new None();
			private None() { super(4); }
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
				pi.dVars[inst.dest.number],
				pi.dVars[inst.v1.number],
				pi.dVars[inst.v2.number],
				vectorSize
			);
		}
	}
	
	static abstract class OperatorDaDa_Ba implements Operator<RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.None> {
		protected abstract void apply(boolean[] dest, double[] i1, double[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar,RegisterBankID.None> inst, int vectorSize ) {
			apply(
				pi.bVars[inst.dest.number],
				pi.dVars[inst.v1.number],
				pi.dVars[inst.v2.number],
				vectorSize
			);
		}
	}
	
	static abstract class OperatorBaDaDa_Da implements Operator<RegisterBankID.DVar,RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar> {
		protected abstract void apply(double[] dest, boolean[] i1, double[] i2, double[] i3, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<RegisterBankID.DVar,RegisterBankID.BVar,RegisterBankID.DVar,RegisterBankID.DVar> inst, int vectorSize ) {
			apply(
				pi.dVars[inst.dest.number],
				pi.bVars[inst.v1.number],
				pi.dVars[inst.v2.number],
				pi.dVars[inst.v3.number],
				vectorSize
			);
		}
	}
	
	public static final Operator<RegisterBankID.DVar,RegisterBankID.DConst,RegisterBankID.None,RegisterBankID.None> LOADCONST = new Operator<RegisterBankID.DVar,RegisterBankID.DConst,RegisterBankID.None,RegisterBankID.None>() {
		@Override
		public void apply(Instance pi, Instruction<RegisterBankID.DVar,RegisterBankID.DConst,RegisterBankID.None,RegisterBankID.None> inst, int vectorSize) {
			double[] dest = pi.dVars[inst.dest.number];
			double constVal = pi.program.constants[inst.v1.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = constVal;
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
	public final double[] constants;
	public final int booleanVarCount, doubleVarCount;
	
	protected ThreadLocal<Reference<Instance>> instances = new ThreadLocal<Reference<Instance>>();
	
	public Program( Instruction<RegisterBankID,RegisterBankID,RegisterBankID,RegisterBankID>[] initInstructions, Instruction<RegisterBankID,RegisterBankID,RegisterBankID,RegisterBankID>[] runInstructions, double[] constants, int booleanVarCount, int doubleVarCount ) {
		this.initInstructions = initInstructions;
		this.runInstructions = runInstructions;
		this.constants = constants;
		this.booleanVarCount = booleanVarCount;
		this.doubleVarCount = doubleVarCount;
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
