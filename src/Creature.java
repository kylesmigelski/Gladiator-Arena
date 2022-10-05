import java.util.ArrayList;
import java.util.Random;

/*********************************************************
 Represents a creature in the Arena

 @author Kyle Smigelski
 @version Winter 2021
 *********************************************************/
public class Creature {

    /** ***********************************************************************
     * Instance Variables
     */
    private int strength;                   // Strength value
    private int health;                     // Health value
    private int timeFrozen;                 // Turns frozen
    private int timeOnFire;                 // Turns on fire

    Random rand = new Random();
    private ArrayList<SpellType> spells;    // ArrayList contains spells

    /** ***********************************************************************
     * Builds a creature.
     * @param str   Strength
     * @param hp    Health
     */
    public Creature(int str, int hp){
        strength = str;
        health = hp;

        // Initialize spell ArrayList
        spells = new ArrayList<>();

        // Check for negative parameters
        if (str < 0 || hp < 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * @return time frozen.
     */
    public int getTimeFrozen() {
        return timeFrozen;
    }

    /**
     * @return time on fire.
     */
    public int getTimeOnFire() {
        return timeOnFire;
    }

    /**
     * @param index index of spell.
     * @return spell at index
     */
    public SpellType getSpell(int index){
        return spells.get(index);
    }

    /**
     * @return strength value.
     */
    public int getStrength() {
        return strength;
    }

    /**
     * @return health value.
     */
    public int getHealth() {
        return health;
    }

    /**
     * @param timeFrozen sets timeFrozen.
     */
    public void setTimeFrozen(int timeFrozen) {
        this.timeFrozen = timeFrozen;

        // Check for negative parameter
        if (timeFrozen < 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param timeOnFire sets time on fire.
     */
    public void setTimeOnFire(int timeOnFire) {
        this.timeOnFire = timeOnFire;
        if (timeOnFire < 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param strength set strength value.
     */
    public void setStrength(int strength) {
        this.strength = strength;
        if (strength < 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param health sets health value.
     */
    public void setHealth(int health) {
        this.health = health;
        if (health < 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Levels up the creature by giving it a spell.
     * @param type type of spell
     */
    public void levelUp(SpellType type){
        giveSpell(type);
    }

    /**
     * @param damage reduces health by inputted damage.
     */
    public void hurt(int damage){
        health -= damage;
    }

    /**
     * @param amt amount to add to health.
     */
    public void heal(int amt){
        health += amt;
    }

    /**
     * @return if frozen.
     */
    public boolean isFrozen(){
        return timeFrozen > 0;
    }

    /**
     * @param type  spell to give creature.
     */
    public void giveSpell(SpellType type){
        spells.add(type);
    }

    /**
     * Sets timeFrozen to either 1 or 2 turns.
     */
    public void freeze(){
        timeFrozen = rand.nextInt(2) + 1;
    }

    /**
     * Reduces timeFrozen by one.
     */
    public void decFreezeTimer(){
        timeFrozen--;
    }

    /**
     * Reduces timeOnFire by one.
     */
    public void decFireTimer(){
        timeOnFire--;
    }

    /**
     * Sets timeOnFire to random int between 1 and 4 turns.
     */
    public void setOnFire(){
        timeOnFire = rand.nextInt(3) + 1;
    }

    /**
     * @return if creature is on fire.
     */
    public boolean isOnFire(){
        return timeOnFire > 0;
    }

    /** ***********************************************************************
     * If creature is not frozen, attack.
     * @return int between 0 and strength
     */
    public int attack(){
        if (!isFrozen()){
            return rand.nextInt(strength);
        } else {
            return 0;
        }
    }

    /** ***********************************************************************
     * Loop ArrayList of spells to see if the creature has the inputted spell.
     * @param type type of spell
     * @return true if has spell
     */
    public boolean canCast(SpellType type){
        for (SpellType spell : spells) {
            if (spell.equals(type)) {
                return true;
            }
        }
        return false;
    }

    /** ***********************************************************************
     * Print all held spells.
     * @return String representation of spells
     */
    public String printSpells() {
        String spellList = "";
        for (int i = 0; i < spells.size(); i++){
            spellList = spellList.concat(i+1 + "." + spells.get(i).toString());
        }
        return spellList;
    }

    /** ***********************************************************************
     * @return amount of spells held
     */
    public int spellAmount(){
        int count = 0;
        for (int i = 0; i < spells.size(); i++){
            count++;
        }
        return count;
    }

    /** ***********************************************************************
     * Remove specified spell
     * @param index index of spell
     */
    public void removeSpell(int index){
        spells.remove(index);
    }


}
