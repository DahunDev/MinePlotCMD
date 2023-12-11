package net.daniel.plot.cmds;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.util.EventUtil;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.plotsquared.bukkit.util.BukkitUtil;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.Plotcmds.main.PlayerConfirmHolder.TrustConfirm;
import net.daniel.plotcmd.Utils.MCUtils;

public class TrustCommand implements CommandExecutor {

	String OtherPlotPerm = "MinePlotCMD.trust.forOtherPlot";

	private void setConfirm(TrustConfirm trustConfirm, CommandSender sender, String nick, Plot playerplot, Player p,
			UUID uuid, int plotsize, double price) {

		MCUtils.setConfirmCancelled(sender, p, trustConfirm, false);

		if (Main.Eco.getBalance(p) < price * plotsize) {
			sender.sendMessage(Lang.NO_MONEY.toString().replaceAll("%money_need%",
					String.format("%.2f", price * plotsize - Main.Eco.getBalance(p))));
			trustConfirm.isRequested = false;

		} else {

			trustConfirm.isRequested = true;
			trustConfirm.nick = nick;
			trustConfirm.player = p;
			trustConfirm.playerplot = playerplot;
			trustConfirm.price = price;
			trustConfirm.plotsize = plotsize;
			trustConfirm.uuid = uuid;

			trustConfirm.lastReqTime = System.currentTimeMillis() / 1000L;

			
			sender.sendMessage(Lang.withPlaceHolder(Lang.ADD_TRUSTED_CONFIRM,
					new String[] { "%price%", "%plot%", "%target%", "%cmd_confirm%", "%sec%" },
					String.format("%.1f", price * plotsize), trustConfirm.playerplot, nick, "/땅멤버 작업확인",
					Main.confirm_sec));

			MCUtils.cancelConfirmLater(sender, trustConfirm, p);
		}

	}

	private void addTrusted(TrustConfirm trustConfirm, double calcedPrice, CommandSender sender, Player player,
			Plot playerplot, UUID uuid, String nick) {

		if (MCUtils.checkforConfirm(playerplot, sender, player, trustConfirm, OtherPlotPerm)) {

			if (playerplot.getConnectedPlots().size() == trustConfirm.plotsize || !Main.useConfirm_Trust) {

				if (!playerplot.isOwner(uuid)) {
					if (!playerplot.getTrusted().contains(uuid)) {

						if (playerplot.getTrusted().size() + playerplot.getMembers().size()
								+ 1 <= playerplot.getArea().MAX_PLOT_MEMBERS) {
							if (MCUtils.checkBalance(player, calcedPrice, sender, trustConfirm)) {
								if (uuid != DBFunc.everyone) {
									if (!playerplot.removeTrusted(uuid)) {
										if (playerplot.getDenied().contains(uuid)) {
											playerplot.removeDenied(uuid);
										}
									}
								}
								playerplot.addTrusted(uuid);
								EventUtil.manager.callMember(BukkitUtil.getPlayer(player), playerplot, uuid, true);

								Main.Eco.withdrawPlayer(player, calcedPrice);

								sender.sendMessage(Lang.withPlaceHolder(Lang.ADD_TRUSTED,
										new String[] { "%price%", "%target%", "%plot%" }, calcedPrice, nick,
										playerplot));

								System.out.println(Lang.withPlaceHolder(Lang.ADD_TRUSTED_CONSOLE,
										new String[] { "%price%", "%target%", "%plot%", "%player%" }, calcedPrice, nick,
										playerplot, player.getName()));
							}

						} else {

							MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.PLOT_MAX_MEMBERS);
							MCUtils.setConfirmCancelled(sender, player, trustConfirm, false);

							return;
						}

					} else {

						sender.sendMessage(Lang.ALREADY_ADDED.toString().replaceAll("%target%", nick));
						MCUtils.setConfirmCancelled(sender, player, trustConfirm, false);

						return;
					}

				} else {

					sender.sendMessage(Lang.ALREADY_OWNER.toString().replaceAll("%target%", nick));

					MCUtils.setConfirmCancelled(sender, player, trustConfirm, false);

					return;
				}

			} else {

				trustConfirm.isRequested = false;
				sender.sendMessage(Lang.CANCEL_BY_SIZE_CHANGE.toString());

			}
		}

