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

		} else {

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

	}

	private void deletePlot(Player player, Plot playerplot, double calcedprice, DeleteConfirm deleteConfirm,
			CommandSender sender) {

		if (playerplot.getRunning() > 0) {
			MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
			MCUtils.setConfirmCancelled(sender, player, deleteConfirm, false);
			return;
		}

		if (MCUtils.checkforConfirm(playerplot, sender, player, deleteConfirm, OtherPlotPerm)) {
			int size = playerplot.getConnectedPlots().size();

			if (deleteConfirm.plotsize == size || !Main.useConfirm_delete) {

				if (MCUtils.checkBalance(player, calcedprice, sender, deleteConfirm)) {
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
					if (result) {
						playerplot.addRunning();
					} else {
						MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
						MCUtils.setConfirmCancelled(sender, player, deleteConfirm, false);

					}
				}

			} else {

				deleteConfirm.isRequested = false;
				sender.sendMessage(Lang.CANCEL_BY_SIZE_CHANGE.toString());
				return;
			}
		}

		deleteConfirm.isRequested = false;

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (MCUtils.checkPlayerPerm(sender, "MinePlotCMD.delete")) {

			Player p = (Player) sender;
			Location loc = p.getLocation();
			Plot playerplot = Main.plotAPI.getPlot(loc);

			DeleteConfirm deleteConfirm = Main.getData().get(p.getUniqueId().toString()).delete;

			(new BukkitRunnable() {
				public void run() {

					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("확인") || args[0].equalsIgnoreCase("작업확인")) {

							if (Main.useConfirm_delete && deleteConfirm.isRequested) {

								double ExPrice = deleteConfirm.price
										* deleteConfirm.playerplot.getConnectedPlots().size();
								deletePlot(deleteConfirm.player, deleteConfirm.playerplot, ExPrice, deleteConfirm,
										sender);

							} else {
								deleteConfirm.isRequested = false;

								sender.sendMessage(Lang.NOT_REQUESTED_CONFIRM.toString());
								return;
							}
							return;

						} else {
							MCUtils.sendHelpMessageWithPrice(Lang.DELETE_PLOT_HELP, Lang.DELETE_HELP_PRICE_DEFAULT,
									Lang.DELETE_PLOT_HELP, playerplot, sender, "delete", loc);

							if (Main.useConfirm_delete) {
								MCUtils.sendHelpMessageWithPrice(Lang.DELETE_CONFIRM_HELP,
										Lang.DELETE_HELP_PRICE_DEFAULT, Lang.DELETE_CONFIRM_HELP, playerplot, sender,
										"delete", loc);
							}

							return;

						}

					} else {
						if (args.length == 0) {

							if (MCUtils.checkforConfirm(playerplot, sender, p, deleteConfirm, OtherPlotPerm)) {
								final java.util.Set<Plot> plots = playerplot.getConnectedPlots();
								MCUtils.setConfirmCancelled(sender, p, deleteConfirm, false);

								double price = Main.get().getConfig().getDouble(
										"Price-by-World." + loc.getWorld().getName() + ".delete", Double.NaN);

								if (price == Double.NaN) {

									if (Main.cancelIfConfigNotSet) {
										sender.sendMessage(Lang.CONFIG_NOT_SET.toString());
										System.out.println(
												Lang.CONFIG_NOT_SET_CONSOLE.toString().replaceAll("%config_node%",
														"Price-by-World." + loc.getWorld().getName() + ".delete"));
										return;

									} else {
										price = 0.0;

									}
								}

								if (MCUtils.checkBalance(p, price * plots.size(), sender, deleteConfirm)) {
									if (Main.useConfirm_delete) {

										MCUtils.setConfirmCancelled(sender, p, deleteConfirm, false);

										setConfirm(deleteConfirm, sender, playerplot, p, plots.size(), price);
									} else {

										deletePlot(p, playerplot, plots.size() * price, deleteConfirm, sender);

										deleteConfirm.isRequested = false;

									}

								}

							} else {
								return;
							}

						} else {

							MCUtils.sendHelpMessageWithPrice(Lang.DELETE_PLOT_HELP, Lang.DELETE_HELP_PRICE_DEFAULT,
									Lang.DELETE_PLOT_HELP_DEFAULT, playerplot, sender, "delete", loc);

							if (Main.useConfirm_delete) {
								MCUtils.sendHelpMessageWithPrice(Lang.DELETE_CONFIRM_HELP,
										Lang.DELETE_HELP_PRICE_DEFAULT, Lang.DELETE_CONFIRM_HELP, playerplot, sender,
										"delete", loc);
							}

						}

					}

				}
			}).runTaskAsynchronously(Main.plugin);
		}

		return true;
	}

}
