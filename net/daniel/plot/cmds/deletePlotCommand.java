package net.daniel.plot.cmds;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.util.MainUtil;
import com.plotsquared.bukkit.util.BukkitUtil;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.Plotcmds.main.PlayerConfirmHolder.DeleteConfirm;
import net.daniel.plotcmd.Utils.MCUtils;

public class deletePlotCommand implements CommandExecutor {

	private final String otherPlotPerm = "MinePlotCMD.delete.forOtherPlot";

	private void setConfirm(DeleteConfirm deleteConfirm, CommandSender sender, Plot playerplot, Player p, int plotsize,
							double price) {

		MCUtils.setConfirmCancelled(sender, p, deleteConfirm, false);

		if (Main.Eco.getBalance(p) < price * plotsize) {
			sender.sendMessage(Lang.NO_MONEY.toString().replaceAll("%money_need%",
					String.format("%.2f", price * plotsize - Main.Eco.getBalance(p))));
			deleteConfirm.isRequested = false;
			return;
		}

		deleteConfirm.isRequested = true;
		deleteConfirm.player = p;
		deleteConfirm.playerplot = playerplot;
		deleteConfirm.price = price;
		deleteConfirm.plotsize = plotsize;
		deleteConfirm.lastReqTime = System.currentTimeMillis() / 1000L;

		sender.sendMessage(Lang.withPlaceHolder(Lang.DELETE_PLOT_CONFIRM,
				new String[] { "%price%", "%plot%", "%cmd_confirm%", "%sec%", "%price%" },
				String.format("%.1f", price * plotsize), deleteConfirm.playerplot, "/땅삭제 작업확인",
				Main.snapshot().confirm_sec, deleteConfirm.price * deleteConfirm.plotsize));

		MCUtils.cancelConfirmLater(sender, deleteConfirm, p);
	}

	private void deletePlot(Player player, Plot playerplot, double calcedprice, DeleteConfirm deleteConfirm,
							CommandSender sender) {

		// 1. Guard against overlapping tasks on this plot
		if (playerplot.getRunning() > 0) {
			MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
			MCUtils.setConfirmCancelled(sender, player, deleteConfirm, false);
			return;
		}

		if (!MCUtils.checkforConfirm(playerplot, sender, player, deleteConfirm, otherPlotPerm)) {
			deleteConfirm.isRequested = false;
			return;
		}

		// 2. Defend against TOCTOU exploit (e.g. merging plots while confirmation is pending)
		int size = playerplot.getConnectedPlots().size();
		if (deleteConfirm.plotsize != size && Main.snapshot().useConfirm_delete) {
			deleteConfirm.isRequested = false;
			sender.sendMessage(Lang.CANCEL_BY_SIZE_CHANGE.toString());
			return;
		}

		if (!MCUtils.checkBalance(player, calcedprice, sender, deleteConfirm)) {
			deleteConfirm.isRequested = false;
			return;
		}

		final long start = System.currentTimeMillis();

		// 3. Extend in-flight reload gate across the asynchronous PlotSquared deletion lifecycle
		final Main.CallbackLease callback = Main.retainCallback();

		// 4. Pre-emptively acquire plot lock to prevent counter underflow or re-entrant race conditions
		playerplot.addRunning();

		// Post-charge settlement: prevent ghost charges on failed block mutations
		if (!MCUtils.withdrawOrNotify(player, calcedprice, sender)) {
			deleteConfirm.isRequested = false;
			playerplot.removeRunning();
			callback.complete();
			return;
		}

		final boolean result;
		try {
			result = playerplot.deletePlot(new Runnable() {
				@Override
				public void run() {
					try {
						// Release internal PlotSquared lock upon completion
						playerplot.removeRunning();
						long time = System.currentTimeMillis() - start;


						deleteConfirm.isRequested = false;
						sender.sendMessage(Lang.withPlaceHolder(Lang.DELETED_PLOT,
								new String[] { "%price%", "%plot%", "%time%" }, calcedprice, playerplot,
								time + "ms"));
						System.out.println(Lang.withPlaceHolder(Lang.DELETED_PLOT,
								new String[] { "%price%", "%plot%", "%player%", "%time%" }, calcedprice, playerplot,
								player.getName(), time + "ms"));
					} finally {
						callback.complete();
					}
				}
			});
		} catch (RuntimeException | Error failure) {
			// Revert lock state if submission throws synchronously
			playerplot.removeRunning();
			callback.complete();
			sender.sendMessage(Lang.DELETE_FAILURE.toString());
			throw failure;
		}

		// 5. Compensate immediately if PlotSquared refuses execution
		if (!result) {
			playerplot.removeRunning();
			callback.complete();
			MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
			MCUtils.setConfirmCancelled(sender, player, deleteConfirm, false);
			return;
		}

		deleteConfirm.isRequested = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!MCUtils.checkPlayerPerm(sender, "MinePlotCMD.delete")) {
			return true;
		}

