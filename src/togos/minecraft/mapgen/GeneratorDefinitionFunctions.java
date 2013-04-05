package togos.minecraft.mapgen;

import java.util.ArrayList;

import togos.lang.BaseSourceLocation;
import togos.lang.CompileError;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.LayeredTerrainFunction;
import togos.minecraft.mapgen.world.gen.MinecraftWorldGenerator;
import togos.noise.v1.func.LFunctionDaDaDa_Ia;
import togos.noise.v1.func.LFunctionDaDa_Da;
import togos.noise.v3.functions.MathFunctions;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.BoundArgumentList;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.runtime.Function;

public class GeneratorDefinitionFunctions
{
	static final BaseSourceLocation BUILTIN_LOC = new BaseSourceLocation( GeneratorDefinitionFunctions.class.getName()+".java", 0, 0);
	protected static <V> Binding<? extends V> builtinBinding( V v ) {
		return Binding.forValue( v, BUILTIN_LOC );
	}
	
	protected static <V> Function<V> toFunc( Binding<?> b, Class<V> vClass ) throws CompileError {
		Object v;
        try {
	        v = b.getValue();
        } catch( Exception e ) {
        	throw new CompileError(e, b.sLoc);
        }
		if( v instanceof Function ) {
			return (Function<V>)v;
		} else if( vClass.isAssignableFrom(v.getClass()) ) {
			return new MathFunctions.ConstantBindingFunction<V>( (Binding<? extends V>)b );
		} else {
			throw new CompileError("Can't convert "+v.getClass()+" to a number function", b.sLoc);
		}
	}
	
	protected static final <V> V applyFunction( Function<V> func, Object...argValues ) throws Exception {
		BoundArgumentList bal = new BoundArgumentList(BUILTIN_LOC, BUILTIN_LOC);
		for( Object argValue : argValues ) {
			bal.add( "", Binding.forValue(argValue, BUILTIN_LOC), BUILTIN_LOC );
		}
		return func.apply(bal).getValue();
	}
	
	static class FunctionAdapterDaDaDa_Ia implements LFunctionDaDaDa_Ia {
		Function<? extends Number> sFunc;
		public FunctionAdapterDaDaDa_Ia( Function<? extends Number> sFunc ) {
			this.sFunc = sFunc;
		}
		
		@Override
		public void apply(int vectorSize, double[] x, double[] y, double[] z, int[] dest) {
			try {
				for( int i=vectorSize-1; i>=0; --i ) {
					dest[i] = applyFunction(sFunc, x[i], y[i], z[i]).intValue();
				}
			} catch( Exception e ) {
				throw new RuntimeException(e);
			}
		}
	}
	
	static class FunctionAdapterDaDa_Da implements LFunctionDaDa_Da {
		Function<? extends Number> sFunc;
		public FunctionAdapterDaDa_Da( Function<? extends Number> sFunc ) {
			this.sFunc = sFunc;
		}
		
		@Override
		public void apply(int vectorSize, double[] x, double[] y, double[] dest) {
			try {
				for( int i=vectorSize-1; i>=0; --i ) {
					dest[i] = applyFunction(sFunc, x[i], y[i]).intValue();
				}
			} catch( Exception e ) {
				throw new RuntimeException(e);
			}
		}
	}
	
	static class WorldGeneratorDefinition implements MinecraftWorldGenerator
	{
		ArrayList<LayerDefinition> layerDefs = new ArrayList<LayerDefinition>();
		Function<Number> biomeFunction = new MathFunctions.ConstantBindingFunction<Number>( Binding.forValue(0, BUILTIN_LOC) );
		
