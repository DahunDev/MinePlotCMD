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
import com.intellectualcrafters.plot.util.block.GlobalBlockQueue;
import com.plotsquared.bukkit.util.BukkitUtil;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.Plotcmds.main.PlayerConfirmHolder.ClearConfirm;
import net.daniel.plotcmd.Utils.MCUtils;

public class ClearPlotCommand implements CommandExecutor {

	String OtherPlotPerm = "MinePlotCMD.clear.forOtherPlot";

	private void setConfirm(ClearConfirm clearConfirm, CommandSender sender, Plot playerplot, Player p, int plotsize,
			double price) {

		MCUtils.setConfirmCancelled(sender, p, clearConfirm, false);

		if (Main.Eco.getBalance(p) < price * plotsize) {
			sender.sendMessage(Lang.NO_MONEY.toString().replaceAll("%money_need%",
					String.format("%.2f", price * plotsize - Main.Eco.getBalance(p))));
			clearConfirm.isRequested = false;

		} else {

			clearConfirm.isRequested = true;
			clearConfirm.player = p;
			clearConfirm.playerplot = playerplot;
			clearConfirm.price = price;
			clearConfirm.plotsize = plotsize;
			clearConfirm.lastReqTime = System.currentTimeMillis() / 1000L;

			sender.sendMessage(Lang.withPlaceHolder(Lang.CLEAR_PLOT_CONFIRM,
					new String[] { "%price%", "%plot%", "%cmd_confirm%", "%sec%" },
					new Object[] { String.format("%.1f", price * plotsize), clearConfirm.playerplot, "/땅초기화 작업확인",
							Main.confirm_sec }));

			MCUtils.cancelConfirmLater(sender, clearConfirm, p);
		}

	}

	private void clearPlot(Player player, Plot playerplot, double calcedprice, ClearConfirm clearConfirm,
			CommandSender sender) {

		if (playerplot.getRunning() > 0) {
			MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
			MCUtils.setConfirmCancelled(sender, player, clearConfirm, false);
			return;
		}

		if (MCUtils.checkforConfirm(playerplot, sender, player, clearConfirm, OtherPlotPerm)) {
			int size = playerplot.getConnectedPlots().size();

			if (clearConfirm.plotsize == size || !Main.useConfirm_Clear) {

				if (MCUtils.checkBalance(player, calcedprice, sender, clearConfirm)) {

					long start = System.currentTimeMillis();

					boolean result = playerplot.clear(true, false, new Runnable() {
						@Override
						public void run() {
							playerplot.unlink();
							GlobalBlockQueue.IMP.addTask(new Runnable() {
								@Override
								public void run() {
									playerplot.removeRunning();
									// If the state changes, then mark it as no longer done

									Main.Eco.withdrawPlayer(player, calcedprice);
									clearConfirm.isRequested = false;

									long time = System.currentTimeMillis() - start;

									
									sender.sendMessage(Lang.withPlaceHolder(Lang.CLEAR_PLOT,
											new String[] { "%price%", "%plot%", "%time%" },
											new Object[] { calcedprice, playerplot, time + "ms" }));
									
									
									
									System.out.println(Lang.withPlaceHolder(Lang.CLEAR_PLOT_CONSOLE,
											new String[] { "%price%", "%plot%", "%player%", "%time%" }, calcedprice,
											playerplot, player.getName(), time + "ms"));
									
								}
							});
						}
					});
					if (!result) {
						MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
						MCUtils.setConfirmCancelled(sender, player, clearConfirm, false);

					} else {
						playerplot.addRunning();
					}
				}

			} else {

				sender.sendMessage(Lang.CANCEL_BY_SIZE_CHANGE.toString());
				clearConfirm.isRequested = false;

				return;
			}
		}

		clearConfirm.isRequested = false;

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (MCUtils.checkPlayerPerm(sender, "MinePlotCMD.clear")) {

			Player p = (Player) sender;
			Location loc = p.getLocation();
			Plot playerplot = Main.plotAPI.getPlot(loc);

			ClearConfirm clearConfirm = Main.getData().get(p.getUniqueId().toString()).clear;

			(new BukkitRunnable() {
				public void run() {

					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("확인") || args[0].equalsIgnoreCase("작업확인")) {

							if (Main.useConfirm_Clear && clearConfirm.isRequested) {

								double ExPrice = clearConfirm.price
										* clearConfirm.playerplot.getConnectedPlots().size();
								clearPlot(clearConfirm.player, clearConfirm.playerplot, ExPrice, clearConfirm, sender);

							} else {
								clearConfirm.isRequested = false;

								sender.sendMessage(Lang.NOT_REQUESTED_CONFIRM.toString());
								return;
							}
							return;

						} else {
							MCUtils.sendHelpMessageWithPrice(Lang.CLEAR_PLOT_HELP, Lang.CLEAR_HELP_PRICE_DEFAULT,
									Lang.CLEAR_PLOT_HELP_DEFAULT, playerplot, sender, "clear", loc);

							if (Main.useConfirm_Clear) {
								MCUtils.sendHelpMessageWithPrice(Lang.CLEAR_CONFIRM_HELP, Lang.CLEAR_HELP_PRICE_DEFAULT,
										Lang.CLEAR_CONFIRM_HELP, playerplot, sender, "clear", loc);
							}

							return;

						}

					} else {
						if (args.length == 0) {
							MCUtils.setConfirmCancelled(sender, p, clearConfirm, false);

							if (MCUtils.checkforConfirm(playerplot, sender, p, clearConfirm, OtherPlotPerm)) {
								final java.util.Set<Plot> plots = playerplot.getConnectedPlots();

								double price = Main.get().getConfig()
										.getDouble("Price-by-World." + loc.getWorld().getName() + ".add", Double.NaN);

								if (price == Double.NaN) {

									if (Main.cancelIfConfigNotSet) {
										sender.sendMessage(Lang.CONFIG_NOT_SET.toString());
										System.out.println(
												Lang.CONFIG_NOT_SET_CONSOLE.toString().replaceAll("%config_node%",
														"Price-by-World." + loc.getWorld().getName() + ".add"));
										return;

									} else {
										price = 0.0;

									}
								}

								if (Main.useConfirm_Clear) {

									setConfirm(clearConfirm, sender, playerplot, p, plots.size(), price);
								} else {

									clearPlot(p, playerplot, plots.size() * price, clearConfirm, sender);

									clearConfirm.isRequested = false;

								}

							} else {
								return;
							}

						} else {

							MCUtils.sendHelpMessageWithPrice(Lang.CLEAR_PLOT_HELP, Lang.CLEAR_HELP_PRICE_DEFAULT,
									Lang.CLEAR_PLOT_HELP_DEFAULT, playerplot, sender, "clear", loc);

							if (Main.useConfirm_Clear) {
								MCUtils.sendHelpMessageWithPrice(Lang.CLEAR_CONFIRM_HELP, Lang.CLEAR_HELP_PRICE_DEFAULT,
										Lang.CLEAR_CONFIRM_HELP, playerplot, sender, "clear", loc);
							}

						}

					}

				}
			}).runTaskAsynchronously(Main.plugin);
		}

		return false;
	}

}
