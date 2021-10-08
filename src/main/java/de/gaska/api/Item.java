package de.gaska.api;


public class Item {
    String itemNumber = "";
    String name = "";
    String priceNetto = "";
    String verfuegbarkeit = "";
    String oemNummern = "";
    String verpackungseinheit = "";
    String urlToDetails;


    Part part;

    public Item(final String itemNumber, final String urlToDetails) {
        this.itemNumber = itemNumber;
        this.urlToDetails = urlToDetails;
    }

    public String getOemNummern() {
        return oemNummern;
    }

    public void setOemNummern(final String oemNummern) {
        this.oemNummern = oemNummern;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(final String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPriceNetto() {
        return priceNetto.replace("EUR", "").trim();
    }

    public void setPriceNetto(final String priceNetto) {
        this.priceNetto = priceNetto;
    }

    public String getVerfuegbarkeit() {
        return verfuegbarkeit.replace(",00", "");
    }

    public void setVerfuegbarkeit(final String verfuegbarkeit) {
        this.verfuegbarkeit = verfuegbarkeit;
    }

    public String getVerpackungseinheit() {
        return verpackungseinheit;
    }

    public void setVerpackungseinheit(final String verpackungseinheit) {
        this.verpackungseinheit = verpackungseinheit;
    }

    public void setPart(final Part part) {
        this.part = part;
    }

    public String getUrlToDetails() {
        return urlToDetails;
    }

    public void setUrlToDetails(final String urlToDetails) {
        this.urlToDetails = urlToDetails;
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(this.getItemNumber());
        str.append(";");
        str.append(this.part.getPartName());
        str.append(";");
        str.append(this.getName());
        str.append(";");
        str.append(this.part.getPartBedarfsmaenge());
        str.append(";");
        str.append(this.getVerfuegbarkeit());
        str.append(";");
        str.append(this.getPriceNetto());
        str.append(";");
        str.append(this.getVerpackungseinheit());
        str.append(";");
        str.append(this.getOemNummern());
        return str.toString();
    }

    public static String getHeader() {
        return "Pos. Nr.;Gesucht Art.Nr.;Bezeichnung IMPEX;Gaska Art.Nr.;Bezeichnung GASKA;Bedarfsmenge IMPEX;Verfuegbarkeit, St;Grosshandelspreis, Netto;Verpackungseinheit;OEM Nummern (Ersatz-Nr.)";
    }

}
