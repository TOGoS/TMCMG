package togos.noise2.vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import togos.noise2.lang.BaseSourceLocation;
import togos.noise2.lang.ScriptError;
import togos.noise2.vm.dftree.func.LFunctionDaDaDa_Da;
import togos.noise2.vm.dftree.lang.TNLCompiler;
import togos.noise2.vm.dftree.lang.macro.NoiseMacros;

public class VMArithmeticPerformanceTest
{
	int outerIterations = 10;
	int innerIterations = 1000;
	int vectorSize = 256;
	
	public Map scripts = new HashMap();
	public Map compilers = new HashMap();
	public Map compileTimes = new HashMap();
	public Map runTimes = new HashMap();
	
	double[] x, y, z, dest;
	
	protected void add( Map times, String key, long amount ) {
		Long value = (Long)times.get(key);
		amount += value == null ? 0 : value.longValue();
		times.put(key, new Long(amount));
	}
	
	protected long timerStartTime;
	protected void startTimer() {
		timerStartTime = System.currentTimeMillis();
	}
	protected long stopTimer() {
		return System.currentTimeMillis() - timerStartTime;
	}
	
	protected void run( String scriptName, String script, String compilerName, Compiler c ) {
		startTimer();
		LFunctionDaDaDa_Da fun;
		try {
			fun = (LFunctionDaDaDa_Da)c.compile( script, new BaseSourceLocation(scriptName,1,1), null, LFunctionDaDaDa_Da.class );
		} catch( ScriptError e ) {
			throw new RuntimeException(e);
		}
		add( compileTimes, compilerName + "/" + scriptName, stopTimer() );
		
		startTimer();
		for( int i=0; i<innerIterations; ++i ) {
			fun.apply( vectorSize, x, y, z, dest );
		}
		add( runTimes, compilerName + "/" + scriptName, stopTimer() );
	}
	
	public void run() {
		x = new double[vectorSize];
		y = new double[vectorSize];
		z = new double[vectorSize];
		dest = new double[vectorSize];
		for( int i=0; i<vectorSize; ++i ) {
			x[i] = Math.random()*1024*1024;
			y[i] = Math.random()*1024*1024;
			z[i] = Math.random()*1024*1024;
		}
		for( int i=0; i<outerIterations; ++i ) {
			for( Iterator si=scripts.entrySet().iterator(); si.hasNext(); ) {
				Map.Entry se = (Map.Entry)si.next();
				for( Iterator ci=compilers.entrySet().iterator(); ci.hasNext(); ) {
					Map.Entry ce = (Map.Entry)ci.next();
					run( (String)se.getKey(), (String)se.getValue(), (String)ce.getKey(), (Compiler)ce.getValue() );
				}
			}
		}
	}
	
	public static String padLeft( String wrd, int places ) {
		while( wrd.length() < places ) wrd = " " + wrd;
		return wrd.substring(0,places);
	}
	
	public static String padRight( String wrd, int places ) {
		while( wrd.length() < places ) wrd += " ";
		return wrd.substring(0,places);
	}
	
	public static void main(String[] args) {
		TNLCompiler dftreeCompiler = new TNLCompiler();
		dftreeCompiler.macroTypes.putAll( NoiseMacros.stdNoiseMacros );
		
		VMArithmeticPerformanceTest t = new VMArithmeticPerformanceTest();
		
		t.compilers.put("dftree", dftreeCompiler);
		
		t.scripts.put("arithmetic", "(x * y) + (y * z) - (x / z)");
		
		t.run();
		ArrayList runs = new ArrayList(t.compileTimes.keySet());
		Collections.sort(runs);
		
		System.out.println(
			padRight("Script",14)+
			padRight("VM",14)+
			padLeft("Compile Time",14)+
			padLeft("Run time",14)
		);
		
		for( Iterator i=runs.iterator(); i.hasNext(); ) {
			String run = (String)i.next();
			String[] k = run.split("/");
			long compileTime = ((Long)t.compileTimes.get(run)).longValue();
			long runTime     = ((Long)t.runTimes.get(run)).longValue();
			System.out.println(
				padRight(k[1],14)+
				padRight(k[0],14)+
				padLeft(compileTime+"ms",14)+
				padLeft(runTime+"ms",14)
			);
		}
	}
}
