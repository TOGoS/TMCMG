package togos.noise.v3.vector.vm;

import togos.noise.v3.vector.vm.Program.Instruction;
import togos.noise.v3.vector.vm.Program.Operator;
import togos.noise.v3.vector.vm.Program.RegisterBankID.BVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.DConst;
import togos.noise.v3.vector.vm.Program.RegisterBankID.DVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IConst;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.None;

public class Operators
{
	public static abstract class OperatorBaDaDa_Da implements Operator<DVar,BVar,DVar,DVar> {
		protected abstract void apply(double[] dest, boolean[] i1, double[] i2, double[] i3, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<DVar,BVar,DVar,DVar> inst, int vectorSize ) {
			apply(
				pi.doubleVectors[inst.dest.number],
				pi.booleanVectors[inst.v1.number],
				pi.doubleVectors[inst.v2.number],
				pi.doubleVectors[inst.v3.number],
				vectorSize
			);
		}
	}
	public static abstract class OperatorDaDa_Ba implements Operator<BVar,DVar,DVar,None> {
		protected abstract void apply(boolean[] dest, double[] i1, double[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<BVar,DVar,DVar,None> inst, int vectorSize ) {
			apply(
				pi.booleanVectors[inst.dest.number],
				pi.doubleVectors[inst.v1.number],
				pi.doubleVectors[inst.v2.number],
				vectorSize
			);
		}
	}
	public static abstract class OperatorIaIa_Ia implements Operator<IVar,IVar,IVar,None> {
		protected abstract void apply(int[] dest, int[] i1, int[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<IVar,IVar,IVar,None> inst, int vectorSize ) {
			apply(
				pi.integerVectors[inst.dest.number],
				pi.integerVectors[inst.v1.number],
				pi.integerVectors[inst.v2.number],
				vectorSize
			);
		}
	}
	public static abstract class OperatorBaBa_Ba implements Operator<BVar,BVar,BVar,None> {
		protected abstract void apply(boolean[] dest, boolean[] i1, boolean[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<BVar,BVar,BVar,None> inst, int vectorSize ) {
			apply(
				pi.booleanVectors[inst.dest.number],
				pi.booleanVectors[inst.v1.number],
				pi.booleanVectors[inst.v2.number],
				vectorSize
			);
		}
	}
	public static abstract class OperatorDaDa_Da implements Operator<DVar,DVar,DVar,None> {
		protected abstract void apply(double[] dest, double[] i1, double[] i2, int vectorSize);
		
		public void apply( Program.Instance pi, Instruction<DVar,DVar,DVar,None> inst, int vectorSize ) {
			apply(
				pi.doubleVectors[inst.dest.number],
				pi.doubleVectors[inst.v1.number],
				pi.doubleVectors[inst.v2.number],
				vectorSize
			);
		}
	}
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
	public static final OperatorDaDa_Da POWER = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] i1, double[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = Math.pow(i1[i], i2[i]);
		}
	};
	public static final OperatorDaDa_Da DIVIDE = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] i1, double[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] / i2[i];
		}
	};
	public static final OperatorDaDa_Da MULTIPLY = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] i1, double[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] * i2[i];
		}
	};
	public static final OperatorDaDa_Da SUBTRACT = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] i1, double[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] - i2[i];
		}
	};
	public static final OperatorDaDa_Da ADD = new OperatorDaDa_Da() {
		public void apply( double[] dest, double[] i1, double[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] + i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITWISE_XOR = new OperatorIaIa_Ia() {
		public void apply( int[] dest, int[] i1, int[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] ^ i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITWISE_AND = new OperatorIaIa_Ia() {
		public void apply( int[] dest, int[] i1, int[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] & i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITWISE_OR = new OperatorIaIa_Ia() {
		public void apply( int[] dest, int[] i1, int[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] | i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITSHIFT_RIGHT = new OperatorIaIa_Ia() {
		public void apply( int[] dest, int[] i1, int[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] >> i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITSHIFT_LEFT = new OperatorIaIa_Ia() {
		public void apply( int[] dest, int[] i1, int[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] << i2[i];
		}
	};
	public static final OperatorBaBa_Ba LOGICAL_XOR = new OperatorBaBa_Ba() {
		public void apply( boolean[] dest, boolean[] i1, boolean[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] ? !i2[i] : i2[i];
		}
	};
	public static final OperatorBaBa_Ba LOGICAL_AND = new OperatorBaBa_Ba() {
		public void apply( boolean[] dest, boolean[] i1, boolean[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] && !i2[i];
		}
	};
	public static final OperatorBaBa_Ba LOGICAL_OR = new OperatorBaBa_Ba() {
		public void apply( boolean[] dest, boolean[] i1, boolean[] i2, int vectorSize ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] || i2[i];
		}
	};
	public static final Operator<DVar,IVar,None,None> INT_TO_DOUBLE = new Operator<DVar,IVar,None,None>() {
		@Override
		public void apply(Program.Instance pi, Instruction<DVar,IVar,None,None> inst, int vectorSize) {
			int[] src = pi.integerVectors[inst.v1.number];
			double[] dest = pi.doubleVectors[inst.dest.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = src[i];
		}
	};
	public static final Operator<IVar,IConst,None,None> LOAD_INT_CONST = new Operator<IVar,IConst,None,None>() {
		@Override
		public void apply(Program.Instance pi, Instruction<IVar,IConst,None,None> inst, int vectorSize) {
			int[] dest = pi.integerVectors[inst.dest.number];
			int constVal = pi.program.intConstants[inst.v1.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = constVal;
		}
	};
	public static final Operator<DVar,IConst,None,None> LOAD_INT_CONST_AS_DOUBLE = new Operator<DVar,IConst,None,None>() {
		@Override
		public void apply(Program.Instance pi, Instruction<DVar,IConst,None,None> inst, int vectorSize) {
			double[] dest = pi.doubleVectors[inst.dest.number];
			double constVal = pi.program.intConstants[inst.v1.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = constVal;
		}
	};
	public static final Operator<DVar,DConst,None,None> LOAD_DOUBLE_CONST = new Operator<DVar,DConst,None,None>() {
		@Override
		public void apply(Program.Instance pi, Instruction<DVar,DConst,None,None> inst, int vectorSize) {
			double[] dest = pi.doubleVectors[inst.dest.number];
			double constVal = pi.program.doubleConstants[inst.v1.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = constVal;
		}
	};
	
	// TODO: (FAST)SINE, MOD, IF
}
