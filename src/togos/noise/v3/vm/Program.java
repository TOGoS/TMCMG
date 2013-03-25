package togos.noise.v3.vm;

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
		
		protected void run( Instruction<RT,RT,RT,RT>[] instructions, int vectorSize ) {
			for( Instruction<RT,RT,RT,RT> instr : instructions ) {
				instr.op.apply( this, instr, vectorSize ); 
			}
		}
		
		public void run( int vectorSize ) {
			assert vectorSize < maxVectorSize;
			if( firstRun ) {
				run( program.initInstructions, vectorSize );
			}
			run( program.runInstructions, vectorSize );
		}
	}
	
	/** Register type */
	public class RT {
		public class DConst extends RT {};
		public class BVar extends RT {};
		public class DVar extends RT {};
		public class None extends RT {};
	};
	
	public static final class RegisterID<Type extends RT> implements Comparable<RegisterID<?>> {
		public static final RegisterID<RT.None> NONE = new RegisterID<RT.None>((short)0);
		
		public final short number;
		public RegisterID( short number ) {
			this.number = number;
		}
		
		@Override
		public int compareTo(RegisterID<?> other) {
			return number < other.number ? -1 : number > other.number ? 1 : 0;
		}
	}
	
	public static final class Instruction<
		DestRT extends RT,
		V1RT extends RT,
		V2RT extends RT,
		V3RT extends RT
	> {
		//// Shortcuts for instantiating instructions of various types */
		public final Operator<DestRT, V1RT, V2RT, V3RT> op;
		/** High bits of each are used to indicate flags, low bits to indicate register number */
		public final short dest, v1, v2, v3;
		
		public Instruction( Operator<DestRT,V1RT,V2RT,V3RT> op, short dest, short v1, short v2, short v3 ) {
			this.op = op;
			this.dest = dest;
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}

		public Instruction( Operator<DestRT,V1RT,V2RT,V3RT> op, RegisterID<DestRT> dest, RegisterID<V1RT> v1, RegisterID<V2RT> v2, RegisterID<V3RT> v3 ) {
			this( op, dest.number, v1.number, v2.number, v3.number );
		}
	}
	
	interface Operator<
		DestRT extends RT,
		V1RT extends RT,
		V2RT extends RT,
		V3RT extends RT
	> {
		public void apply( Program.Instance pi, Instruction<DestRT,V1RT,V2RT,V3RT> inst, int vectorSize );
	}
	
	static abstract class OperatorDaDa_Da implements Operator<RT.DVar,RT.DVar,RT.DVar,RT.None> {
		protected abstract void apply(double[] dest, double[] i1, double[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<RT.DVar,RT.DVar,RT.DVar,RT.None> inst, int vectorSize ) {
			apply(
				pi.dVars[inst.dest],
				pi.dVars[inst.v1],
				pi.dVars[inst.v2],
				vectorSize
			);
		}
	}
	
	static abstract class OperatorDaDa_Ba implements Operator<RT.BVar,RT.DVar,RT.DVar,RT.None> {
		protected abstract void apply(boolean[] dest, double[] i1, double[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<RT.BVar,RT.DVar,RT.DVar,RT.None> inst, int vectorSize ) {
			apply(
				pi.bVars[inst.dest],
				pi.dVars[inst.v1],
				pi.dVars[inst.v2],
				vectorSize
			);
		}
	}
	
	static abstract class OperatorBaDaDa_Da implements Operator<RT.DVar,RT.BVar,RT.DVar,RT.DVar> {
		protected abstract void apply(double[] dest, boolean[] i1, double[] i2, double[] i3, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<RT.DVar,RT.BVar,RT.DVar,RT.DVar> inst, int vectorSize ) {
			apply(
				pi.dVars[inst.dest],
				pi.bVars[inst.v1],
				pi.dVars[inst.v2],
				pi.dVars[inst.v3],
				vectorSize
			);
		}
	}
	
	public static final Operator<RT.DVar,RT.DConst,RT.None,RT.None> LOADCONST = new Operator<RT.DVar,RT.DConst,RT.None,RT.None>() {
		@Override
		public void apply(Instance pi, Instruction<RT.DVar,RT.DConst,RT.None,RT.None> inst, int vectorSize) {
			double[] dest = pi.dVars[inst.dest];
			double constVal = pi.program.constants[inst.v1];
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
	
	/** Instructions that only need to be run once per instance (constant initialization) */
	public final Instruction<RT,RT,RT,RT>[] initInstructions;
	public final Instruction<RT,RT,RT,RT>[] runInstructions;
	public final double[] constants;
	public final int booleanVarCount, doubleVarCount;
	
	protected ThreadLocal<Reference<Instance>> instances = new ThreadLocal<Reference<Instance>>();
	
	public Program( Instruction<RT,RT,RT,RT>[] initInstructions, Instruction<RT,RT,RT,RT>[] runInstructions, double[] constants, int booleanVarCount, int doubleVarCount ) {
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
