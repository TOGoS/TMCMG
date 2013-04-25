import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StringSplitTest
{
	protected static final String toSplit = "hello+123+456+I+am+awesome+oh+yeah+wuiahdicuasnxcuiewncnkrecrekcuj";
	protected static final String toSplit2 = toSplit + "+" + toSplit + "+" + toSplit + "+" + toSplit;
	protected static final String toSplit3 = toSplit2 + "+" + toSplit2 + "+" + toSplit2 + "+" + toSplit2;
	protected static final String toSplit4 = toSplit3 + "+" + toSplit3 + "+" + toSplit3 + "+" + toSplit3;
	protected static final String toSplit5 = toSplit4 + "+" + toSplit4 + "+" + toSplit4 + "+" + toSplit4;
	protected static final byte[] toSplit6 = toSplit5.getBytes();
	
	class Spliterator implements Iterator<String> {
		final char delim;
		final String s;
		final int len;
		int index;
		
		public Spliterator( char delim, String s, int index ) {
			this.delim = delim;
			this.s = s;
			this.index = index;
			this.len = s.length();
		}
		
		public Spliterator( char delim, String s ) {
			this( delim, s, 0 );
		}
		
		public final boolean hasNext() {
			return index < len;
		}
		
		public final String next() {
			int end = s.indexOf( delim, index );
			if( end == -1 ) end = len;
			String r = s.substring(index,end);
			index = end+1;
			return r;
		}

		public void remove() {
			throw new UnsupportedOperationException();
        }
	}
	
	protected final String[] fastSplitToArray( final char delim, final String s ) {
		final int len = s.length();
		
		int count = 0;
		for( int end = 0; end < len; ) {
			end = s.indexOf(delim,end);
			end = (end == -1) ? len : end+1;
			++count;
		}
		
		String[] res = new String[count+1];
		
		//ArrayList splut = new ArrayList();
		for( int c=0, begin = 0; begin < len; ) {
			int end = s.indexOf(delim,begin);
			if( end == -1 ) end = len;
			res[c++] = s.substring(begin,end);
			begin = end+1;
		}
		return res;
	}
	
	protected final List<String> fastSplitToList( final char delim, final String s ) {
		final int len = s.length();
		
		ArrayList<String> splut = new ArrayList<String>();
		for( int begin = 0; begin < len; ) {
			int end = s.indexOf(delim,begin);
			if( end == -1 ) end = len;
			splut.add( s.substring(begin,end) );
			begin = end+1;
		}
		return splut;
	}
	
	final int outerIterations = 200;
	final int innerIterations = 200;
	
	protected void runSplit() {
		for( int i=0; i<innerIterations; ++i ) {
			toSplit5.split("\\+");
		}
	}
	
	protected void runFastSplitToArray() {
		for( int i=0; i<innerIterations; ++i ) {
			fastSplitToArray('+',toSplit5);
		}
	}
	
	protected void runFastSplitToList() {
		for( int i=0; i<innerIterations; ++i ) {
			fastSplitToList('+',toSplit5);
		}
	}
	
	protected void runSpliterator() {
		for( int j=0; j<innerIterations; ++j ) {
			for( Spliterator i = new Spliterator('+',toSplit5); i.hasNext(); ) {
				String s = (String)i.next();
				eaten += s.length();
			}
		}
	}
	
	protected void runInline() {
		for( int j=0; j<innerIterations; ++j ) {
			final int len = toSplit5.length();
			for( int index = 0; index < len; ) {
				int end = toSplit5.indexOf('+',index);
				if( end == -1 ) end = len;
				String s = toSplit5.substring(index,end);
				eaten += s.length();
				index = end+1;
			}
		}
	}
	
	protected void runByteSplit() {
		for( int j=0; j<innerIterations; ++j ) {
			final int len = toSplit6.length;
			
			int begin = 0;
			for( int i=0; i<len; ++i ) {
				if( toSplit6[i] == '+' ) {
					//eaten += new SimpleByteChunk(toSplit6,begin,i).length;
					eaten += (i-begin);
					begin = i+1;
				}
			}
			//eaten += new SimpleByteChunk(toSplit6,begin,len).length;
			eaten += (len-begin);
		}
	}
	
	public long splitTime = 0;
	public long fastSplitATime = 0;
	public long fastSplitLTime = 0;
	public long spliteratorTime = 0;
	public long inlineTime = 0;
	public long byteSplitTime = 0;
	
	public int eaten;
	
	protected void run() {
		long begin, end;
		
		for( int i=0; i<outerIterations; ++i ) {
			begin = System.currentTimeMillis();
			runSplit();
			end   = System.currentTimeMillis();
			splitTime += (end - begin);
			
			begin = System.currentTimeMillis();
			runFastSplitToList();
			end   = System.currentTimeMillis();
			fastSplitATime += (end - begin);
			
			begin = System.currentTimeMillis();
			runFastSplitToArray();
			end   = System.currentTimeMillis();
			fastSplitLTime += (end - begin);

			begin = System.currentTimeMillis();
			runSpliterator();
			end   = System.currentTimeMillis();
			spliteratorTime += (end - begin);
			
			begin = System.currentTimeMillis();
			runInline();
			end   = System.currentTimeMillis();
			inlineTime += (end - begin);
			
			begin = System.currentTimeMillis();
			runByteSplit();
			end   = System.currentTimeMillis();
			byteSplitTime += (end - begin);
		}
	}
	
	public static String padLeft( String wrd, int places ) {
		while( wrd.length() < places ) wrd = " " + wrd;
		return wrd.substring(0,places);
	}
	
	public static void main(String[] args) {
		StringSplitTest sst = new StringSplitTest();
		sst.run();
		System.err.println( "Split       : "+padLeft(""+sst.splitTime,10)+"ms");
		System.err.println( "FastSplit A : "+padLeft(""+sst.fastSplitATime,10)+"ms");
		System.err.println( "FastSplit L : "+padLeft(""+sst.fastSplitLTime,10)+"ms");
		System.err.println( "Spliterator : "+padLeft(""+sst.spliteratorTime,10)+"ms");
		System.err.println( "Inline      : "+padLeft(""+sst.inlineTime,10)+"ms");
		System.err.println( "ByteSplit   : "+padLeft(""+sst.byteSplitTime,10)+"ms");
		System.err.println();
		System.err.println( "a number    : "+sst.eaten );
	}
}
