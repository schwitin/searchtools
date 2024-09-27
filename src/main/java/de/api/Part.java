package de.api;

import java.util.ArrayList;
import java.util.List;

public class Part {
    final String id;
    String partNr;
    final String partName;
    final String partBedarfsmaenge;
    final int substringLastCharsFromPartNrWhileSearch = 0;
    List<Item> otherItems = new ArrayList<>();

    public Part(final String id, final String partNr, final String partName, final String partBedarfsmaenge) {
        this.id = id;
        this.partNr = partNr;
        this.partName = partName;
        this.partBedarfsmaenge = partBedarfsmaenge;
    }

    public String getId() {
        return id;
    }

    public String getPartNr() {
        return partNr;
    }

    public void setPartNr(final String partNr) {
        this.partNr = partNr;
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

    public void setOtherItems(final List<Item> otherItems) {
        this.otherItems = otherItems;
        this.otherItems.forEach(i -> i.setPart(this));
    }

    public List<Item> getOtherItems() {
        return otherItems;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(this.id);
        buffer.append(";");
        buffer.append(this.partNr);

        int i = 0;
        for (final Item item : otherItems) {

            if (i == 0)
                buffer.append(";").append(item);
            else
                buffer.append(";;").append(item);

            if (i != otherItems.size() - 1) {
                buffer.append(System.lineSeparator());
            }
            i++;

        }

        return buffer.toString();
    }
}
