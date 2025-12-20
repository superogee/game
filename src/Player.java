public class Player {
    private int maxHp;
    private int hp;
    private int attack;
    private int xp;
    private int xpToNextLvl;
    private int lvl;
    private int armour;
    public int playerX;
    public int playerY;
    private int bonusHp;

    public Player(int spawnX, int spawnY){
        this.maxHp = 20;
        this.hp = maxHp;
        this.attack = 5;
        this.xp = 0;
        this.xpToNextLvl = 10;
        this.lvl = 1;
        this.armour = 5;
        this.bonusHp = 0;
        this.playerX = spawnX;
        this.playerY = spawnY;
    }

    public void takeDamage(int damage){
        int damageDelt = Math.max(0, damage - armour);
        hp -= damageDelt;
        if(hp < 0) hp = 0;
    }
    public boolean isAlive(){
        return hp > 0;
    }
    public void getXp(int xpFound){
        xp += xpFound;
        while(xp >= xpToNextLvl) lvlUp();
    }
    public int dealDamage(){
        return attack;
    }
    public void heal(int heal){
        hp += heal;
        if(hp > maxHp) hp = maxHp;
    }
    public void lvlUp(){
        lvl++;
        xp -= xpToNextLvl;
        xpToNextLvl += 10;
        maxHp = 20 * lvl + bonusHp;
        hp = maxHp;
        attack += 3;
        armour ++;
    }
}
