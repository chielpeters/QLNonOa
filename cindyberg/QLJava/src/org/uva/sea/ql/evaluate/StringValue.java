package org.uva.sea.ql.evaluate;

public class StringValue extends Value{
	
	private final String value;

	public StringValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
}
