
import l2.gameserver.Config;
public class TestConfig {
    public static void main(String[] args) {
        Config.load();
        System.out.println("ALLOW_GLOBAL_GK: " + Config.ALLOW_GLOBAL_GK);
        System.out.println("ALLOW_BUFFER: " + Config.ALLOW_BUFFER);
        System.out.println("DeathPenaltyChance: " + Config.ALT_DEATH_PENALTY_C5_CHANCE);
    }
}
