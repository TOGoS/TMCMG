package togos.minecraft.mapgen.world.structure;

import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.ShortTag;
import org.jnbt.Tag;

import togos.minecraft.mapgen.TagMap;

public class InventoryItemData
{
	public int itemTypeId;
	public int damage;
	public int count;
	public int slotNumber;
	
	public InventoryItemData() { }
	
	public InventoryItemData(short blockId, int count, int slot) {
		this.itemTypeId = blockId;
		this.count = count;
		this.slotNumber = slot;
	}
	
	public Tag toTag(String name) {
		TagMap m = new TagMap();
		m.add(new ShortTag("id",(short)itemTypeId));
		m.add(new ShortTag("Damage",(short)damage));
		m.add(new ByteTag("Count",(byte)count));
		m.add(new ByteTag("Slot",(byte)slotNumber));
		return new CompoundTag(name, m);
	}
}
