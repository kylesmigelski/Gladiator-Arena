import java.util.Random;
import java.util.Scanner;

/*********************************************************
 Creates a 4 queue arena with creatures continuously being added to the queues.
 All game logic is handled here.

 @author Kyle Smigelski
 @version Winter 2021
 *********************************************************/
public class Arena {

    /** ***********************************************************************
     * Instance Variables
     */
    private int turns = 1;                  // Increments every time the player has a turn
    private int cycle = 1;                  // Cycles 1- 4 for monster spawning
    private int HP;                         // Player health
    private int spellChance = 10;           // Chance a monster spawns with a spell
    private String mode;
    Queue<Creature> N;                      // Queues for each direction
    Queue<Creature> E;
    Queue<Creature> S;
    Queue<Creature> W;
    Player p;                               // Player object

    /** ***********************************************************************
     * Initializes the game.
     */
    public Arena(){

        // Initialize queues
        N = new Queue<>();
        E = new Queue<>();
        S = new Queue<>();
        W = new Queue<>();
    }

    /** ***********************************************************************
     * Program starting point.
     * @param args Unused.
     */
    public static void main(String[] args){
        Arena a = new Arena();
        a.gameLoop();
    }

    /** ***********************************************************************
     * Loops until the the game is over
     */
    public void gameLoop(){
        Scanner kb = new Scanner(System.in);
        System.out.println("Game starting...");

        // Asks the player to select mode, checks for bad input
        System.out.println("Choose mode: \n1. Easy\n2. Hard\n3.Blitz");
        do {
            mode = kb.next();
        } while (!mode.matches("[123]"));
        System.out.println("Choose starting level: from 1 to 7:");
        String levelInput;
        do {
            levelInput = kb.next();
        } while (!levelInput.matches("[1234567]"));
        int level = Integer.parseInt(levelInput);

        // Mode 1 sets health and XP for an easy game
        if (mode.equals("1")) {
            HP = 80;
            p = new Player(10, HP);
            p.giveSpell(SpellType.LIGHT);
            for (int i = 1; i < level; i++){
                p.levelUp(randSpell());
            }
            p.setMaxXP(15);
        }

        // Mode 2 is harder
        if (mode.equals("2")) {
            HP = 60;
            p = new Player(10, HP);
            for (int i = 1; i < level; i++){
                p.levelUp(randSpell());
            }
            p.setMaxXP(20);
        }

        // Mode 3 sets for faster level up
        if (mode.equals("3")) {
            HP = 80;
            p = new Player(10, HP);
            for (int i = 1; i < level; i++){
                p.levelUp(randSpell());
            }
            p.setMaxXP(10);
            spellChance = 5;
        }

        // Starts game loop, controls order of turns
        while(!isGameOver()){
            createMonster();
            playersTurn();
            monstersTurn();
            turns++;
        }
        System.out.println("You Died!\nSurvived " + turns + " turns.");
    }

    /** ***********************************************************************
     * Checks if player health is 0 or below.
     */
    boolean isGameOver(){
        return p.getHealth() <= 0;
    }

    /** ***********************************************************************
     * Displays player info and allows user input for players action
     */
    void playersTurn(){
        Random rand = new Random();
        int health = p.getHealth();
        int str = p.getStrength();
        int xp = p.getExp();
        int fire = rand.nextInt(5) + 1;
        Scanner kb = new Scanner(System.in);
        System.out.println("\n-------------------------------------------------------------");
        display();
        System.out.println("\n-------------------------------------------------------------" +
                "\nHealth: "+health+"        Strength: " +str+"       Level: " + p.getLevel() + "       XP: "+xp+"      Turns: " +turns+
                "\nSpells: " + p.printSpells() + "\n-------------------------------------------------------------");
        
        // If player is on fire, hurt the player and display damage done
        if (p.isOnFire()) {
            p.hurt(fire);
            System.out.println("You burned for " + fire + " damage.");
            p.decFireTimer();
        }

        // If player is not frozen and has no spells, default action
        else if (p.spellAmount() == 0 && !p.isFrozen()) {
            System.out.println("Choose a direction to attack:\nN E S W");
            String option = kb.next();
            playerAction(option.toUpperCase(), false);
        }

        // If player is not frozen and has a spell, call player action with spell "true"
        else if (p.spellAmount() > 0 && !p.isFrozen()) {
            System.out.println("\nChoose a direction to attack: N E S W");
            String option = kb.next();
            playerAction(option.toUpperCase(), true);
        }

        // If player is frozen, skip turn and display how long frozen
        else if (p.isFrozen()){
            System.out.println("You are frozen for " + p.getTimeFrozen() + " more turns");
            p.decFreezeTimer();
        }

        // If player can level up, do so with a random spell.
        if (p.canLevelUp()) {
            p.levelUp(randSpell());

            // If on easy mode, give extra spells
            if (mode.equals("1")){
                for (int i = 0; i < p.getLevel(); i++) {
                    p.giveSpell(randSpell());
                }
            }
            p.setHealth(HP + (p.getLevel() * 3));
            System.out.println("Level up!");
        }
    }

