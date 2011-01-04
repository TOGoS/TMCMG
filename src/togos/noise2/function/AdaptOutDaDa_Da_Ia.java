package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDa;
import togos.noise2.data.DataIa;


public class AdaptOutDaDa_Da_Ia implements FunctionDaDa_Ia
{
	FunctionDaDa_Da next;
	public AdaptOutDaDa_Da_Ia( FunctionDaDa_Da next ) {
		this.next = next;
	}
	
	public DataIa apply( DataDaDa in ) {
		int[] out = new int[in.getLength()];
		DataDa d = next.apply(in);
		for( int i=d.getLength()-1; i>=0; --i ) {
			out[i] = (int)Math.floor(d.v[i]);
		}
		return new DataIa(out);
	}
}
