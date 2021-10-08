package de.gaska.api;

import java.util.ArrayList;
import java.util.List;

public class Part {
    String id;
    String partNr;
    String partName;
    String partBedarfsmaenge;
    int substringLastCharsFromPartNrWhileSearch = 0;


    List<Item> otherItems = new ArrayList<>();

    public Part(final String id, final String partNr, final String partName, final String partBedarfsmaenge) {
        this.id = id;
        this.partNr = partNr.equals("-") ? "?" : partNr;
        this.partName = partName;
        this.partBedarfsmaenge = partBedarfsmaenge;
    }

    public String getPartNr() {
        return partNr;
    }

    public String getPartName() {
        return partName;
    }

    public String getPartBedarfsmaenge() {
        return partBedarfsmaenge;
    }

    public int getSubstringLastCharsFromPartNrWhileSearch() {
        return substringLastCharsFromPartNrWhileSearch;
    }

    public void setSubstringLastCharsFromPartNrWhileSearch(final int substringLastCharsFromPartNrWhileSearch) {
        this.substringLastCharsFromPartNrWhileSearch = substringLastCharsFromPartNrWhileSearch;
    }

    public void setOtherItems(final List<Item> otherItems) {
        this.otherItems = otherItems;
        this.otherItems.forEach(i -> i.setPart(this));
    }

    public List<Item> getOtherItems() {
        return otherItems;
    }

    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.id);
        buffer.append(";");
        buffer.append(this.partNr);

        int i = 0;
        for (final Item item : otherItems) {

            if (i == 0)
                buffer.append(";" + item);
            else
                buffer.append(";;" + item);

            if (i != otherItems.size() - 1) {
                buffer.append(System.getProperty("line.separator"));
            }
            i++;

        }

        return buffer.toString();
    }
}