    /** ***********************************************************************
     * Creates a monster and adds it to one of the queues.
     */
    void createMonster(){
        Random rand = new Random();
        int str, hp;

        // Monster health and strength are scaled to player level
        int diff = p.getLevel() + 5;
        str = rand.nextInt(diff) + (p.getLevel() * p.getLevel());
        hp = rand.nextInt(diff) + (p.getLevel() * p.getLevel());
        Creature c = new Creature(str, hp);

        // Monster has the chance to spawn with a spell
        int chance = rand.nextInt(spellChance) + 1;
        if (chance == 1) {
            SpellType s = randSpell();
            c.levelUp(s);
            System.out.println("~ A new monster with a " + s + "spell approaches!");
        } else {
            System.out.println("~ A new monster approaches!");
        }
        direction().enqueue(c);
    }

    /** ***********************************************************************
     * Controls and diplays the actions of each present monster.
     */
    void monstersTurn(){
        Random rand = new Random();
        String[] dir = {"N", "E", "S", "W"};

        // If monster has a spell, it is used immediately by calling spellAttack.
        for (String value : dir) {

            // For each direction, check if the queue is empty.
            if (!isTargetEmpty(value)){
                if (target(value).peek().spellAmount() > 0){
                    spellAttack("N", "1", true, target(value).peek().getSpell(0), target(value).peek());
                    target(value).peek().removeSpell(0);
                }
            }
        }

        // If monster is not frozen or on fire, use standard attack
        for (String value : dir) {
            if (!isTargetEmpty(value)) {
                if (!(target(value).peek().isOnFire() && target(value).peek().isFrozen())) {
                    int dmg = target(value).peek().attack();
                    p.hurt(dmg);
                    System.out.println("~ Monster " + value + " attacks for " + dmg + " damage.");
                }
            }
        }

        // If monster is frozen, skip turn and decrement timer
        for (String s : dir) {
            if (!isTargetEmpty(s)) {
                if (target(s).peek().isFrozen()) {
                    System.out.println("~ Monster at " + s + " is frozen!");
                    target(s).peek().decFreezeTimer();
                }

                // if monster is on fire, damage them and check if they are dead
                if (target(s).peek().isOnFire()) {
                    int fire = rand.nextInt(5) + 1;
                    target(s).peek().hurt(fire);
                    System.out.println("~ Monster at " + s + " burned for " + fire + " damage.");
                    target(s).peek().decFireTimer();
                    if (isDead(s)) {
                        System.out.print("~ Monster Killed!\n");
                    }
                }
            }
        }
    }

    /** ***********************************************************************
     * Called in playersTurn, handles input and choice for attack.
     * @param option    direction of attack
     * @param spell     if player has a spell
     */
    void playerAction(String option, boolean spell){
        Scanner kb = new Scanner(System.in);
        String dir = option;

        while (!(isValidDirection(dir) && !isTargetEmpty(dir))){
            System.out.println("Invalid input. Try again.");
            dir = kb.next().toUpperCase();
        }

        if (!spell) {
            playerAttack(dir);
        }

        // If player has a spell, ask if they want to use it.
        else {
            System.out.println("Cast spell? y/n");
            String choice;
            do {
                choice = kb.next();
            } while (!choice.matches("[ynYN]"));
            if (choice.equals("y")) {
                System.out.println("Select Spell Number: ");
                String spellChoice;
                do{
                    spellChoice = kb.next();
                } while (Integer.parseInt(spellChoice) > p.spellAmount());
                spellAttack(dir, spellChoice, false, null, null);
            } else {
                playerAttack(dir);
            }
        }
    }

