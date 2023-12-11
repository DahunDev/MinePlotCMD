package net.daniel.plot.Listeners;

import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import com.google.common.base.Optional;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.util.EventUtil;
import com.intellectualcrafters.plot.util.expiry.ExpireManager;

import net.daniel.Plotcmds.main.Main;

public class OnPlaceBreak implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlace(BlockPlaceEvent e) {
		updateExpire(e.getPlayer());

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBreak(BlockBreakEvent e) {
		updateExpire(e.getPlayer());

	}

	private void updateExpire(Player player) {

		Plot playerplot = Main.plotAPI.getPlot(player.getLocation());

		if (playerplot != null && playerplot.hasOwner()) {

			double days = Main.get().getConfig().getDouble("Expire-Days-by-World." + playerplot.getWorldName(),
					Double.NaN);

			if (days == Double.NaN) {

				if (Main.cancelIfConfigNotSet) {

					return;

				} else {
					days = 30.0;

				}
			}
			long expire = Math.round(days * 86400000) + System.currentTimeMillis();

			long expire_old = 0L;

			Optional<?> optional = playerplot.getFlag(Flags.KEEP);

			if (optional.isPresent()) {
				if (optional.get() instanceof Long) {
					expire_old = (long) optional.get();
				}
			}

			if (optional.toString().toLowerCase().contains("true")) {
				expire_old = 9223372036854775807L;
			}

			if ((expire_old / 120000) < (expire / 120000)) {

				// playerplot.setFlag(Flags.KEEP, expire) 구현

				if (ExpireManager.IMP != null) {
					ExpireManager.IMP.updateExpired(playerplot);
				}

				boolean result = EventUtil.manager.callFlagAdd(Flags.KEEP, playerplot);
				if (result) {

					// playerplot.setFlag(Flags.KEEP, expire) 의 내부 필요메소드 구현

					Iterator<Plot> var4 = playerplot.getConnectedPlots().iterator();

					while (var4.hasNext()) {
						Plot plot = (Plot) var4.next();
						plot.getFlags().put(Flags.KEEP, expire);
						DBFunc.setFlags(plot, plot.getFlags());
					}

				}

			}

		}

	}

}
