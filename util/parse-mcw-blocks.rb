#!/usr/bin/ruby

infile = nil
outfile = nil
package = 'togos.minecraft.mapgen.world'
classname = 'Materials'

args = $*.clone
while arg = args.shift
  case arg
  when '-o'
    outfile = args.shift
  when '-package'
    package = args.shift
  when '-classname'
    classname = args.shift
  when /^[^-]/
    infile = arg
  else
    STDERR.puts "Unrecognised argument: #{arg}"
    exit 1 
  end
end

instream = infile ? open(infile,'r') : STDIN
outstream = outfile ? open(outfile,'w') : STDOUT

outstream.print <<"EOS"
/**
 * This file is automatically generated based on a section of
 * the wiki source text from http://www.minecraftwiki.net/wiki/Data_values.
 * Edit util/mcw-block-defs.txt and parse-mcw-blocks.rb and re-run
 * rather than editing this file directly.
 */

package #{package};

import java.util.HashMap;

import #{package}.Material;

public class #{classname}
{
EOS
outstream.print <<'EOS'
	static final int BLOCK_TYPE_MASK = 0xFF;
	static final int BLOCK_TYPE_COUNT = 256;
	
	public static Material[] byBlockType = new Material[BLOCK_TYPE_COUNT];
	static HashMap byName = new HashMap();
	static HashMap byIcon = new HashMap();
	
	public static String normalizeName(String name) {
		name = name.replace("state", "");
		name = name.replace(" ", "");
		name = name.replace("'", "");
		name = name.replace("-", "");
		name = name.replace("\"", "");
		name = name.replace("(", "");
		name = name.replace(")", "");
		name = name.toLowerCase();
		return name;
	}
	
	static void add( Material m ) {
		byBlockType[m.blockType] = m;
		byName.put(normalizeName(m.name), m);
		byIcon.put(m.icon, m);
	}
	
	public static Material getByBlockType(int blockType) {
		return byBlockType[blockType & BLOCK_TYPE_MASK];
	}
	
	public static Material getByName(String name) {
		return (Material)byName.get(normalizeName(name));
	}
	
	public static Material getByIcon(String icon) {
		return (Material)byIcon.get(icon);
	}
	
	static {
		Material voidMaterial = new Material( (byte)0x00, (byte)0x00, 0x00000000, "  ", "" );
		for( int i=0; i<BLOCK_TYPE_COUNT; ++i ) {
			byBlockType[i] = voidMaterial;
		}
EOS

colors = {
  :air              => '00000000',
  :stone            => 'FF888888',
  :cobblestone      => 'FF666666',
  :woodenplank      => 'FFAA8844',
  :sapling          => 'FF00AA00',
  :grass            => 'FF008800',
  :bedrock          => 'FF444444',
  :dirt             => 'FF884400',
  :movingwater      => 'FF0000AA',
  :water            => 'FF000088',
  :movinglava       => 'FFFF4400',
  :lava             => 'FFFF4411',
  :sand             => 'FFAAAA66',
  :gravel           => 'FF664444',
  :log              => 'FF883322',
  :leaves           => 'FF006633',
  :glass            => 'FF77AAAA',
  :lapislazuliore   => 'FF546283',
  :lapislazuliblock => 'FF1741A9',
  :tallgrass        => 'FF00AA00',
  :deadshrub        => 'FF884433',
  :brick            => 'FF882222',
  :obsidian         => 'FF442244',
  :torch            => 'FFFFFFEE',
  :fire             => 'FFFFDDCC',
  :snow             => 'FFCCCCCC',
  :ice              => 'FF7799AA',
  :cactus           => 'FF226622',
  :snowblock        => 'FFEEEEEE',
  :clay             => 'FFAAAAAA',
}

icons = {
  :air => '. ',
  :stone => 'XX',
  :bedrock => '##',
  :sand => 'SS',
  :dirt => 'DD',
  :cobblestone => 'CC',
  :mossycobblestone => 'MC',
  :water => 'WW',
  :grass => 'GD',
}

newnames = {
  :wood => 'Log',
  :water => 'Moving water',
  :stationarywater => 'Water',
  :lava => 'Moving lava',
  :stationarylava => 'Lava',
}

def normalize( name )
  name.gsub(/[ '"-\(\)]|state/,'').downcase.intern
end

while line = instream.gets
  if line =~ /\| [^|]+\|/
    cells = $'.split('||')
    cells.collect! { |c| c.strip }
    
    hex = cells[2].gsub(/<[^>]+>/,'')
    name = cells[3].gsub( /<sup>.*(?:<\/sup>|$)/, '' ).gsub(/<[^>]+>/,'').strip

    extraHex = '00'
    color = '0xFF000000'
    if name =~ /''/
      name = $`
    end
    name = name.gsub('[[','').gsub(']]','')
    names = name.split('|')
    name = names[-1]
    
    sname = normalize(name)
    
    if newname = newnames[sname]
      name = newname
      sname = normalize(name)
    end
    
    colorHex = colors[sname] || 'FFFF00FF'
    icon = icons[sname] || '? '    
    outstream.puts "\t\tadd(new Material( (byte)0x#{hex}, (byte)0x#{extraHex}, 0x#{colorHex}, #{icon.inspect}, #{name.inspect} ));"
  end
end

outstream.print <<EOS
	}
}
EOS
