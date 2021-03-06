package org.xena.plugin.official;

import org.xena.Indexer;
import org.xena.Xena;
import org.xena.cs.ClientState;
import org.xena.cs.GameEntity;
import org.xena.cs.Me;
import org.xena.cs.Player;
import org.xena.keylistener.NativeKeyUtils;
import org.xena.logging.Logger;
import org.xena.plugin.Plugin;
import org.xena.plugin.PluginManifest;
import org.xena.plugin.utils.AngleUtils;

@PluginManifest(name = "Aim Assist", description = "Helps you to stay on target.")
public final class ForceAimPlugin extends Plugin {

    private final AngleUtils aimHelper;

    public ForceAimPlugin(Logger logger, Xena xena) {
        super(logger, xena);
        aimHelper = new AngleUtils(this, 40.5F, 1.7F, 2.5F, 1.7F, 2.5F);
    }

    private final float[] aim = new float[3];

    private Player lastTarget = null;

    @Override
    public void pulse(ClientState clientState, Me me, Indexer<GameEntity> players) {
        if (NativeKeyUtils.isLeftAltDown()) {
            Player target = me.getTarget();
            if (lastTarget != null && target == null) {
                if (!lastTarget.isDead() && lastTarget.isSpotted()) {
                    target = lastTarget;
                } else {
                    lastTarget = null;
                }
            }

            if (target == null) {
                return;
            }

            System.out.println(target + ", " + target.address());


            if (aimHelper.canShoot(me, target)) {
                aimHelper.velocityComp(me, target, target.getBones());
                aimHelper.calculateAngle(me, me.getPosition(), target.getBones(), aim);
                aimHelper.setAngleSmooth(aim, target.getViewAngles());
            } else {
                lastTarget = null;
                return;
            }
            lastTarget = target;
        } else {
            lastTarget = null;
        }
    }

}