		trustConfirm.isRequested = false;

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (MCUtils.checkPlayerPerm(sender, "MinePlotCMD.trust")) {
			Player p = (Player) sender;
			Location loc = p.getLocation();
			Plot playerplot = Main.plotAPI.getPlot(loc);

			if (args.length == 1) {

				(new BukkitRunnable() {
					public void run() {

						TrustConfirm trustConfirm = Main.getData().get(p.getUniqueId().toString()).trust;

						if (args[0].equalsIgnoreCase("작업확인") || args[0].equalsIgnoreCase("확인")) {

							if (Main.useConfirm_Trust && trustConfirm.isRequested) {

								double ExPrice = trustConfirm.price * trustConfirm.plotsize;

								addTrusted(trustConfirm, ExPrice, sender, trustConfirm.player, trustConfirm.playerplot,
										trustConfirm.uuid, trustConfirm.nick);

							} else {
								trustConfirm.isRequested = false;

								sender.sendMessage(Lang.NOT_REQUESTED_CONFIRM.toString());
								return;
							}
							return;
						}

						if (MCUtils.checkforConfirm(playerplot, sender, p, trustConfirm, OtherPlotPerm)) {
							final java.util.Set<Plot> plots = playerplot.getConnectedPlots();

							// 땅이 합쳐진 경우 합쳐진 만큼 금액 배수 적용 필요

							double price = Main.get().getConfig()
									.getDouble("Price-by-World." + loc.getWorld().getName() + ".trust", Double.NaN);

							if (price == Double.NaN) {

								if (Main.cancelIfConfigNotSet) {
									sender.sendMessage(Lang.CONFIG_NOT_SET.toString());
									System.out.println(Lang.CONFIG_NOT_SET_CONSOLE.toString().replaceAll(
											"%config_node%", "Price-by-World." + loc.getWorld().getName() + ".trust"));
									return;

								} else {
									price = 0.0;

								}
							}

							if (args[0].equalsIgnoreCase("*")) {

								if (Main.useConfirm_Trust) {
									setConfirm(trustConfirm, sender, C.EVERYONE.toString(), playerplot, p,
											DBFunc.everyone,  plots.size(), price);

								} else {
									addTrusted(trustConfirm, price * plots.size(), sender, p, playerplot,
											DBFunc.everyone, C.EVERYONE.toString());

								}
								return;

							} else {
								UUID uuid = UUIDHandler.getUUIDFromString(args[0]);
								if (uuid == null || uuid.toString().isEmpty()) {

									sender.sendMessage(Lang.PlAYERNOTFOUND.toString().replaceAll("%player%", args[0]));

									MCUtils.setConfirmCancelled(sender, p, trustConfirm, false);

									return;

								}

								if (Main.useConfirm_Trust) {

									setConfirm(trustConfirm, sender, args[0], playerplot, p, uuid,  plots.size(),
											price);

								} else {

									addTrusted(trustConfirm, price * plots.size(), sender, p, playerplot, uuid, args[0]	);

								}

							}
						}

					}

				}).runTaskAsynchronously(Main.plugin);

			} else {

				if (args.length > 1) {

					sender.sendMessage(Lang.NO_NAME_SPACE.toString());

				} else {

					MCUtils.sendHelpMessageWithPrice(Lang.ADD_TRUSTED_HELP, Lang.ADD_TRUSTED_PRICE_DEFAULT,
							Lang.ADD_TRUSTED_HELP_DEFAULT, playerplot, sender, "trust", loc);

					return true;

				}

			}
			return true;
		}
		return true;

	}
}