		Player p = (Player) sender;
		Location loc = p.getLocation();
		Plot playerplot = Main.plotAPI.getPlot(loc);

		DeleteConfirm deleteConfirm = Main.getData().get(p.getUniqueId().toString()).delete;

		// Dispatch off the main tick loop; gate guards ensure consistent snapshot reads
		Main.runGuardedAsync(sender, new Runnable() {
			@Override
			public void run() {

				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("확인") || args[0].equalsIgnoreCase("작업확인")) {
						if (!Main.snapshot().useConfirm_delete || !deleteConfirm.isRequested) {
							deleteConfirm.isRequested = false;
							sender.sendMessage(Lang.NOT_REQUESTED_CONFIRM.toString());
							return;
						}

						double exPrice = deleteConfirm.price * deleteConfirm.playerplot.getConnectedPlots().size();
						deletePlot(deleteConfirm.player, deleteConfirm.playerplot, exPrice, deleteConfirm, sender);
						return;
					}

					MCUtils.sendHelpMessageWithPrice(Lang.DELETE_PLOT_HELP, Lang.DELETE_HELP_PRICE_DEFAULT,
							Lang.DELETE_PLOT_HELP, playerplot, sender, "delete", loc);

					if (Main.snapshot().useConfirm_delete) {
						MCUtils.sendHelpMessageWithPrice(Lang.DELETE_CONFIRM_HELP,
								Lang.DELETE_HELP_PRICE_DEFAULT, Lang.DELETE_CONFIRM_HELP, playerplot, sender,
								"delete", loc);
					}
					return;
				}

				if (args.length > 1) {
					MCUtils.sendHelpMessageWithPrice(Lang.DELETE_PLOT_HELP, Lang.DELETE_HELP_PRICE_DEFAULT,
							Lang.DELETE_PLOT_HELP_DEFAULT, playerplot, sender, "delete", loc);

					if (Main.snapshot().useConfirm_delete) {
						MCUtils.sendHelpMessageWithPrice(Lang.DELETE_CONFIRM_HELP,
								Lang.DELETE_HELP_PRICE_DEFAULT, Lang.DELETE_CONFIRM_HELP, playerplot, sender,
								"delete", loc);
					}
					return;
				}

				if (!MCUtils.checkforConfirm(playerplot, sender, p, deleteConfirm, otherPlotPerm)) {
					return;
				}

				final java.util.Set<Plot> plots = playerplot.getConnectedPlots();
				MCUtils.setConfirmCancelled(sender, p, deleteConfirm, false);

				double price = Main.snapshot().getWorldPrice(loc.getWorld().getName(), "delete", Double.NaN);

				if (Double.isNaN(price)) {
					if (Main.snapshot().cancelIfConfigNotSet) {
						sender.sendMessage(Lang.CONFIG_NOT_SET.toString());
						System.out.println(
								Lang.CONFIG_NOT_SET_CONSOLE.toString().replaceAll("%config_node%",
										"Price-by-World." + loc.getWorld().getName() + ".delete"));
						return;
					}
					price = 0.0;
				}

				if (!MCUtils.checkBalance(p, price * plots.size(), sender, deleteConfirm)) {
					return;
				}

				if (Main.snapshot().useConfirm_delete) {
					MCUtils.setConfirmCancelled(sender, p, deleteConfirm, false);
					setConfirm(deleteConfirm, sender, playerplot, p, plots.size(), price);
					return;
				}

				deletePlot(p, playerplot, plots.size() * price, deleteConfirm, sender);
				deleteConfirm.isRequested = false;
			}
		});

		return true;
	}
}