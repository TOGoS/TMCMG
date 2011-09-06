package togos.mf.io.osc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import togos.mf.io.PacketReader;
import togos.mf.io.PacketWriter;

public class OSC {
	//// Extra types defined by OSC but that don't have standard Java counterparts ////
	
	static final class Infinitum {
		public static final Infinitum INSTANCE = new Infinitum();
		private Infinitum() {}
	}
	
	static class Symbol {
		String value;
		public Symbol( String value ) {
			this.value = value;
		}
		public String toString() {  return value;  }
		
		public boolean equals(Object o) {
			if( !(o instanceof Symbol) ) return false;
			return value.equals(((Symbol)o).value);
		}
	}
	
	static class MidiMessage {
		int data;
		
		public MidiMessage( int data ) {
			this.data = data;
		}
		public MidiMessage( int portId, int status, int data1, int data2 ) {
			this( ((portId&0xFF)<<24)|((status&0xFF)<<16)|((data1&0xFF)<<8)|((data2&0xFF)<<0) ); 
		}
		
		// TODO: are these values signed?
		public int getDataAsInt() {  return data;  }
		public byte[] getData() {
			return new byte[] {
					(byte)((data>>24)&0xFF),
					(byte)((data>>16)&0xFF),
					(byte)((data>> 8)&0xFF),
					(byte)((data>> 0)&0xFF)
			};
		}
		public int getPortId() {  return (data>>24)&0xFF;  }
		public int getStatus() {  return (data>>16)&0xFF;  }
		public int getData1()  {  return (data>> 8)&0xFF;  }
		public int getData2()  {  return (data>> 0)&0xFF;  }

		public boolean equals( Object o ) {
			if( !(o instanceof MidiMessage) ) return false;
			return data == ((MidiMessage)o).data;
		}
	}
	
	static class RGBA {
		int rgba;
		
		public RGBA( int rgba ) {
			this.rgba = rgba;
		}
		public RGBA( int r, int g, int b, int a ) {
			this.rgba =
				((r&0xFF)<<24) | ((g&0xFF)<<16) |
				((b&0xFF)<< 8) | ((a&0xFF)<< 0);
		}
		
		public int getRed()   {  return (rgba>>24)&0xFF;  }
		public int getGreen() {  return (rgba>>16)&0xFF;  }
		public int getBlue()  {  return (rgba>> 8)&0xFF;  }
		public int getAlpha() {  return (rgba>> 0)&0xFF;  }
		
		public int getRGBA() {
			return rgba;
		}
		public int getARGB() {
			return ((rgba << 24)&0xFF000000) | ((rgba >> 8) | 0x00FFFFFF);
		}
		
		public boolean equals( Object o ) {
			if( !(o instanceof RGBA) ) return false;
			return rgba == ((RGBA)o).rgba;
		}
	}

	//// Message classes ////
	
	public interface Message {
		public byte[] getData();
		public String getPath();
		public List getArguments();
	}
	
	public static class OutMessage implements Message {
		protected byte[] data = null;
		protected String path;
		protected List arguments;
		protected boolean argumentsReadOnly;
		
		public OutMessage( String path, List arguments ) {
			this.path = path;
			this.arguments = arguments;
			this.argumentsReadOnly = true;
		}

		public OutMessage addArgument( Object arg ) {
			if( this.data != null ) throw new RuntimeException("Can't modify OutMessage - data already generated");
			if( argumentsReadOnly ) {
				arguments = new ArrayList(arguments);
				argumentsReadOnly = false;
			}
			arguments.add( arg );
			return this;
		}
		
		//
		
		public String getPath() {
			return path;
		}
		public List getArguments() {
			return arguments;
		}
		public byte[] getData() {
			if( this.data == null ) {
				this.data = encodeMessage(path, arguments);
			}
			return this.data;
		}
	}
	
	//// Stream interfaces ////
	
