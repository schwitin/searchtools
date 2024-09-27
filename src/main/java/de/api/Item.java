package de.api;


public class Item {
    String itemNumber;
    String name = "";
    String priceNetto = "";
    String priceBrutto = "";
    String verfuegbarkeit = "";
    String oemNummern = "";
    String verpackungseinheit = "";
    String gewicht = "";
    String urlToDetails;


    Part part;

    public Item(final String itemNumber, final String urlToDetails) {
        this.itemNumber = itemNumber;
        this.urlToDetails = urlToDetails;
    }

    public Part getPart() {
        return part;
    }

    public String getOemNummern() {
        return oemNummern
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("  ", " ")
                .trim();
    }

    public void setOemNummern(final String oemNummern) {
        this.oemNummern = oemNummern;
    }

    public String getItemNumber() {
        return itemNumber
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("  ", " ")
                .trim();
    }

    public void setItemNumber(final String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getName() {
        return name
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("  ", " ")
                .replace("\"", "''")
                .trim();
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPriceNetto() {
        return priceNetto
                .replace("EUR", "")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("  ", " ")
                .trim();
    }

    public void setPriceNetto(final String priceNetto) {
        this.priceNetto = priceNetto;
    }

    public String getPriceBrutto() {
        return priceBrutto;
    }

    public void setPriceBrutto(String priceBrutto) {
        this.priceBrutto = priceBrutto;
    }

    public String getVerfuegbarkeit() {
        return verfuegbarkeit
                .replace(",00000", "")
                .replace(",0000", "")
                .replace(",000", "")
                .replace(",00", "")
                .replace(",0", "")
                .replace("Stk.", " Stk.")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("  ", " ")
                .trim();
    }

    public void setVerfuegbarkeit(final String verfuegbarkeit) {
        this.verfuegbarkeit = verfuegbarkeit;
    }

    public String getVerpackungseinheit() {
        return verpackungseinheit
                .replace(",00000", "")
                .replace(",0000", "")
                .replace(",000", "")
                .replace(",00", "")
                .replace(",0", "")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("  ", " ")
                .trim();
    }

    public void setVerpackungseinheit(final String verpackungseinheit) {
        this.verpackungseinheit = verpackungseinheit;
    }

    public void setPart(final Part part) {
        this.part = part;
    }

    public String getGewicht() {
        return gewicht;
    }

    public void setGewicht(String gewicht) {
        this.gewicht = gewicht;
    }

    public String getUrlToDetails() {
        return urlToDetails;
    }

    public void setUrlToDetails(final String urlToDetails) {
        this.urlToDetails = urlToDetails;
    }


    @Override
    public String toString() {
        return this.getItemNumber() +
                ";" +
                this.part.getPartName() + // Bezechnung IMPEX
                ";" +
                this.getName() +
                ";" +
                this.part.getPartBedarfsmaenge() +
                ";" +
                this.getVerfuegbarkeit() +
                ";" +
                this.getPriceNetto() +
                ";" +
                this.getVerpackungseinheit() +
                ";" +
                this.getOemNummern();
    }

}
