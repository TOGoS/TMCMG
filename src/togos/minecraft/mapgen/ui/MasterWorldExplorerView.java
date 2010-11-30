package togos.minecraft.mapgen.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import togos.minecraft.mapgen.world.gen.WorldGenerator;

public class MasterWorldExplorerView implements WorldExplorerView
{
	protected List subViews = new ArrayList(); 
	protected WorldGenerator wg;
	protected double wx=0, wy=0, zoom=1;
	
	public MasterWorldExplorerView() {
    }
	
	public void addSubView( WorldExplorerView subView ) {
		subViews.add(subView);
	}
	
	protected WorldExplorerView getPrimarySubView() {
		if( subViews.size() > 0 ) return (WorldExplorerView)subViews.get(0);
		return null;
	}
	
	public double getWorldX() {  return wx;  }
	public double getWorldY() {  return wy;  }
	public double getZoom() {  return zoom;  }
	
	public double getWorldXStepSize() {
		WorldExplorerView pev = getPrimarySubView();
		return pev == null ? 0 : pev.getWorldXStepSize();
	}
	
	public double getWorldYStepSize() {
		WorldExplorerView pev = getPrimarySubView();
		return pev == null ? 0 : pev.getWorldYStepSize();
	}
	
	public void setWorldGenerator( WorldGenerator wg ) {
		this.wg = wg;
		for( Iterator i=subViews.iterator(); i.hasNext(); ) {
			((WorldExplorerView)i.next()).setWorldGenerator(wg);
		}
	}
	
	public void setWorldPos( double wx, double wy, double zoom ) {
		this.wx = wx;
		this.wy = wy;
		this.zoom = zoom;
		for( Iterator i=subViews.iterator(); i.hasNext(); ) {
			((WorldExplorerView)i.next()).setWorldPos(wx, wy, zoom);
		}
	}
}