	public static class MessageReader {
		PacketReader packetReader;
		public MessageReader( PacketReader packetReader ) {
			this.packetReader = packetReader;
		}
		public Message readOscMessage() throws IOException {
			byte[] packet = packetReader.readPacket(4096);
			return decodeMessage(packet);
		}
	}
	
	public static class MessageWriter {
		PacketWriter packetWriter;
		public MessageWriter( PacketWriter packetWriter ) {
			this.packetWriter = packetWriter;
		}
		public void writeOscMessage( String path, List arguments ) throws IOException {
			byte[] packet = encodeMessage( path, arguments );
			packetWriter.writePacket( packet, packet.length );
		}
	}
	
	protected static class ParsePosition {
		int pos;
	}

	protected static class StringParsePosition extends ParsePosition {
		String str;
		public StringParsePosition( String str, int pos ) {
			this.str = str;
			this.pos = pos;
		}
		public boolean hasCurrent() {
			return pos < str.length();
		}
		public char current() {
			return str.charAt(pos);
		}
		public char next() {
			return str.charAt(pos++);
		}
	}
	
	protected static class ByteArrayParsePosition extends ParsePosition {
		byte[] data;
	}
	
	public static class InMessage extends ByteArrayParsePosition implements Message {
		String path = null;
		String typeTag = null;
		List arguments = null;
		public InMessage( byte[] data ) {
			this.data = data;
			this.pos = 0;
		}
		
		public String getPath() {
			if( this.path == null ) {
				path = decodeString(this);
			}
			return this.path;
		}
		protected String getTypeTag() {
			if( typeTag == null ) {
				getPath(); // Make sure path has been read
				typeTag = decodeString(this);
			}
			return typeTag;
		}
		public List getArguments() {
			if( arguments == null ) {
				arguments = decodeList( new StringParsePosition( getTypeTag(), 1 ), this );
			}
			return arguments;
		}
		public byte[] getData() {
			return data;
		}
	}
	
	// Utility functions

