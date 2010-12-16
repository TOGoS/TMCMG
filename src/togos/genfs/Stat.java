package togos.genfs;

public class Stat {
	public long size;
	public int mode;
	public Stat() {}
	public Stat( long size, int mode ) {
		this.size = size;
		this.mode = mode;
	}
}
