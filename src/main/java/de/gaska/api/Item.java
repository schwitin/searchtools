package de.gaska.api;


public class Item {
	String itemNumber="";
	String name="";
	String priceNetto="";
	String verfuegbarkeit ="";
	String oemNummern ="";
	
	public Item(String itemNumber){
		this.itemNumber = itemNumber;
	}
	
	public String getOemNummern() {
		return oemNummern;
	}
	public void setOemNummern(String oemNummern) {
		this.oemNummern = oemNummern;
	}
	public String getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPriceNetto() {
		return priceNetto;
	}
	public void setPriceNetto(String priceNetto) {
		this.priceNetto = priceNetto;
	}
	public String getVerfuegbarkeit() {
		return verfuegbarkeit;
	}
	public void setVerfuegbarkeit(String verfuegbarkeit) {
		this.verfuegbarkeit = verfuegbarkeit;
	}

	@Override
	public String toString() {
	    StringBuilder str = new StringBuilder();
	    str.append(this.itemNumber);
	    str.append(";");
	    str.append(this.name);
	    str.append(";");
	    str.append(this.verfuegbarkeit);
	    str.append(";");
	    str.append(this.priceNetto);
		str.append(";");
		str.append(this.oemNummern);
	    return str.toString();
	}
	
	public static String getHeader(){
		return "Id;Gesucht;ArtikelNr;Bezeichnung;Verfuegbarkeit;Preis Netto;OEM Nummern";
	}
	
}
