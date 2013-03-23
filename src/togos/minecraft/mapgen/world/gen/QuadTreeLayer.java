package togos.minecraft.mapgen.world.gen;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;

import togos.minecraft.mapgen.ui.NoiseCanvas;
import togos.minecraft.mapgen.ui.WorldExploreKeyListener;
import togos.minecraft.mapgen.util.ServiceManager;
import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.LayerUtil;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.data.DataDaIa;
import togos.noise.v1.data.DataIa;
import togos.noise.v1.func.Constant_Da;
import togos.noise.v1.func.Constant_Ia;
import togos.noise.v1.func.FunctionDaDaDa_Ia;
import togos.noise.v1.func.FunctionDaDa_Da;
import togos.noise.v1.func.FunctionDaDa_DaIa;

public class QuadTreeLayer implements ChunkMunger
{
	static class Node extends HeightmapLayer {
		Node subNode0, subNode1, subNode2, subNode3;
		
		public Node( FunctionDaDaDa_Ia material, FunctionDaDa_Da floorHeight, FunctionDaDa_Da ceilingHeight ) {
			super( material, floorHeight, ceilingHeight );
		}
		public Node() {
			this( new Constant_Ia(0), new Constant_Da(0), new Constant_Da(0) );
		}
		public Node( Node subNode0, Node subNode1, Node subNode2, Node subNode3 ) {
			super( null, null, null );
			this.subNode0 = subNode0;
			this.subNode1 = subNode1;
			this.subNode2 = subNode2;
			this.subNode3 = subNode3;
		}
	}
	
	Node rootNode;
	double rootNodeX, rootNodeZ, rootNodeWidth, rootNodeDepth;
	
	class QTState implements ChunkMunger, FunctionDaDa_DaIa {
		Node node;
		double nodeX, nodeZ, nodeWidth, nodeDepth;
		
		protected Node getLeafNode( double x, double z ) {
			if( x < rootNodeX || z < rootNodeZ || x >= rootNodeX+rootNodeWidth || z >= rootNodeZ+rootNodeDepth ) {
				return null;
			}
			
			if( node == null || x < nodeX || z < nodeZ || x>=nodeX+nodeWidth || z >= nodeZ+nodeDepth ) {
				node = rootNode;
				nodeX = rootNodeX;
				nodeZ = rootNodeZ;
				nodeWidth = rootNodeWidth;
				nodeDepth = rootNodeDepth;
				
				while( true ) {
					double halfWidth = nodeWidth/2;
					double halfDepth = nodeDepth/2;
					double halfX = nodeX+halfWidth;
					double halfZ = nodeZ+halfDepth;
					double endX = nodeX+nodeWidth;
					double endZ = nodeZ+nodeDepth;

					if( node.subNode0 != null && x>=nodeX && z>=nodeZ && x<halfX && z<halfZ ) {
						node = node.subNode0;
						nodeWidth = halfWidth; nodeDepth = halfDepth;
						continue;
					}
					if( node.subNode1 != null && x>=halfX && z>=nodeZ && x<endX && z<halfZ ) {
						node = node.subNode1;
						nodeX = halfX;
						nodeWidth = halfWidth; nodeDepth = halfDepth;
						continue;
					}
					if( node.subNode2 != null && x>=nodeX && z>=halfZ && x<halfX && z<endZ ) {
						node = node.subNode2;
						nodeZ = halfZ;
						nodeWidth = halfWidth; nodeDepth = halfDepth;
						continue;
					}
					if( node.subNode3 != null && x>=halfX && z>=halfZ && x<endX && z<endZ ) {
						node = node.subNode3;
						nodeX = halfX; nodeZ = halfZ;
						nodeWidth = halfWidth; nodeDepth = halfDepth;
						continue;
					}
					break;
				}
			}
			return node;
		}
		
		public void mungeChunk( ChunkData cd ) {
			// @todo
		}
		
