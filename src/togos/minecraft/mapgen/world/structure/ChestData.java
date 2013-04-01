package togos.minecraft.mapgen.world.structure;

import java.util.ArrayList;
import java.util.List;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import togos.minecraft.mapgen.TagMap;
import togos.minecraft.mapgen.world.Blocks;

public class ChestData extends TileEntityData
{
	public List<InventoryItemData> inventoryItems = new ArrayList<InventoryItemData>();
	
	public String getTypeId() {
		return "Chest";
	}
	
	public byte getBlockId() {
		return Blocks.CHEST;
	}
	
	public void toTag( TagMap<Tag> m ) {
		super.toTag(m);
		List<Tag> itemTags = new ArrayList<Tag>();
		for( InventoryItemData iid : inventoryItems ) {
			itemTags.add( iid.toTag("") );
		}
		m.add(new ListTag<Tag>("Items", CompoundTag.class, itemTags));
	}
}
