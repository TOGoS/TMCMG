package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDa;
import togos.noise2.data.DataDaDaDa;

public class AdaptInDaDa_DaDaDa_Da implements FunctionDaDa_Da
{
	FunctionDaDa_Da z;
	FunctionDaDaDa_Da next;
	public AdaptInDaDa_DaDaDa_Da( FunctionDaDa_Da z, FunctionDaDaDa_Da next ) {
		this.z = z;
		this.next = next;
	}
	public AdaptInDaDa_DaDaDa_Da( TNLFunctionDaDaDa_Da next ) {
		this( Constant_Da.ZERO, next );
	}
	
	public DataDa apply( DataDaDa in ) {
		DataDa z = this.z.apply(in);
		return next.apply(new DataDaDaDa(in.x, in.y, z.v));
	}
}
