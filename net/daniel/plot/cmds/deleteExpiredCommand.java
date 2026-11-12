package net.daniel.plot.cmds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.intellectualcrafters.plot.object.Plot;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.plotcmd.Utils.MCUtils;

public class deleteExpiredCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!sender.hasPermission("MinePlotCMD.resetExpired")) {
			sender.sendMessage(Lang.NO_PERM.toString());
			return true;
		}

		if (args.length != 0) {
			sender.sendMessage(Lang.DELETE_EXPIRED_HELP.toString());
			return true;
		}

		// Dispatch off the main tick loop to prevent server-wide freeze during full-world scans
		Main.runGuardedAsync(sender, new Runnable() {
			@Override
			public void run() {

				long start = System.currentTimeMillis();

				List<Plot> expiredPlots = new ArrayList<>();
				java.util.Set<Plot> allPlots = Main.plotAPI.getAllPlots();

				// 1. Identify expired plots safely off-thread
				for (Plot plot : allPlots) {
					long[] temp = MCUtils.getExpireDateAndUpdate(plot);
					if (System.currentTimeMillis() + 1000L > temp[0]) {
						expiredPlots.add(plot);
					}
				}

				if (expiredPlots.isEmpty()) {
					sender.sendMessage("§e만기된 땅이 존재하지 않습니다.");
					return;
				}

				// Thread-safe collections for asynchronous completion collection
				final Queue<Plot> deletedPlots = new ConcurrentLinkedQueue<>();
				final List<Plot> failedPlots = Collections.synchronizedList(new ArrayList<>());

				// CountDownLatch to barrier-synchronize the batch execution before reporting
				final CountDownLatch latch = new CountDownLatch(expiredPlots.size());

				for (final Plot plot : expiredPlots) {
					// Guard against overlapping tasks on this plot
					if (plot.getRunning() > 0) {
						failedPlots.add(plot);
						latch.countDown();
						continue;
					}

					// 2. Pre-emptively acquire plot lock to prevent counter underflow or re-entrant race conditions
					plot.addRunning();

					boolean result = false;
					try {
						result = plot.deletePlot(new Runnable() {
							@Override
							public void run() {
								try {
									// Release internal PlotSquared lock upon completion
									plot.removeRunning();
									deletedPlots.add(plot);
								} finally {
									latch.countDown();
								}
							}
						});
					} catch (Throwable t) {
						result = false;
					}

					// 3. Compensate immediately if PlotSquared refuses execution
					if (!result) {
						plot.removeRunning();
						failedPlots.add(plot);
						latch.countDown();
					}
				}

				// 4. Wait for all asynchronous deletion callbacks to drain safely (max 30 seconds timeout)
				try {
					latch.await(30, TimeUnit.SECONDS);
				} catch (InterruptedException ex) {
					Main.plugin.getLogger().warning("Expired plot cleanup latch interrupted: " + ex);
					Thread.currentThread().interrupt();
				}

				long took = System.currentTimeMillis() - start;

				// Format final consolidated reports thread-safely
				String deleted = Lang.withPlaceHolder(Lang.DELETE_EXPIRED_INFO,
						new String[] { "%time%", "%deleted_plots%" }, took, new ArrayList<>(deletedPlots));

				String failed = Lang.DELETE_EXPIRED_FAILED_RUNNING.toString()
						.replaceAll("%failed_plots%", String.valueOf(failedPlots));

				sender.sendMessage(deleted);
				System.out.println(deleted);

				sender.sendMessage(failed);
				System.out.println(failed);
			}
		});

		return true;
	}
}