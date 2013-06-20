package togos.noise.v3.vector.vm;

import togos.noise.MathUtil;
import togos.noise.v3.vector.function.LFunctionBaBa_Ba;
import togos.noise.v3.vector.function.LFunctionBaDaDa_Da;
import togos.noise.v3.vector.function.LFunctionDaDa_Ba;
import togos.noise.v3.vector.function.LFunctionDaDa_Da;
import togos.noise.v3.vector.function.LFunctionDa_Da;
import togos.noise.v3.vector.function.LFunctionIaIa_Ia;
import togos.noise.v3.vector.vm.Program.Instance;
import togos.noise.v3.vector.vm.Program.Instruction;
import togos.noise.v3.vector.vm.Program.Operator;
import togos.noise.v3.vector.vm.Program.RegisterBankID;
import togos.noise.v3.vector.vm.Program.RegisterBankID.BVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.DConst;
import togos.noise.v3.vector.vm.Program.RegisterBankID.DVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IConst;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.None;

public class Operators
{
	public static abstract class AbstractOperator<
		DestRT extends RegisterBankID<?>,
		V1RT extends RegisterBankID<?>,
		V2RT extends RegisterBankID<?>,
		V3RT extends RegisterBankID<?>
	> implements Operator<DestRT,V1RT,V2RT,V3RT> {
		protected final String name;
		protected AbstractOperator( String name ) {
			this.name = name;
		}
		@Override public String getName() { return name; }
	}

	public static abstract class OperatorBaDaDa_Da extends AbstractOperator<DVar,BVar,DVar,DVar> implements LFunctionBaDaDa_Da {
		public OperatorBaDaDa_Da( String name ) { super(name); }
		public void apply( Program.Instance pi, Instruction<DVar,BVar,DVar,DVar> inst, int vectorSize ) {
			apply(
				vectorSize,
				pi.booleanVectors[inst.v1.number],
				pi.doubleVectors[inst.v2.number],
				pi.doubleVectors[inst.v3.number],
				pi.doubleVectors[inst.dest.number]
			);
		}
	}
	public static abstract class OperatorDaDa_Ba extends AbstractOperator<BVar,DVar,DVar,None> implements LFunctionDaDa_Ba {
		public OperatorDaDa_Ba(String name) { super(name); }
		public void apply( Program.Instance pi, Instruction<BVar,DVar,DVar,None> inst, int vectorSize ) {
			apply(
				vectorSize,
				pi.doubleVectors[inst.v1.number],
				pi.doubleVectors[inst.v2.number],
				pi.booleanVectors[inst.dest.number]
			);
		}
	}
	public static abstract class OperatorIaIa_Ia extends AbstractOperator<IVar,IVar,IVar,None> implements LFunctionIaIa_Ia {
		public OperatorIaIa_Ia( String name ) { super(name); }
		public void apply( Program.Instance pi, Instruction<IVar,IVar,IVar,None> inst, int vectorSize ) {
			apply(
				vectorSize,
				pi.integerVectors[inst.v1.number],
				pi.integerVectors[inst.v2.number],
				pi.integerVectors[inst.dest.number]
			);
		}
	}
	public static abstract class OperatorBaBa_Ba extends AbstractOperator<BVar,BVar,BVar,None> implements LFunctionBaBa_Ba {
		public OperatorBaBa_Ba( String name ) { super(name); }
		public void apply( Program.Instance pi, Instruction<BVar,BVar,BVar,None> inst, int vectorSize ) {
			apply(
				vectorSize,
				pi.booleanVectors[inst.v1.number],
				pi.booleanVectors[inst.v2.number],
				pi.booleanVectors[inst.dest.number]
			);
		}
	}

	public static abstract class OperatorDa_Da extends AbstractOperator<DVar,DVar,None,None> implements LFunctionDa_Da {
		public OperatorDa_Da( String name ) { super(name); }
		
		public void apply( Program.Instance pi, Instruction<DVar,DVar,None,None> inst, int vectorSize ) {
			apply(
				vectorSize,
				pi.doubleVectors[inst.v1.number],
				pi.doubleVectors[inst.dest.number]
			);
		}
	}
	
	public static abstract class OperatorDaDa_Da extends AbstractOperator<DVar,DVar,DVar,None> implements LFunctionDaDa_Da {
		public OperatorDaDa_Da( String name ) { super(name); }
		public void apply( Program.Instance pi, Instruction<DVar,DVar,DVar,None> inst, int vectorSize ) {
			apply(
				vectorSize,
				pi.doubleVectors[inst.v1.number],
				pi.doubleVectors[inst.v2.number],
				pi.doubleVectors[inst.dest.number]
			);
		}
	}
	
