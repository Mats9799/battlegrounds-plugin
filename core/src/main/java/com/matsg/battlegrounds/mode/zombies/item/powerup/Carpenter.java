package com.matsg.battlegrounds.mode.zombies.item.powerup;

import com.matsg.battlegrounds.TaskRunner;
import com.matsg.battlegrounds.TranslationKey;
import com.matsg.battlegrounds.InternalsProvider;
import com.matsg.battlegrounds.api.Placeholder;
import com.matsg.battlegrounds.api.Translator;
import com.matsg.battlegrounds.api.entity.GamePlayer;
import com.matsg.battlegrounds.api.game.Game;
import com.matsg.battlegrounds.mode.zombies.Zombies;
import com.matsg.battlegrounds.mode.zombies.component.Barricade;
import com.matsg.battlegrounds.mode.zombies.component.MobSpawn;
import com.matsg.battlegrounds.mode.zombies.item.PowerUpEffect;
import com.matsg.battlegrounds.util.XMaterial;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.function.Consumer;

public class Carpenter implements PowerUpEffect {

    private static final int CARPENTER_POINTS = 200;

    private Game game;
    private int duration;
    private InternalsProvider internals;
    private Material material;
    private String name;
    private TaskRunner taskRunner;
    private Translator translator;
    private Zombies zombies;

    public Carpenter(Game game, Zombies zombies, String name, InternalsProvider internals, TaskRunner taskRunner, Translator translator) {
        this.game = game;
        this.zombies = zombies;
        this.name = name;
        this.internals = internals;
        this.taskRunner = taskRunner;
        this.translator = translator;
        this.duration = 0;
        this.material = XMaterial.OAK_FENCE.parseMaterial();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public void activate(Consumer<PowerUpEffect> callback) {
        Collection<MobSpawn> mobSpawns = zombies.getComponents(MobSpawn.class);

        if (mobSpawns.size() <= 0) {
            return;
        }

        taskRunner.runTaskTimer(new BukkitRunnable() {
            public void run() {
                if (!game.getState().isInProgress()) {
                    cancel();
                    return;
                }

                for (MobSpawn mobSpawn : mobSpawns) {
                    Barricade barricade = mobSpawn.getBarricade();

                    if (barricade != null && !barricade.isClosed() && barricade.getMobs().size() <= 0) {
                        for (Block block : barricade.getBlocks()) {
                            if (block != null && block.getType() == Material.AIR) {
                                // Repair a single block and repeat the loop
                                block.setType(barricade.getMaterial());
                                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType().getId());
                                return;
                            }
                        }
                    }
                }

                for (GamePlayer gamePlayer : game.getPlayerManager().getPlayers()) {
                    gamePlayer.setPoints(gamePlayer.getPoints() + CARPENTER_POINTS);

                    String actionBar = translator.translate(TranslationKey.ACTIONBAR_POINTS_INCREASE.getPath(), new Placeholder("bg_points", CARPENTER_POINTS));
                    internals.sendActionBar(gamePlayer.getPlayer(), actionBar);
                }

                game.updateScoreboard();
                callback.accept(Carpenter.this);
                cancel();
            }
        }, 20, 8);
    }

    public boolean isApplicableForActivation() {
        return zombies.getComponents(Barricade.class).size() > 0;
    }

    public double modifyDamage(double damage) {
        return damage;
    }

    public int modifyPoints(int points) {
        return points;
    }
}
