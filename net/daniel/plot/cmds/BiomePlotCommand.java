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
import com.intellectualcrafters.plot.util.StringMan;
import com.intellectualcrafters.plot.util.WorldUtil;
import com.plotsquared.bukkit.util.BukkitUtil;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.Plotcmds.main.PlayerConfirmHolder.BiomeConfirm;
import net.daniel.plotcmd.Utils.MCUtils;

public class BiomePlotCommand implements CommandExecutor {

	String OtherPlotPerm = "MinePlotCMD.biome.forOtherPlot";

	private void setConfirm(BiomeConfirm biomeConfirm, CommandSender sender, Plot playerplot, Player p, int plotsize,
			String biome, double price) {

		MCUtils.setConfirmCancelled(sender, p, biomeConfirm, false);

		if (Main.Eco.getBalance(p) < price * plotsize) {
			sender.sendMessage(Lang.NO_MONEY.toString().replaceAll("%money_need%",
					String.format("%.2f", price * plotsize - Main.Eco.getBalance(p))));
			biomeConfirm.isRequested = false;

		} else {
			biomeConfirm.isRequested = true;
			biomeConfirm.player = p;
			biomeConfirm.playerplot = playerplot;
			biomeConfirm.price = price;
			biomeConfirm.plotsize = plotsize;
			biomeConfirm.biome = biome;
			biomeConfirm.lastReqTime = System.currentTimeMillis() / 1000L;

			
			sender.sendMessage(Lang.withPlaceHolder(Lang.BIOME_SET_CONFIRM,
					new String[] { "%price%", "%plot%", "%cmd_confirm%", "%sec%", "%biome%" },
					 String.format("%.1f", price * plotsize), biomeConfirm.playerplot, "/땅바이옴 작업확인",
							Main.confirm_sec, biomeConfirm.biome));

			MCUtils.cancelConfirmLater(sender, biomeConfirm, p);
		}

	}

	private void setbiome(Player player, Plot playerplot, double calcedprice, BiomeConfirm biomeConfirm,
			CommandSender sender, String biome) {

		if (playerplot.getRunning() > 0) {
			MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.WAIT_FOR_TIMER);
			MCUtils.setConfirmCancelled(sender, player, biomeConfirm, false);
			return;

		}

		if (MCUtils.checkforConfirm(playerplot, sender, player, biomeConfirm, OtherPlotPerm)) {
			int size = playerplot.getConnectedPlots().size();

			if (biomeConfirm.plotsize == size || !Main.useConfirm_Biome) {

				if (MCUtils.checkBalance(player, calcedprice, sender, biomeConfirm)) {
					if (Main.biome.set(BukkitUtil.getPlayer(player), playerplot, biome)) {

						Main.Eco.withdrawPlayer(player, calcedprice);
						
						sender.sendMessage(Lang.withPlaceHolder(Lang.BIOME_SET,
								new String[] { "%biome%", "%price%" }, biome, calcedprice));

						
						
						System.out.println(Lang.withPlaceHolder(Lang.BIOME_SET_CONSOLE,
								new String[] { "%player%", "%price%", "%plot%", "%biome%" },
								 player.getName(), calcedprice, playerplot, biome));
						biomeConfirm.isRequested = false;

					} else {
						MainUtil.sendMessage(BukkitUtil.getPlayer(biomeConfirm.player), C.NEED_BIOME);
						String biomes = StringMan.join(WorldUtil.IMP.getBiomeList(), C.BLOCK_LIST_SEPARATER.s());
						MainUtil.sendMessage(BukkitUtil.getPlayer(player),
								C.SUBCOMMAND_SET_OPTIONS_HEADER.s() + biomes);

						MCUtils.setConfirmCancelled(sender, player, biomeConfirm, false);
						return;

					}
				}

			} else {

				biomeConfirm.isRequested = false;
				sender.sendMessage(Lang.CANCEL_BY_SIZE_CHANGE.toString());

				return;
			}

		}

		biomeConfirm.isRequested = false;

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (MCUtils.checkPlayerPerm(sender, "MinePlotCMD.biome")) {

			(new BukkitRunnable() {
				public void run() {

					Player p = (Player) sender;
					Location loc = p.getLocation();

					Plot playerplot = Main.plotAPI.getPlot(loc);

					if (args.length < 1) {
						MCUtils.sendHelpMessageWithPrice(Lang.BIOME_HELP, Lang.BIOME_HELP_PRICE_DEFAULT,
								Lang.BIOME_HELP_DEFAULT, playerplot, sender, "biome", loc);
						return;
					}

					double price = Main.get().getConfig()
							.getDouble("Price-by-World." + loc.getWorld().getName() + ".biome", Double.NaN);

					BiomeConfirm biomeConfirm = Main.getData().get(p.getUniqueId().toString()).biome;

					if (price == Double.NaN) {

						if (Main.cancelIfConfigNotSet) {
							sender.sendMessage(Lang.CONFIG_NOT_SET.toString());
							System.out.println(Lang.CONFIG_NOT_SET_CONSOLE.toString().replaceAll("%config_node%",
									"Price-by-World." + loc.getWorld().getName() + ".biome"));
							return;

						} else {
							price = 0.0;

						}
					}

					if (args[0].equalsIgnoreCase("작업확인") || args[0].equalsIgnoreCase("확인")) {

						if (Main.useConfirm_Biome && biomeConfirm.isRequested) {

							double ExPrice = biomeConfirm.price * biomeConfirm.plotsize;

							setbiome(biomeConfirm.player, biomeConfirm.playerplot, ExPrice, biomeConfirm, sender,
									biomeConfirm.biome);

						} else {
							biomeConfirm.isRequested = false;

							sender.sendMessage(Lang.NOT_REQUESTED_CONFIRM.toString());
							return;
						}
						return;
					} else if (args[0].equalsIgnoreCase("조회") || args[0].equalsIgnoreCase("정보")) {
						if (playerplot != null) {

							sender.sendMessage(Lang.BIOME_INFO.toString().replaceAll("%biome%", playerplot.getBiome()));

						} else {
							sender.sendMessage(Lang.NOT_IN_PLOT.toString());

						}
					} else {

						if (MCUtils.checkforConfirm(playerplot, sender, p, biomeConfirm, OtherPlotPerm)) {
							final java.util.Set<Plot> plots = playerplot.getConnectedPlots();

							if (playerplot.getRunning() > 0) {
								MainUtil.sendMessage(BukkitUtil.getPlayer(p), C.WAIT_FOR_TIMER);
								return;
							}

							if (MCUtils.isVaildBiome(args[0])) {

								if (Main.useConfirm_Biome) {

									setConfirm(biomeConfirm, sender, playerplot, p, plots.size(), args[0], price);

								} else {

									setbiome(p, playerplot, price * plots.size(), biomeConfirm, sender, args[0]);

								}

							} else {
								MCUtils.setConfirmCancelled(sender, p, biomeConfirm, false);

								MainUtil.sendMessage(BukkitUtil.getPlayer(p), C.NEED_BIOME);
								String biomes = StringMan.join(WorldUtil.IMP.getBiomeList(),
										C.BLOCK_LIST_SEPARATER.s());
								MainUtil.sendMessage(BukkitUtil.getPlayer(p),
										C.SUBCOMMAND_SET_OPTIONS_HEADER.s() + biomes);

								return;

							}

						}
					}

				}

			}).runTaskAsynchronously(Main.plugin);
		}

		return true;

	}

}
