package net.daniel.plot.cmds;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.block.GlobalBlockQueue;
import com.plotsquared.bukkit.util.BukkitUtil;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.Plotcmds.main.PlayerConfirmHolder.ClearConfirm;
import net.daniel.plotcmd.Utils.MCUtils;

public class ClearPlotCommand implements CommandExecutor {

	private final String otherPlotPerm = "MinePlotCMD.clear.forOtherPlot";

	private void setConfirm(ClearConfirm clearConfirm, CommandSender sender, Plot playerplot, Player p, int plotsize,
							double price) {

		MCUtils.setConfirmCancelled(sender, p, clearConfirm, false);

		if (Main.Eco.getBalance(p) < price * plotsize) {
			sender.sendMessage(Lang.NO_MONEY.toString().replaceAll("%money_need%",
					String.format("%.2f", price * plotsize - Main.Eco.getBalance(p))));
			clearConfirm.isRequested = false;
			return;
		}

		clearConfirm.isRequested = true;
		clearConfirm.player = p;
		clearConfirm.playerplot = playerplot;
		clearConfirm.price = price;
		clearConfirm.plotsize = plotsize;
		clearConfirm.lastReqTime = System.currentTimeMillis() / 1000L;

		sender.sendMessage(Lang.withPlaceHolder(Lang.CLEAR_PLOT_CONFIRM,
				new String[] { "%price%", "%plot%", "%cmd_confirm%", "%sec%" },
				new Object[] { String.format("%.1f", price * plotsize), clearConfirm.playerplot, "/땅초기화 작업확인",
						Main.snapshot().confirm_sec }));

		MCUtils.cancelConfirmLater(sender, clearConfirm, p);
	}

	private void clearPlot(Player player, Plot playerplot, double calcedprice, ClearConfirm clearConfirm,
						   CommandSender sender) {

		// 1. Guard against overlapping tasks on this plot
		if (playerplot.getRunning() > 0) {
			MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
			MCUtils.setConfirmCancelled(sender, player, clearConfirm, false);
			return;
		}

		if (!MCUtils.checkforConfirm(playerplot, sender, player, clearConfirm, otherPlotPerm)) {
			clearConfirm.isRequested = false;
			return;
		}

		// 2. Defend against TOCTOU exploit (e.g. merging plots while confirmation is pending)
		int size = playerplot.getConnectedPlots().size();
		if (clearConfirm.plotsize != size && Main.snapshot().useConfirm_Clear) {
			sender.sendMessage(Lang.CANCEL_BY_SIZE_CHANGE.toString());
			clearConfirm.isRequested = false;
			return;
		}

		if (!MCUtils.checkBalance(player, calcedprice, sender, clearConfirm)) {
			clearConfirm.isRequested = false;
			return;
		}

		final long start = System.currentTimeMillis();

		// 3. Extend in-flight reload gate across PlotSquared and GlobalBlockQueue completion callbacks
		final Main.CallbackLease callback = Main.retainCallback();

		// 4. Pre-emptively acquire plot lock to prevent counter underflow or re-entrant race conditions
		playerplot.addRunning();
		// Pre-charge settlement
		if (!MCUtils.withdrawOrNotify(player, calcedprice, sender)) {
			clearConfirm.isRequested = false;
			playerplot.removeRunning();
			callback.complete();
			return;
		}

		final boolean result;
		try {
			result = playerplot.clear(true, false, new Runnable() {
				@Override
				public void run() {
					try {
						playerplot.unlink();
						GlobalBlockQueue.IMP.addTask(new Runnable() {
							@Override
							public void run() {
								try {
									// Release internal PlotSquared lock upon completion
									playerplot.removeRunning();

									clearConfirm.isRequested = false;
									long time = System.currentTimeMillis() - start;

									sender.sendMessage(Lang.withPlaceHolder(Lang.CLEAR_PLOT,
											new String[] { "%price%", "%plot%", "%time%" },
											new Object[] { calcedprice, playerplot, time + "ms" }));
									System.out.println(Lang.withPlaceHolder(Lang.CLEAR_PLOT_CONSOLE,
											new String[] { "%price%", "%plot%", "%player%", "%time%" }, calcedprice,
											playerplot, player.getName(), time + "ms"));
								} finally {
									callback.complete();
								}
							}
						});
					} catch (RuntimeException | Error failure) {
						// Roll back lock if queue rejected final task
						sender.sendMessage(Lang.CLEAR_FAILURE.toString());
						playerplot.removeRunning();
						callback.complete();
						throw failure;
					}
				}
			});
		} catch (RuntimeException | Error failure) {
			// Revert lock state if submission throws synchronously
			playerplot.removeRunning();
			callback.complete();
			throw failure;
		}

		// 5. Compensate immediately if PlotSquared refuses execution
		if (!result) {
			playerplot.removeRunning();
			callback.complete();
			MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
			MCUtils.setConfirmCancelled(sender, player, clearConfirm, false);
			return;
		}

		clearConfirm.isRequested = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!MCUtils.checkPlayerPerm(sender, "MinePlotCMD.clear")) {
			return false;
		}

