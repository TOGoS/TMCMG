package togos.minecraft.mapgen.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class WorldExploreKeyListener implements KeyListener
{
	WorldExplorerView wev;
	public WorldExploreKeyListener( WorldExplorerView wev ) {
		this.wev = wev;
	}
	
    public void keyTyped( KeyEvent evt ) {
    }

    public void keyReleased( KeyEvent evt ) {
    }

    public void keyPressed( KeyEvent evt ) {
    	switch( evt.getKeyCode() ) {
    	case(KeyEvent.VK_MINUS): case(KeyEvent.VK_UNDERSCORE):
    		wev.setWorldPos(wev.getWorldX(),wev.getWorldY(),wev.getZoom()/2);
    		break;
    	case(KeyEvent.VK_PLUS): case(KeyEvent.VK_EQUALS):
    		wev.setWorldPos(wev.getWorldX(),wev.getWorldY(),wev.getZoom()*2);
    		break;
    	case(KeyEvent.VK_UP):
    		wev.setWorldPos(wev.getWorldX(),wev.getWorldY()-wev.getWorldYStepSize(),wev.getZoom());
    		break;
    	case(KeyEvent.VK_DOWN):
    		wev.setWorldPos(wev.getWorldX(),wev.getWorldY()+wev.getWorldYStepSize(),wev.getZoom());
    		break;
    	case(KeyEvent.VK_LEFT):
    		wev.setWorldPos(wev.getWorldX()-wev.getWorldXStepSize(),wev.getWorldY(),wev.getZoom());
    		break;
    	case(KeyEvent.VK_RIGHT):
    		wev.setWorldPos(wev.getWorldX()+wev.getWorldXStepSize(),wev.getWorldY(),wev.getZoom());
    		break;
    	}
    }
}
