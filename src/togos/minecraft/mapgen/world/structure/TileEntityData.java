package togos.minecraft.mapgen.world.structure;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import togos.minecraft.mapgen.TagMap;

public abstract class TileEntityData
{
	public int x, y, z;
	
	public abstract String getTypeId();
	
	protected void toTag( TagMap m ) {
		m.add(new StringTag("id", getTypeId()));
		m.add(new IntTag("x", x));
		m.add(new IntTag("y", y));
		m.add(new IntTag("z", z));
	}
	
	public Tag toTag() {
		TagMap m = new TagMap();
		toTag(m);
		return new CompoundTag("", m);
	}
}