	public static final OperatorDaDa_Da FLOORED_DIVISION_MODULUS = new OperatorDaDa_Da("floored-division-mod") {
		public void apply( int vectorSize, double[] num, double[] den, double[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) {
				/*
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
				*/
				dest[i] = MathUtil.safeFlooredDivisionModulus( num[i], den[i] );
			}
		}
	};
	
	//// Comparison ////
	
	public static final OperatorDaDa_Ba COMPARE_GREATER = new OperatorDaDa_Ba("compare-greater") {
		public void apply( int vectorSize, double[] i1, double[] i2, boolean[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] > i2[i];
		}
	};
	public static final OperatorDaDa_Ba COMPARE_GREATER_OR_EQUAL = new OperatorDaDa_Ba("compare-greater-or-equal") {
		public void apply( int vectorSize, double[] i1, double[] i2, boolean[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] >= i2[i];
		}
	};
	public static final OperatorDaDa_Ba COMPARE_EQUAL = new OperatorDaDa_Ba("compare-equal") {
		public void apply( int vectorSize, double[] i1, double[] i2, boolean[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] == i2[i];
		}
	};
	public static final OperatorDaDa_Ba COMPARE_NOT_EQUAL = new OperatorDaDa_Ba("compare-not-equal") {
		public void apply( int vectorSize, double[] i1, double[] i2, boolean[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] != i2[i];
		}
	};
	public static final OperatorDaDa_Ba COMPARE_LESSER_OR_EQUAL = new OperatorDaDa_Ba("compare-lesser-or-equal") {
		public void apply( int vectorSize, double[] i1, double[] i2, boolean[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] <= i2[i];
		}
	};
	public static final OperatorDaDa_Ba COMPARE_LESSER = new OperatorDaDa_Ba("compare-lesser") {
		public void apply( int vectorSize, double[] i1, double[] i2, boolean[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] < i2[i];
		}
	};

	
	//// Logical operations ////
	
	public static final OperatorBaBa_Ba LOGICAL_AND = new OperatorBaBa_Ba("logical-and") {
		public void apply( int vectorSize, boolean[] i1, boolean[] i2, boolean[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] && i2[i];
		}
	};
	public static final OperatorBaBa_Ba LOGICAL_OR = new OperatorBaBa_Ba("logical-or") {
		public void apply( int vectorSize, boolean[] i1, boolean[] i2, boolean[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] || i2[i];
		}
	};
	public static final OperatorBaBa_Ba LOGICAL_XOR = new OperatorBaBa_Ba("logical-xor") {
		public void apply( int vectorSize, boolean[] i1, boolean[] i2, boolean[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] ? !i2[i] : i2[i];
		}
	};
	
	//// Bitwise operations ////
	