		public DataDaIa apply( DataDaDa in ) {
			int vectorSize = in.getLength();
			
			double[] outCeiling = new double[vectorSize];
			int[] outType = new int[vectorSize];
			
			double[] inX = new double[vectorSize];
			double[] inZ = new double[vectorSize];
			
			int beginIdx = 0;
			while( beginIdx < vectorSize) {
				Node n = getLeafNode(in.x[beginIdx], in.y[beginIdx]);
				Node n2 = n;
				int j = 0;
				while( n2 == n ) {
					inX[j] = in.x[j+beginIdx];
					inZ[j] = in.y[j+beginIdx];
					
					++j;
					if( j+beginIdx == vectorSize ) break;
					n2 = getLeafNode(in.x[j+beginIdx], in.y[j+beginIdx]);
				}
				
				if( n != null ) {
					DataDaDa nodeIn = new DataDaDa(vectorSize, inX, inZ);
					DataDa floor = n.floorHeightFunction.apply(nodeIn);
					DataDa ceiling = n.ceilingHeightFunction.apply(nodeIn);
					double[] topY = LayerUtil.maxY(ceiling.x);
					DataIa type = n.typeFunction.apply(new DataDaDaDa(vectorSize, inX, topY, inZ));
					
					int[] rFloor = LayerUtil.roundHeights(floor.x);
					int[] rCeil = LayerUtil.roundHeights(ceiling.x);
					
					for( int i=0; i<j; ++i ) {
						if( rCeil[i] > rFloor[i] ) {
							outCeiling[beginIdx+i] = ceiling.x[i];
							outType[beginIdx+i] = type.v[i];
						} else {
							outType[beginIdx+i] = Blocks.NONE;
						}
					}
				}
				
				beginIdx += j;
			}
			
			return new DataDaIa( vectorSize, outCeiling, outType );
		}
	}
	
	public FunctionDaDa_DaIa getGroundFunction() {
		return new QTState();
	}
	
	public void mungeChunk( ChunkData cd ) {
		new QTState().mungeChunk(cd);
	}
	
	public static void main( String[] args ) {
		final ServiceManager sm = new ServiceManager();
		final Frame f = new Frame("Noise Canvas");
		final NoiseCanvas nc = new NoiseCanvas();
		
		QuadTreeLayer qtl = new QuadTreeLayer();
		qtl.rootNodeX = -1024;
		qtl.rootNodeZ = -1024;
		qtl.rootNodeWidth = 2048;
		qtl.rootNodeDepth = 2048;
		qtl.rootNode = new Node(
			new Node( new Constant_Ia(Blocks.COBBLESTONE), new Constant_Da(64), new Constant_Da(96) ),
			new Node(),
			new Node(),
			new Node(
				new Node( new Constant_Ia(Blocks.COBBLESTONE), new Constant_Da(64), new Constant_Da(96) ),
				new Node(),
				new Node(),
				new Node()
			)
		);
		
		WorldGenerator wg = new SimpleWorldGenerator(null, qtl.getGroundFunction(), null, Collections.EMPTY_MAP );
		
		nc.setWorldGenerator( wg );
		
		nc.setPreferredSize(new Dimension(512,384));
		f.add(nc);
		f.pack();
		f.addWindowListener(new WindowListener() {
			public void windowOpened( WindowEvent arg0 ) {}
			public void windowIconified( WindowEvent arg0 ) {}
			public void windowDeiconified( WindowEvent arg0 ) {}
			public void windowDeactivated( WindowEvent arg0 ) {}
			public void windowClosing( WindowEvent arg0 ) {
				nc.stopRenderer();
				f.dispose();
				sm.halt();
			}
			public void windowClosed( WindowEvent arg0 ) {}
			public void windowActivated( WindowEvent arg0 ) {}
		});
		sm.start();
		f.setVisible(true);
		nc.addKeyListener(new WorldExploreKeyListener(nc));
		nc.requestFocus();
	}
}
