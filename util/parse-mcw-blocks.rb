#!/usr/bin/ruby

print <<'EOS'
package togos.minecraft.mapgen.world;

import java.util.HashMap;

import togos.minecraft.mapgen.world.gen.Material;

public class Materials
{
	static Material[] byBlockType = new Material[128];
	static HashMap byName = new HashMap();
	static HashMap byIcon = new HashMap();
	
	static String normalizeName(String name) {
		name = name.replace("state", "");
		name = name.replace(" ", "");
		name = name.replace("'", "");
		name = name.replace("-", "");
		name = name.replace("\"", "");
		name = name.replace("(", "");
		name = name.replace(")", "");
		return name;
	}
	
	static void add( Material m ) {
		byBlockType[m.blockType] = m;
		byName.put(normalizeName(m.name), m);
		byIcon.put(m.icon, m);
	}
	
	public static Material getByBlockType(int blockType) {
		if( blockType < 0 || blockType > 127 ) return null;
		return byBlockType[blockType];
	}
	
	public static Material getByName(String name) {
		return (Material)byName.get(normalizeName(name));
	}
	
	public static Material getByIcon(String icon) {
		return (Material)byIcon.get(icon);
	}
	
	static {
EOS

colors = {
  :air => '00000000',
  :stone => 'FF888888',
  :cobblestone => 'FF666666',
  :grass => 'FF008800',
  :dirt => 'FF884400',
  :sand => 'FFAAAA66',
  :water => 'FF000088',
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
  :water => 'Moving water',
  :stationarywater => 'Water',
  :lava => 'Moving lava',
  :stationarylava => 'Lava',
}

def normalize( name )
  name.gsub(/[ '"-]/,'').downcase.intern
end

while line = gets
  if line =~ /\|\|[^\|]+\|\|\s+([0-9A-Fa-f]+)\s+\|\|\s+'*([^<]+)/
    hex = $1
    extraHex = '00';
    name = $2
    color = '0xFF000000'
    name.strip!
    if name =~ /''/
      name = $`
    end
    name = name.gsub('[[','').gsub(']]','')
    sname = normalize(name)
    
    if newname = newnames[sname]
      name = newname
      sname = normalize(name)
    end
    
    colorHex = colors[sname] || 'FFFF00FF'
    icon = icons[sname] || '? '    
    names = name.split('|')
    name = names[-1]
    puts "\t\tadd(new Material( (byte)0x#{hex}, (byte)0x#{extraHex}, 0x#{colorHex}, #{icon.inspect}, #{name.inspect} ));"
  end
end

print <<EOS
	}
}
EOS
