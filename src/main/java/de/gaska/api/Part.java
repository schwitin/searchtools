package de.gaska.api;

import java.util.ArrayList;
import java.util.List;

public class Part {
	String id;
	String partNr;

	List<Item> otherItems = new ArrayList<>();
	public Part(String id, String partNr){
		this.id = id;
		this.partNr = partNr;
	}

	public String getPartNr() {
		return partNr;
	}

	public void addOtherItem(Item item){
		this.otherItems.add(item);
	}
	
	public List<Item> getOtherItems() {
		return otherItems;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.id);
		buffer.append(";");
		buffer.append(this.partNr);

		int i = 0;
		for (Item item : otherItems) {

			if(i == 0)
	        	buffer.append(";" + item );
			else
				buffer.append(";;" + item );

	        if(i != otherItems.size() -1) {
				buffer.append(System.getProperty("line.separator"));
			}
	        i++;

        }

	    return buffer.toString();
	}
}