	protected static byte[] stringToBytes( String s ) {
		try {
			return s.getBytes("UTF-8");
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e); // Never happens
		}
	}
	
	protected static String bytesToString( byte[] data, int begin, int length ) {
		try {
			return new String( data, begin, length, "UTF-8");
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
		                  	
	protected static int roundUp( int l ) {
		while( l%4 != 0 ) ++l;
		return l;
	}
	
	// Encode functions
	
	protected static void encodeInt32( int i, byte[] buf, int offset ) {
		buf[offset++] = (byte)((i>>24)&0xFF);
		buf[offset++] = (byte)((i>>16)&0xFF);
		buf[offset++] = (byte)((i>> 8)&0xFF);
		buf[offset++] = (byte)((i>> 0)&0xFF);
	}
	
	protected static void encodeInt64( long i, byte[] buf, int offset ) {
		buf[offset++] = (byte)((i>>56)&0xFF);
		buf[offset++] = (byte)((i>>48)&0xFF);
		buf[offset++] = (byte)((i>>40)&0xFF);
		buf[offset++] = (byte)((i>>32)&0xFF);
		buf[offset++] = (byte)((i>>24)&0xFF);
		buf[offset++] = (byte)((i>>16)&0xFF);
		buf[offset++] = (byte)((i>> 8)&0xFF);
		buf[offset++] = (byte)((i>> 0)&0xFF);
	}
	
	protected static int getEncodedLength( Object o ) {
		if( o == null ) {
			return 0;
		} else if( o instanceof Integer ) {
			return 4;
		} else if( o instanceof Float ) {
			return 4;
		} else if( o instanceof Character ) {
			return 4;
		} else if( o instanceof Long ) {
			return 8;
		} else if( o instanceof Double ) {
			return 8;
		} else if( o instanceof RGBA ) { 
			return 4;
		} else if( o instanceof MidiMessage ) {
			return 4;
		} else if( o instanceof Boolean ) {
			return 0;
		} else if( o instanceof Infinitum ) {
			return 0;
		} else if( o instanceof String ) {
			return roundUp(stringToBytes((String)o).length+1);
		} else if( o instanceof Symbol ) {
			return roundUp(stringToBytes(((Symbol)o).value).length+1);
		} else if( o instanceof byte[] ) {
			return roundUp(((byte[])o).length)+4;
		} else if( o instanceof List ) {
			int l=0;
			for( Iterator i=((List)o).iterator(); i.hasNext(); ) {
				l += getEncodedLength(i.next());
			}
			return l;
		} else if( o instanceof Map ) {
			int l=0;
			for( Iterator i=((Map)o).entrySet().iterator(); i.hasNext(); ) {
				Map.Entry e = (Map.Entry)i.next();
				l += getEncodedLength(e.getKey());
				l += getEncodedLength(e.getValue());
			}
			return l;
		} else {
			throw new RuntimeException("Don't know how to encode "+o.getClass().getName());
		}
	}
	
	protected static void encodeType( Object o, StringBuffer sb ) {
		if( o == null ) {
			sb.append("N");
		} else if( o instanceof Integer ) {
			sb.append("i");
		} else if( o instanceof Float ) {
			sb.append("f");
		} else if( o instanceof Character ) {
			sb.append("c");
		} else if( o instanceof Long ) {
			sb.append("l");
		} else if( o instanceof Double ) {
			sb.append("d");
		} else if( o instanceof RGBA ) { 
			sb.append("r");
		} else if( o instanceof MidiMessage ) {
			sb.append("m");
		} else if( o instanceof Boolean ) {
			sb.append( ((Boolean)o).booleanValue() ? "T" : "F" );
		} else if( o instanceof Infinitum ) {
			sb.append("I");
		} else if( o instanceof String ) {
			sb.append("s");
		} else if( o instanceof Symbol ) {
			sb.append("S");
		} else if( o instanceof byte[] ) {
			sb.append("b");
		} else if( o instanceof List ) {
			sb.append("[");
			for( Iterator i=((List)o).iterator(); i.hasNext(); ) {
				encodeType(i.next(), sb);
			}
			sb.append("]");
		} else if( o instanceof Map ) {
			sb.append("(");
			for( Iterator i=((Map)o).entrySet().iterator(); i.hasNext(); ) {
				Map.Entry e = (Map.Entry)i.next();
				encodeType(e.getKey(), sb);
				encodeType(e.getValue(), sb);
			}
			sb.append(")");
		} else {
			throw new RuntimeException("Don't know how to encode "+o.getClass().getName());
		}
	}
	
	protected static int encodeString( String s, byte[] buf, int offset ) {
		byte[] strBytes = stringToBytes(s);
		int i;
		for( i=0; i<strBytes.length; ++i ) {
			buf[offset+i] = strBytes[i];
		}
		do { buf[offset+(i++)] = 0; } while( i%4 != 0 );
		return offset+i;
	}
	
	protected static int encode( Object o, byte[] buf, int offset ) {
		if( o == null ) {
			return offset;
		} else if( o instanceof Integer ) {
			encodeInt32( ((Integer)o).intValue(), buf, offset );
			return offset+4;
		} else if( o instanceof Float ) {
			encodeInt32( Float.floatToIntBits( ((Float)o).floatValue() ), buf, offset );
			return offset+4;
		} else if( o instanceof Character ) {
			encodeInt32( (int)((Character)o).charValue(), buf, offset );
			return offset+4;
		} else if( o instanceof Long ) {
			encodeInt64( ((Long)o).longValue(), buf, offset );
			return offset+8;
		} else if( o instanceof Double ) {
			encodeInt64( Double.doubleToLongBits(((Double)o).doubleValue()), buf, offset );
			return offset+8;
		} else if( o instanceof RGBA ) {
			encodeInt32( ((RGBA)o).rgba, buf, offset );
			return offset+4;
		} else if( o instanceof MidiMessage ) {
			encodeInt32( ((MidiMessage)o).data, buf, offset );
			return offset+4;
		} else if( o instanceof Symbol ) {
			return encodeString( ((Symbol)o).value, buf, offset );
		} else if( o instanceof Boolean ) {
			return offset;
		} else if( o instanceof Infinitum ) {
			return offset;
		} else if( o instanceof String ) {
			return encodeString( (String)o, buf, offset );
		} else if( o instanceof Symbol ) {
			return encodeString( ((Symbol)o).value, buf, offset );
		} else if( o instanceof byte[] ) {
			byte[] dat = (byte[])o;
			int i;
			encodeInt32( dat.length, buf, offset );
			offset += 4;
			for( i=0; i<dat.length; ++i ) {
				buf[offset+i] = dat[i];
			}
			while( i%4 != 0 ) { buf[offset+(i++)] = 0; }
			return offset+i;
		} else if( o instanceof List ) {
			for( Iterator i=((List)o).iterator(); i.hasNext(); ) {
				offset = encode(i.next(), buf, offset);
			}
			return offset;
		} else if( o instanceof Map ) {
			for( Iterator i=((Map)o).entrySet().iterator(); i.hasNext(); ) {
				Map.Entry e = (Map.Entry)i.next();
				offset = encode(e.getKey(), buf, offset);
				offset = encode(e.getValue(), buf, offset);
			}
			return offset;
		} else {
			throw new RuntimeException("Don't know how to encode "+(o == null ? "null" : o.getClass().getName()));
		}
	}
	
	protected static String encodeTypeTag( List arguments ) {
		StringBuffer s = new StringBuffer(",");
		for( Iterator i=arguments.iterator(); i.hasNext(); ) {
			encodeType( i.next(), s );
		}
		return s.toString();
	}
	
	public static byte[] encodeMessage( String path, List arguments ) {
		String typeTag = encodeTypeTag( arguments );
		byte[] buf = new byte[getEncodedLength(path)+getEncodedLength(typeTag)+getEncodedLength(arguments)];
		int offset = 0;
		offset = encode( path, buf, offset );
		offset = encode( typeTag, buf, offset );
		offset = encode( arguments, buf, offset );
		return buf;
	}
	
	public static OutMessage createMessage( String path, List arguments ) {
		return new OutMessage( path, arguments );
	}
	
	public static OutMessage createMessage( String path ) {
		return new OutMessage( path, Collections.EMPTY_LIST );
	}
	
	// Decode functions
	
	protected static int decodeInt32( byte[] buf, int offset ) {
		return
			((buf[  offset]&0xFF) << 24) |
			((buf[++offset]&0xFF) << 16) | 
			((buf[++offset]&0xFF) <<  8) |
			((buf[++offset]&0xFF) <<  0);
	}
	
	protected static long decodeInt64( byte[] buf, int offset ) {
		return
			((long)(buf[  offset]&0xFF) << 56) |
			((long)(buf[++offset]&0xFF) << 48) | 
			((long)(buf[++offset]&0xFF) << 40) |
			((long)(buf[++offset]&0xFF) << 32) |
			((long)(buf[++offset]&0xFF) << 24) |
			((long)(buf[++offset]&0xFF) << 16) | 
			((long)(buf[++offset]&0xFF) <<  8) |
			((long)(buf[++offset]&0xFF) <<  0);
	}
	
	protected static int decodeInt32( ByteArrayParsePosition p ) {
		int i = decodeInt32( p.data, p.pos );
		p.pos += 4;
		return i;
	}
	
	protected static Integer decodeInteger( ByteArrayParsePosition p ) {
		return new Integer(decodeInt32(p));
	}
	
	protected static Float decodeFloat( ByteArrayParsePosition p ) {
		int i = decodeInt32( p.data, p.pos );
		p.pos += 4;
		return new Float(Float.intBitsToFloat(i));
	}
	
	protected static Character decodeCharacter( ByteArrayParsePosition p ) {
		return new Character((char)decodeInt32(p));
	}
	
	protected static Long decodeLong( ByteArrayParsePosition p ) {
		long l = decodeInt64( p.data, p.pos );
		p.pos += 8;
		return new Long(l);
	}
	
	protected static Double decodeDouble( ByteArrayParsePosition p ) {
		long l = decodeInt64( p.data, p.pos );
		p.pos += 8;
		return new Double(Double.longBitsToDouble(l));
	}
	
	protected static RGBA decodeRgbaMessage( ByteArrayParsePosition p ) {
		return new RGBA( decodeInt32(p) );
	}

	protected static MidiMessage decodeMidiMessage( ByteArrayParsePosition p ) {
		return new MidiMessage( decodeInt32(p) );
	}
	
	protected static String decodeString( ByteArrayParsePosition p ) {
		int begin = p.pos;
		while( p.pos < p.data.length && p.data[p.pos] != 0 ) {
			++p.pos;
		}
		int len = p.pos-begin;
		p.pos = roundUp(p.pos+1);
		return bytesToString( p.data, begin, len );
	}
	
	protected static Symbol decodeSymbol( ByteArrayParsePosition p ) {
		return new Symbol(decodeString(p));
	}
	
	protected static byte[] decodeBlob( ByteArrayParsePosition p ) {
		int length = decodeInt32(p.data, p.pos);
		if( length > 1024*32 ) {
			throw new RuntimeException("Blob too big ("+length+" bytes)");
		}
		byte[] dat = new byte[length];
		p.pos += 4;
		int i=0;
		for( i=0; i<length; ++i ) {
			dat[i] = p.data[p.pos+i];
		}
		p.pos += roundUp(length);
		return dat;
	}
	
	protected static List decodeList( StringParsePosition tagParser, ByteArrayParsePosition dataParser ) {
		ArrayList l = new ArrayList();
		while( tagParser.hasCurrent() && tagParser.current() != ']' ) {
			l.add(decode(tagParser,dataParser));
		}
		++tagParser.pos;
		return l;
	}
	
	protected static Map decodeMap( StringParsePosition tagParser, ByteArrayParsePosition dataParser ) {
		HashMap m = new HashMap();
		while( tagParser.hasCurrent() && tagParser.current() != ')' ) {
			Object k = decode(tagParser,dataParser);
			Object v = decode(tagParser,dataParser);
			m.put( k, v );
		}
		++tagParser.pos;
		return m;
	}
	
	protected static Object decode( StringParsePosition tagParser, ByteArrayParsePosition dataParser ) {
		char t = tagParser.next();
		switch( t ) {
		case('i'): return decodeInteger( dataParser );
		case('f'): return decodeFloat( dataParser );
		case('c'): return decodeCharacter( dataParser );
		case('l'): return decodeLong( dataParser );
		case('d'): return decodeDouble( dataParser );
		case('r'): return decodeRgbaMessage( dataParser );
		case('m'): return decodeMidiMessage( dataParser );
		case('N'): return null;
		case('T'): return Boolean.TRUE;
		case('F'): return Boolean.FALSE;
		case('I'): return Infinitum.INSTANCE;
		case('s'): return decodeString( dataParser );
		case('S'): return decodeSymbol( dataParser );
		case('b'): return decodeBlob( dataParser );
		case('['): return decodeList( tagParser, dataParser );
		case('('): return decodeMap( tagParser, dataParser );
		default:
			throw new RuntimeException( "Don't know how to parse '"+t+"' (type tag = \""+tagParser.str+"\")");
		}
	}
	
	public static InMessage decodeMessage( byte[] data ) {
		return new InMessage(data);
	}
}
