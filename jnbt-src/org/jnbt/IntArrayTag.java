package org.jnbt;

public class IntArrayTag extends Tag
{
	int[] value;
	
	public IntArrayTag( String name, int[] value ) {
		super(name);
		this.value = value;
	}
	
	@Override
	public int[] getValue() {
		return value;
	}
}
