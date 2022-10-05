/*********************************************************
 Represents the player, inherits Creature

 @author Kyle Smigelski
 @version Winter 2021
 *********************************************************/

public class Player extends Creature{

    /** ***********************************************************************
     * Instance Variables
     */
    private int exp;                    // Experience points
    private int maxXP = 25;             // XP needed to level up
    private int level = 1;              // Starting level

    /** ***********************************************************************
     * Builds the player.
     * @param str   Strength
     * @param hp    Health
     */
    public Player(int str, int hp) {
        super(str, hp);
        exp = 0;
    }

    /**
     * @return maximum XP.
     */
    public int getMaxXP(){
        return maxXP;
    }

    /**
     * @param max set max XP.
     */
    public void setMaxXP(int max){
        maxXP = max;
        if (max < 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param amt amount of XP to add.
     */
    public void addXP(int amt){
        exp += amt;
    }

    /**
     * @return current XP.
     */
    public int getExp(){
        return exp;
    }

    /**
     * @return current level.
     */
    public int getLevel(){
        return level;
    }

    /**
     * @param level level to set player to.
     */
    public void setLevel(int level){
        this.level = level;
        if (level < 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * @return true if player's XP is more or equal to max XP
     */
    public boolean canLevelUp(){
        return (exp >= maxXP);
    }

    /** ***********************************************************************
     * Levels the player up.
     * @param type the type of spell given
     */
    public void levelUp(SpellType type){

        // Sets XP back to zero
        exp = 0;

        // Add 5 strength
        super.setStrength(super.getStrength() + 5);

        // Give spell
        super.giveSpell(type);

        // Increment level
        level++;

        // Adds half of current max XP to the new max
        setMaxXP(maxXP + (maxXP / 2));
    }


}
