/** ***********************************************************************
 * Enumeration for the type of spell.
 */

public enum SpellType {
    FROST("Frost"),
    FIRE("Fire"),
    LIGHT("Lightning"),
    HEAL("Heal");

    private final String type;

    SpellType(String spell){
        this.type = spell;
    }

    public String toString() {
        return type + " ";
    }

}
