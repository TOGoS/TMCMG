package togos.minecraft.mapgen;

import togos.lang.BaseSourceLocation;
import togos.minecraft.mapgen.world.gen.MinecraftWorldGenerator;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.BoundArgumentList;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.runtime.Function;
import togos.lang.RuntimeError;

public class GeneratorDefinitionFunctions
{
	static final BaseSourceLocation BUILTIN_LOC = new BaseSourceLocation( GeneratorDefinitionFunctions.class.getName()+".java", 0, 0);
	protected static <V> Binding<V> builtinBinding( V v ) {
		return new Binding.Constant<V>( v, BUILTIN_LOC );
	}
	
	static class LayerDef {
		final Function<Number> blockType;
		final Function<Number> floorHeight;
		final Function<Number> ceilingHeight;
		
		public LayerDef( Function<Number> blockType, Function<Number> floorHeight, Function<Number> ceilingHeight ) {
			this.blockType = blockType;
			this.floorHeight = floorHeight;
			this.ceilingHeight = ceilingHeight;
		}
		
		protected static Function<Number> toFunc( Binding<?> b ) {
			
		}
		
		public LayerDef( Binding<?> blockType, Binding<?> floorHeight, Binding<?> ceilingHeight ) throws Exception {
			this( toFunc(blockType), toFunc(floorHeight), toFunc(ceilingHeight) );
		}
	}
	
	public static final Context CONTEXT = new Context();
	static {
		CONTEXT.put("layer", builtinBinding(new Function<LayerDef>() {
			@Override
            public Binding<LayerDef> apply( BoundArgumentList input ) throws Exception {
				if( input.arguments.size() != 3 ) throw new RuntimeError(
					"'layer' requires exactly 3 arguments, but "+input.arguments.size()+" given", input.sLoc);
				for( BoundArgumentList.BoundArgument<?> arg : input.arguments ) {
					if( !arg.name.isEmpty() ) throw new RuntimeError(
						"'layer' takes no named arguments, but got '"+arg.name+"'", input.sLoc );
				}
				return new Binding.Constant<LayerDef>( new LayerDef(
					input.arguments.get(0).value,
					input.arguments.get(1).value,
					input.arguments.get(2).value
				), input.sLoc);
            }
		}));
		CONTEXT.put("layered-terrain", builtinBinding(new Function<MinecraftWorldGenerator>() {
			@Override
            public Binding<MinecraftWorldGenerator> apply( BoundArgumentList input ) throws Exception {
				for( BoundArgumentList.BoundArgument<?> arg : input.arguments ) {
					Object v = arg.value.getValue();
				}
            }
		}));
	}
}