	public static final OperatorIaIa_Ia BITWISE_AND = new OperatorIaIa_Ia("bitwise-and") {
		public void apply( int vectorSize, int[] i1, int[] i2, int[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] & i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITWISE_OR = new OperatorIaIa_Ia("bitwise-or") {
		public void apply( int vectorSize, int[] i1, int[] i2, int[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] | i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITWISE_XOR = new OperatorIaIa_Ia("bitwise-xor") {
		public void apply( int vectorSize, int[] i1, int[] i2, int[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] ^ i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITSHIFT_LEFT = new OperatorIaIa_Ia("bitshift-left") {
		public void apply( int vectorSize, int[] i1, int[] i2, int[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] << i2[i];
		}
	};
	public static final OperatorIaIa_Ia BITSHIFT_RIGHT = new OperatorIaIa_Ia("bitshift-right") {
		public void apply( int vectorSize, int[] i1, int[] i2, int[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] >> i2[i];
		}
	};
	
	//// Arithmetic ////
	
	public static final OperatorDaDa_Da ADD = new OperatorDaDa_Da("add") {
		public void apply( int vectorSize, double[] i1, double[] i2, double[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] + i2[i];
		}
	};
	public static final OperatorDaDa_Da SUBTRACT = new OperatorDaDa_Da("subtract") {
		public void apply( int vectorSize, double[] i1, double[] i2, double[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] - i2[i];
		}
	};
	public static final OperatorDaDa_Da MULTIPLY = new OperatorDaDa_Da("multiply") {
		public void apply( int vectorSize, double[] i1, double[] i2, double[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] * i2[i];
		}
	};
	public static final OperatorDaDa_Da DIVIDE = new OperatorDaDa_Da("divide") {
		public void apply( int vectorSize, double[] i1, double[] i2, double[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = i1[i] / i2[i];
		}
	};
	public static final OperatorDaDa_Da EXPONENTIATE = new OperatorDaDa_Da("exponentiate") {
		public void apply( int vectorSize, double[] i1, double[] i2, double[] dest ) {
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = Math.pow(i1[i], i2[i]);
		}
	};
	
	//// Conversion ////
	
	public static final Operator<DVar,IVar,None,None> INT_TO_DOUBLE = new AbstractOperator<DVar,IVar,None,None>("integer-to-double") {
		@Override
		public void apply(Program.Instance pi, Instruction<DVar,IVar,None,None> inst, int vectorSize) {
			int[] src = pi.integerVectors[inst.v1.number];
			double[] dest = pi.doubleVectors[inst.dest.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = src[i];
		}
	};
	public static final Operator<IVar,IConst,None,None> LOAD_INT_CONST = new AbstractOperator<IVar,IConst,None,None>("load-integer-constant") {
		@Override
		public void apply(Program.Instance pi, Instruction<IVar,IConst,None,None> inst, int vectorSize) {
			int[] dest = pi.integerVectors[inst.dest.number];
			int constVal = pi.program.integerConstants[inst.v1.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = constVal;
		}
	};
	public static final Operator<DVar,IConst,None,None> LOAD_INT_CONST_AS_DOUBLE = new AbstractOperator<DVar,IConst,None,None>("load-integer-constant-as-double") {
		@Override
		public void apply(Program.Instance pi, Instruction<DVar,IConst,None,None> inst, int vectorSize) {
			double[] dest = pi.doubleVectors[inst.dest.number];
			double constVal = pi.program.integerConstants[inst.v1.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = constVal;
		}
	};
	public static final Operator<DVar,DConst,None,None> LOAD_DOUBLE_CONST = new AbstractOperator<DVar,DConst,None,None>("load-double-constant") {
		@Override
		public void apply(Program.Instance pi, Instruction<DVar,DConst,None,None> inst, int vectorSize) {
			double[] dest = pi.doubleVectors[inst.dest.number];
			double constVal = pi.program.doubleConstants[inst.v1.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = constVal;
		}
	};
	
	//// Selection ////
	
	public static final Operator<BVar,BVar,BVar,BVar> SELECT_BOOLEAN = new AbstractOperator<BVar,BVar,BVar,BVar>("select-boolean") {
		@Override
		public void apply(Instance pi, Instruction<BVar, BVar, BVar, BVar> inst, int vectorSize) {
			boolean[] dest = pi.booleanVectors[inst.dest.number];
			boolean[] s = pi.booleanVectors[inst.v1.number];
			boolean[] i0 = pi.booleanVectors[inst.v2.number];
			boolean[] i1 = pi.booleanVectors[inst.v3.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = s[i] ? i1[i] : i0[i];
		}
	};
	
	public static final Operator<IVar,BVar,IVar,IVar> SELECT_INTEGER = new AbstractOperator<IVar,BVar,IVar,IVar>("select-integer") {
		@Override
		public void apply(Instance pi, Instruction<IVar, BVar, IVar, IVar> inst, int vectorSize) {
			int[] dest = pi.integerVectors[inst.dest.number];
			boolean[] s = pi.booleanVectors[inst.v1.number];
			int[] i0 = pi.integerVectors[inst.v2.number];
			int[] i1 = pi.integerVectors[inst.v3.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = s[i] ? i1[i] : i0[i];
		}
	};
	
	public static final Operator<DVar,BVar,DVar,DVar> SELECT_DOUBLE = new AbstractOperator<DVar,BVar,DVar,DVar>("select-double") {
		@Override
		public void apply(Instance pi, Instruction<DVar, BVar, DVar, DVar> inst, int vectorSize) {
			double[] dest = pi.doubleVectors[inst.dest.number];
			boolean[] s = pi.booleanVectors[inst.v1.number];
			double[] i0 = pi.doubleVectors[inst.v2.number];
			double[] i1 = pi.doubleVectors[inst.v3.number];
			for( int i = vectorSize-1; i >= 0; --i ) dest[i] = s[i] ? i1[i] : i0[i];
		}
	};
	
	// TODO: (FAST)SINE, MOD
}
