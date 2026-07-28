package net.daniel.plot.cmds;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.util.MainUtil;
import com.plotsquared.bukkit.util.BukkitUtil;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.Plotcmds.main.PlayerConfirmHolder.DeleteConfirm;
import net.daniel.plotcmd.Utils.MCUtils;

public class deletePlotCommand implements CommandExecutor {

	String OtherPlotPerm = "MinePlotCMD.delete.forOtherPlot";

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
				String.format("%.1f", price * plotsize), deleteConfirm.playerplot, "/땅삭제 작업확인", Main.confirm_sec,
				deleteConfirm.price * deleteConfirm.plotsize));

		MCUtils.cancelConfirmLater(sender, deleteConfirm, p);
	}

	private void deletePlot(Player player, Plot playerplot, double calcedprice, DeleteConfirm deleteConfirm,
			CommandSender sender) {

		if (playerplot.getRunning() > 0) { //race condition
			MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
			MCUtils.setConfirmCancelled(sender, player, deleteConfirm, false);
			return;
		}

		if (!MCUtils.checkforConfirm(playerplot, sender, player, deleteConfirm, OtherPlotPerm)) {
			deleteConfirm.isRequested = false;
			return;
		}

		int size = playerplot.getConnectedPlots().size();

		if (deleteConfirm.plotsize != size && Main.useConfirm_delete) {
			deleteConfirm.isRequested = false;
			sender.sendMessage(Lang.CANCEL_BY_SIZE_CHANGE.toString());
			return;
		}

		if (!MCUtils.checkBalance(player, calcedprice, sender, deleteConfirm)) {
			deleteConfirm.isRequested = false;
			return;
		}

		final long start = System.currentTimeMillis();

		boolean result = playerplot.deletePlot(new Runnable() {
			@Override
			public void run() {
				playerplot.removeRunning();

				long time = System.currentTimeMillis() - start;

				Main.Eco.withdrawPlayer(player, calcedprice);
				deleteConfirm.isRequested = false;

				sender.sendMessage(Lang.withPlaceHolder(Lang.DELETED_PLOT,
						new String[] { "%price%", "%plot%", "%time%" }, calcedprice, playerplot,
						time + "ms"));

				System.out.println(Lang.withPlaceHolder(Lang.DELETED_PLOT,
						new String[] { "%price%", "%plot%", "%player%", "%time%" }, calcedprice, playerplot,
						player.getName(), time + "ms"));
			}
		});

		if (!result) {
			MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
			MCUtils.setConfirmCancelled(sender, player, deleteConfirm, false);
			return;
		}

		playerplot.addRunning();
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

		(new BukkitRunnable() {
			public void run() {

				if (args.length == 1) {

					if (args[0].equalsIgnoreCase("확인") || args[0].equalsIgnoreCase("작업확인")) {

						if (!Main.useConfirm_delete || !deleteConfirm.isRequested) {
							deleteConfirm.isRequested = false;

							sender.sendMessage(Lang.NOT_REQUESTED_CONFIRM.toString());
							return;
						}

						double ExPrice = deleteConfirm.price
								* deleteConfirm.playerplot.getConnectedPlots().size();

						deletePlot(deleteConfirm.player, deleteConfirm.playerplot, ExPrice, deleteConfirm,
								sender);

						return;
					}

					MCUtils.sendHelpMessageWithPrice(Lang.DELETE_PLOT_HELP, Lang.DELETE_HELP_PRICE_DEFAULT,
							Lang.DELETE_PLOT_HELP, playerplot, sender, "delete", loc);

					if (Main.useConfirm_delete) {
						MCUtils.sendHelpMessageWithPrice(Lang.DELETE_CONFIRM_HELP,
								Lang.DELETE_HELP_PRICE_DEFAULT, Lang.DELETE_CONFIRM_HELP, playerplot, sender,
								"delete", loc);
					}

					return;
				}

				if (args.length > 1) {
					MCUtils.sendHelpMessageWithPrice(Lang.DELETE_PLOT_HELP, Lang.DELETE_HELP_PRICE_DEFAULT,
							Lang.DELETE_PLOT_HELP_DEFAULT, playerplot, sender, "delete", loc);

					if (Main.useConfirm_delete) {
						MCUtils.sendHelpMessageWithPrice(Lang.DELETE_CONFIRM_HELP,
								Lang.DELETE_HELP_PRICE_DEFAULT, Lang.DELETE_CONFIRM_HELP, playerplot, sender,
								"delete", loc);
					}

					return;
				}

				if (!MCUtils.checkforConfirm(playerplot, sender, p, deleteConfirm, OtherPlotPerm)) {
					return;
				}

				final java.util.Set<Plot> plots = playerplot.getConnectedPlots();

				MCUtils.setConfirmCancelled(sender, p, deleteConfirm, false);

				double price = Main.get().getConfig().getDouble(
						"Price-by-World." + loc.getWorld().getName() + ".delete", Double.NaN);

				if (Double.isNaN(price)) {

					if (Main.cancelIfConfigNotSet) {
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

				if (Main.useConfirm_delete) {
					MCUtils.setConfirmCancelled(sender, p, deleteConfirm, false);

					setConfirm(deleteConfirm, sender, playerplot, p, plots.size(), price);
					return;
				}

				deletePlot(p, playerplot, plots.size() * price, deleteConfirm, sender);

				deleteConfirm.isRequested = false;
			}
		}).runTaskAsynchronously(Main.plugin);

		return true;
	}
}