		Player p = (Player) sender;
		Location loc = p.getLocation();
		Plot playerplot = Main.plotAPI.getPlot(loc);

		ClearConfirm clearConfirm = Main.getData().get(p.getUniqueId().toString()).clear;

		// Dispatch off the main tick loop; gate guards ensure consistent snapshot reads
		Main.runGuardedAsync(sender, new Runnable() {
			@Override
			public void run() {

				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("확인") || args[0].equalsIgnoreCase("작업확인")) {
						if (!Main.snapshot().useConfirm_Clear || !clearConfirm.isRequested) {
							clearConfirm.isRequested = false;
							sender.sendMessage(Lang.NOT_REQUESTED_CONFIRM.toString());
							return;
						}

						double exPrice = clearConfirm.price * clearConfirm.playerplot.getConnectedPlots().size();
						clearPlot(clearConfirm.player, clearConfirm.playerplot, exPrice, clearConfirm, sender);
						return;
					}

					MCUtils.sendHelpMessageWithPrice(Lang.CLEAR_PLOT_HELP, Lang.CLEAR_HELP_PRICE_DEFAULT,
							Lang.CLEAR_PLOT_HELP_DEFAULT, playerplot, sender, "clear", loc);

					if (Main.snapshot().useConfirm_Clear) {
						MCUtils.sendHelpMessageWithPrice(Lang.CLEAR_CONFIRM_HELP, Lang.CLEAR_HELP_PRICE_DEFAULT,
								Lang.CLEAR_CONFIRM_HELP, playerplot, sender, "clear", loc);
					}
					return;
				}

				if (args.length > 0) {
					MCUtils.sendHelpMessageWithPrice(Lang.CLEAR_PLOT_HELP, Lang.CLEAR_HELP_PRICE_DEFAULT,
							Lang.CLEAR_PLOT_HELP_DEFAULT, playerplot, sender, "clear", loc);

					if (Main.snapshot().useConfirm_Clear) {
						MCUtils.sendHelpMessageWithPrice(Lang.CLEAR_CONFIRM_HELP, Lang.CLEAR_HELP_PRICE_DEFAULT,
								Lang.CLEAR_CONFIRM_HELP, playerplot, sender, "clear", loc);
					}
					return;
				}

				MCUtils.setConfirmCancelled(sender, p, clearConfirm, false);

				if (!MCUtils.checkforConfirm(playerplot, sender, p, clearConfirm, otherPlotPerm)) {
					return;
				}

				final java.util.Set<Plot> plots = playerplot.getConnectedPlots();

				double price = Main.snapshot().getWorldPrice(loc.getWorld().getName(), "clear", Double.NaN);

				if (Double.isNaN(price)) {
					if (Main.snapshot().cancelIfConfigNotSet) {
						sender.sendMessage(Lang.CONFIG_NOT_SET.toString());
						System.out.println(
								Lang.CONFIG_NOT_SET_CONSOLE.toString().replaceAll("%config_node%",
										"Price-by-World." + loc.getWorld().getName() + ".clear"));
						return;
					}
					price = 0.0;
				}

				if (Main.snapshot().useConfirm_Clear) {
					setConfirm(clearConfirm, sender, playerplot, p, plots.size(), price);
					return;
				}

				clearPlot(p, playerplot, plots.size() * price, clearConfirm, sender);
				clearConfirm.isRequested = false;
			}
		});

		return false;
	}
}