    /** ***********************************************************************
     * Called in playerAction, damages monster at selected direction and checks if
     * they died.
     * @param dir       direction of attack
     *
     */
    void playerAttack(String dir) {
        int dmg = p.attack();
        target(dir).peek().hurt(dmg);
        System.out.print("-------------------------------------------------------------\n~ Did " + dmg + " damage.\n");
        if (isDead(dir)){
            System.out.print("~ Monster killed!\n");
        }
    }

    /** ***********************************************************************
     * Handles casting of spells by both the player and monster.
     * @param dir       direction of attack
     * @param option    selected spell
     * @param isMonster boolean determines if caller is a monster
     * @param s         null if player, if monster input currently held spell
     * @param c         used for heal spell if called by monster
     */
    void spellAttack(String dir, String option, boolean isMonster, SpellType s, Creature c){
        int index = Integer.parseInt(option)-1;
        SpellType spell;

        // gets selected spell if player
        if (!isMonster && s == null) {
            spell = p.getSpell(index);
        } else {
            spell = s;
        }

        // Finds spell and calls corresponding cast method, spell is removed if caller is the player
        switch (spell) {
            case FROST -> {
                castFrost(dir, isMonster);
                if (!isMonster)
                    p.removeSpell(index);
            }
            case FIRE -> {
                castFire(dir, isMonster);
                if (!isMonster)
                    p.removeSpell(index);
            }
            case LIGHT -> {
                castLightning(isMonster);
                if (!isMonster)
                    p.removeSpell(index);
            }
            case HEAL -> {
                if (!isMonster) {
                    castHeal(false, null);
                    p.removeSpell(index);
                } else {
                    castHeal(true, c);
                }
            }
        }
    }

    /** ***********************************************************************
     * Spell that heals the caller.
     * @param isMonster     boolean determines if called by monster
     * @param c             null if player, otherwise specific monster object
     */
    private void castHeal(boolean isMonster, Creature c) {
        Random rand = new Random();

        // If player, heal half of health + 10
        if (!isMonster) {
            int hp = rand.nextInt(HP / 2) + 10;
            p.heal(hp);
            System.out.print("Healed " + hp + " health points\n");
        }

        // If monster, heal by half of current health
        else {
            int hp = c.getHealth();
            c.heal(hp/2);
            System.out.print("~ Monster healed for " + hp/2 + " points.\n");
        }
    }

    /** ***********************************************************************
     * Spell that freezes selected Creature.
     * @param dir           direction of attack
     * @param isMonster     boolean determines if called by monster
     */
    private void castFrost(String dir, boolean isMonster) {

        // If player, freeze the monster at selected direction
        if (!isMonster) {
            target(dir).peek().freeze();
            System.out.println("~ Froze monster at " + dir.toUpperCase() + " for " +
                    target(dir).peek().getTimeFrozen() + " turns.");
        }

        // If monster, freeze the player
        else {
            p.freeze();
            System.out.println("~ You have been frozen for " + p.getTimeFrozen() + " turns");
        }
    }


    /** ***********************************************************************
     * Spell that sets selected Creature on fire.
     * @param dir           direction of attack
     * @param isMonster     boolean determines if called by monster
     */
    private void castFire(String dir, boolean isMonster) {

        // If player, set selected monster on fire and display damage + turns affected
        if (!isMonster) {
            target(dir).peek().setOnFire();
            System.out.println("Set monster at " + dir.toUpperCase() + " on fire for " +
                    target(dir).peek().getTimeOnFire() + " turns");
        }

        // If monster, set player on fire
        else {
            p.setOnFire();
            System.out.println("~ You have been set on fire for " + p.getTimeOnFire() + " turns!");
        }

    }