		@Override
        public LayeredTerrainFunction getTerrainFunction() {
			return new LayeredTerrainFunction() {
				@Override
                public TerrainBuffer apply( int vectorSize, double[] x, double[] z, TerrainBuffer buffer ) {
					try {
						buffer = TerrainBuffer.getInstance( buffer, vectorSize, layerDefs.size() );
		                for( int i=vectorSize-1; i>=0; --i ) {
		                	buffer.biomeData[i] = applyFunction( biomeFunction, x[i], z[i] ).intValue();
		                }
		                for( int i=0; i<layerDefs.size(); ++i ) {
		                	FunctionAdapterDaDa_Da floorHeightFunction = new FunctionAdapterDaDa_Da( layerDefs.get(i).floorHeight );
		                	FunctionAdapterDaDa_Da ceilingHeightFunction = new FunctionAdapterDaDa_Da( layerDefs.get(i).ceilingHeight );
		                	floorHeightFunction.apply( vectorSize, x, z, buffer.layerData[i].floorHeight );
		                	ceilingHeightFunction.apply( vectorSize, x, z, buffer.layerData[i].ceilingHeight );
		                	buffer.layerData[i].blockTypeFunction = new FunctionAdapterDaDaDa_Ia( layerDefs.get(i).blockType );
		                }
		                return buffer;
					} catch( Exception e ) {
						throw new RuntimeException(e);
					}
                }
			};
        }
		
		@Override
        public ChunkMunger getChunkMunger() {
	        // TODO Auto-generated method stub
	        return null;
        }
	}
	
	static class LayerDefinition
	{
		final Function<Number> blockType;
		final Function<Number> floorHeight;
		final Function<Number> ceilingHeight;
		
		public LayerDefinition( Function<Number> blockType, Function<Number> floorHeight, Function<Number> ceilingHeight ) {
			this.blockType = blockType;
			this.floorHeight = floorHeight;
			this.ceilingHeight = ceilingHeight;
		}
		
		public LayerDefinition( Binding<?> blockType, Binding<?> floorHeight, Binding<?> ceilingHeight ) throws CompileError {
			this( toFunc(blockType, Number.class), toFunc(floorHeight, Number.class), toFunc(ceilingHeight, Number.class) );
		}
	}
	
	public static final Context CONTEXT = new Context();
	static {
		CONTEXT.put("layer", builtinBinding(new Function<LayerDefinition>() {
			@Override
            public Binding<LayerDefinition> apply( BoundArgumentList input ) throws CompileError {
				if( input.arguments.size() != 3 ) throw new CompileError(
					"'layer' requires exactly 3 arguments, but "+input.arguments.size()+" given", input.argListLocation);
				for( BoundArgumentList.BoundArgument<?> arg : input.arguments ) {
					if( !arg.name.isEmpty() ) throw new CompileError(
						"'layer' takes no named arguments, but got '"+arg.name+"'", input.argListLocation );
				}
				return Binding.forValue( new LayerDefinition(
					input.arguments.get(0).value,
					input.arguments.get(1).value,
					input.arguments.get(2).value
				), LayerDefinition.class, input.callLocation );
            }
		}));
		CONTEXT.put("layered-terrain", builtinBinding(new Function<WorldGeneratorDefinition>() {
			@Override
            public Binding<WorldGeneratorDefinition> apply( BoundArgumentList input ) throws CompileError {
				WorldGeneratorDefinition wgd = new WorldGeneratorDefinition();
				for( BoundArgumentList.BoundArgument<?> arg : input.arguments ) {
					Object v;
					try {
						v = arg.value.getValue();
					} catch( CompileError e ) {
						throw e;
					} catch( Exception e ) {
						throw new RuntimeException( e );
					}
					if( "biome".equals(arg.name) ) {
						wgd.biomeFunction = toFunc( arg.value, Number.class );
					} else if( "".equals(arg.name) && v instanceof LayerDefinition ) {
						wgd.layerDefs.add( (LayerDefinition)v );
					} else {
						String argName = arg.name.length() == 0 ? " " : " '"+arg.name+"' "; 
						throw new CompileError("Don't know how to handle"+argName+"argument with value: "+v, arg.sLoc);
					}
				}
				return Binding.forValue(wgd, WorldGeneratorDefinition.class, input.argListLocation);
            }
		}));
	}
}
