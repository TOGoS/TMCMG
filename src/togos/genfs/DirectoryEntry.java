package togos.genfs;

public class DirectoryEntry extends Stat {
	public String name;
	
	public DirectoryEntry() { }
	public DirectoryEntry( String name, long size, int mode ) {
		super( size, mode );
		this.name = name;
	}
}