    /** ***********************************************************************
     * Spell that damages all monsters if called by player, or if called by monster
     * damage the player.
     * @param isMonster     boolean determines if called by monster
     */
    private void castLightning(boolean isMonster){
        Random rand = new Random();

        // Random damage between 5 and 15
        int lightningDamage = rand.nextInt(10) + 5;

        // If called by player, damage every present monster and check if any died
        if (!isMonster) {
            String[] dir = {"N", "E", "S", "W"};
            for (String s : dir) {
                if (!isTargetEmpty(s)) {
                    target(s).peek().hurt(lightningDamage);
                    isDead(s);
                }
            }
            System.out.println("~ Damaged all monsters for " + lightningDamage + " points!");
        }

        // If called by monster, damage player
        else {
            p.hurt(lightningDamage);
            System.out.println("~ You have been hit with lightning for " + lightningDamage + " points!");
        }
    }

    /** ***********************************************************************
     * Used to make concise calls to specific queues.
     * @param dir   String direction inputted by user to select corresponding queue
     */
    Queue<Creature> target(String dir) {
            if (dir.equals("N"))
                return N;
            if (dir.equals("E"))
                return E;
            if (dir.equals("S"))
                return S;
           if (dir.equals("W"))
                return W;
           return null;
    }

    /** ***********************************************************************
     * Every turn, direction is cycled so new monster is placed in a different queue.
     */
    Queue<Creature> direction() {
        if (cycle == 1) {
            cycle++;
            return N;
        }
        if (cycle == 2) {
            cycle++;
            return E;
        }
        if (cycle == 3) {
            cycle++;
            return S;
        }
        if (cycle == 4) {
            cycle = 1;
            return W;
        }
        return null;
    }

    /** ***********************************************************************
     * Displays monsters for each direction, their stats and how many are in the queue.
     */
    void display(){
        String North = "";
        String East = "";
        String South = "";
        String West = "";

        // If there are monsters in the queue, display
        if (N.size() > 0)
            North = "N\n"+"Str: " + N.peek().getStrength() + " HP: " + N.peek().getHealth() + "     Monsters: " + N.size();
        if (E.size() > 0)
            East = "\nE\n"+"Str: " + E.peek().getStrength() + " HP: " + E.peek().getHealth() + "    Monsters: " + E.size();
        if (S.size() > 0)
            South = "\nS\n"+"Str: " + S.peek().getStrength() + " HP: " + S.peek().getHealth() + "    Monsters: " + S.size();
        if (W.size() > 0)
            West = "\nW\n"+"Str: " + W.peek().getStrength() + " HP: " + W.peek().getHealth() + "    Monsters: " + W.size();

        System.out.println(North+"      "+East+"        "+South+"       "+West);

    }

    /** ***********************************************************************
     * Checks if the inputted string direction is either N, S, E, or W.
     * @param dir   direction/selected queue
     */
    public boolean isValidDirection(String dir) {
        return dir.matches("^[NESW]*$");
    }

    /** ***********************************************************************
     * Checks if the selected direction is empty.
     * @param dir   direction/selected queue
     */
    public boolean isTargetEmpty(String dir) {
        return target(dir).isEmpty();
    }

    /** ***********************************************************************
     * Selects a random spell.
     */
    SpellType randSpell(){
        Random rand = new Random();
        int i = rand.nextInt(4);
        if (i == 0)
            return SpellType.FIRE;
        if (i == 1)
            return SpellType.FROST;
        if (i == 2)
            return SpellType.HEAL;
        if (i == 3)
            return SpellType.LIGHT;
        return null;
    }

    /** ***********************************************************************
     * Checks if the monster at inputted direction is dead.
     * @param dir   direction/selected queue
     */
    public boolean isDead(String dir){

        // If health is 0 or below
        if (target(dir).peek().getHealth() <= 0) {

            // If monster has a spell, give it to the player
            if (target(dir).peek().spellAmount() > 0) {
                p.giveSpell(target(dir).peek().getSpell(0));
                System.out.println("~ Picked up spell!");
            }

            // Give player XP equal to monsters strength and dequeue the monster.
            p.addXP(target(dir).peek().getStrength());
            target(dir).dequeue();
            return true;
        } else {
            return false;
        }
    }
}
