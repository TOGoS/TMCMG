package togos.minecraft.mapgen.world.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;

import togos.minecraft.mapgen.TagMap;
import togos.minecraft.mapgen.world.BlockIDs;

public class ChestData extends TileEntityData
{
	public List inventoryItems = new ArrayList();
	
	public String getTypeId() {
		return "Chest";
	}
	
	public byte getBlockId() {
		return BlockIDs.CHEST;
	}
	
	public void toTag( TagMap m ) {
		super.toTag(m);
		List itemTags = new ArrayList();
		for( Iterator i=inventoryItems.iterator(); i.hasNext(); ) {
			InventoryItemData id = (InventoryItemData)i.next();
			itemTags.add( id.toTag("") );
		}
		m.add(new ListTag("Items", CompoundTag.class, itemTags));
	}
}
