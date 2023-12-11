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
import net.daniel.Plotcmds.main.PlayerConfirmHolder.AddConfirm;
import net.daniel.plotcmd.Utils.MCUtils;

public class AddCommand implements CommandExecutor {

	String OtherPlotPerm = "MinePlotCMD.add.forOtherPlot";

	private void setConfirm(AddConfirm addConfirm, CommandSender sender, String nick, Plot playerplot, Player p,
			UUID uuid, int memsize, int plotsize, double price) {

		MCUtils.setConfirmCancelled(sender, p, addConfirm, false);

		if (Main.Eco.getBalance(p) < price * plotsize) {
			sender.sendMessage(Lang.NO_MONEY.toString().replaceAll("%money_need%",
					String.format("%.2f", price * plotsize - Main.Eco.getBalance(p))));
			addConfirm.isRequested = false;

		} else {

			addConfirm.isRequested = true;
			addConfirm.nick = nick;
			addConfirm.player = p;
			addConfirm.playerplot = playerplot;
			addConfirm.price = price;
			addConfirm.plotsize = plotsize;
			addConfirm.uuid = uuid;
			addConfirm.lastReqTime = System.currentTimeMillis() / 1000L;

			sender.sendMessage(Lang.withPlaceHolder(Lang.ADD_MEMBER_CONFIRM,
					new String[] { "%price%", "%plot%", "%target%", "%cmd_confirm%", "%sec%" },
					String.format("%.1f", price * plotsize), addConfirm.playerplot, nick, "/땅약식멤버 작업확인",
					Main.confirm_sec));

			MCUtils.cancelConfirmLater(sender, addConfirm, p);

		}

	}

	private void addMember(AddConfirm addConfirm, double calcedPrice, CommandSender sender, Player player,
			Plot playerplot, UUID uuid, String nick) {

		if (MCUtils.checkforConfirm(playerplot, sender, player, addConfirm, OtherPlotPerm)) {

			if (!playerplot.isOwner(uuid)) {

				if (playerplot.getConnectedPlots().size() == addConfirm.plotsize || !Main.useConfirm_Add) {
					if (!playerplot.getMembers().contains(uuid)) {

						if (playerplot.getTrusted().size() + playerplot.getMembers().size()
								+ 1 <= playerplot.getArea().MAX_PLOT_MEMBERS) {

							if (MCUtils.checkBalance(player, calcedPrice, sender, addConfirm)) {
								if (uuid != DBFunc.everyone) {
									if (!playerplot.removeTrusted(uuid)) {
										if (playerplot.getDenied().contains(uuid)) {
											playerplot.removeDenied(uuid);
										}
									}
								}
								playerplot.addMember(uuid);
								EventUtil.manager.callMember(BukkitUtil.getPlayer(player), playerplot, uuid, true);

								Main.Eco.withdrawPlayer(player, calcedPrice);

								sender.sendMessage(Lang.withPlaceHolder(Lang.ADD_MEMBER,
										new String[] { "%price%", "%target%", "%plot%" }, calcedPrice, nick,
										playerplot));

								System.out.println(Lang.withPlaceHolder(Lang.ADD_MEMBER_CONSOLE,
										new String[] { "%price%", "%target%", "%plot%" }, calcedPrice, nick,
										playerplot));

							}

						} else {
							MainUtil.sendMessage(BukkitUtil.getPlayer(player), C.PLOT_MAX_MEMBERS);
							MCUtils.setConfirmCancelled(sender, player, addConfirm, false);

							return;
						}

					} else {

						sender.sendMessage(Lang.ALREADY_ADDED.toString().replaceAll("%target%", nick));

						MCUtils.setConfirmCancelled(sender, player, addConfirm, false);

						return;
					}

				} else {
					addConfirm.isRequested = false;
					sender.sendMessage(Lang.CANCEL_BY_SIZE_CHANGE.toString());

				}

			} else {

				sender.sendMessage(Lang.ALREADY_OWNER.toString().replaceAll("%target%", nick));

				MCUtils.setConfirmCancelled(sender, player, addConfirm, false);

				return;
			}
		}

		addConfirm.isRequested = false;

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (MCUtils.checkPlayerPerm(sender, "MinePlotCMD.add")) {

			Player p = (Player) sender;
			Location loc = p.getLocation();
			Plot playerplot = Main.plotAPI.getPlot(loc);

			if (args.length == 1) {

				
				
				(new BukkitRunnable() {
					public void run() {

						AddConfirm addConfirm = Main.getData().get(p.getUniqueId().toString()).add;

						if (args[0].equalsIgnoreCase("작업확인") || args[0].equalsIgnoreCase("확인")) {

							if (Main.useConfirm_Add && addConfirm.isRequested) {

								double ExPrice = addConfirm.price * addConfirm.plotsize;

								addMember(addConfirm, ExPrice, sender, addConfirm.player, addConfirm.playerplot,
										addConfirm.uuid, addConfirm.nick);

							} else {
								addConfirm.isRequested = false;

								sender.sendMessage(Lang.NOT_REQUESTED_CONFIRM.toString());
								return;
							}
							return;
						}

						if (MCUtils.checkforConfirm(playerplot, sender, p, addConfirm, OtherPlotPerm)) {
							int size = playerplot.getTrusted().size() + playerplot.getMembers().size();

							final java.util.Set<Plot> plots = playerplot.getConnectedPlots();

							// 땅이 합쳐진 경우 합쳐진 만큼 금액 배수 적용 필요

							double price = Main.get().getConfig()
									.getDouble("Price-by-World." + loc.getWorld().getName() + ".add", Double.NaN);

							if (price == Double.NaN) {

								if (Main.cancelIfConfigNotSet) {
									sender.sendMessage(Lang.CONFIG_NOT_SET.toString());
									System.out.println(Lang.CONFIG_NOT_SET_CONSOLE.toString().replaceAll(
											"%config_node%", "Price-by-World." + loc.getWorld().getName() + ".add"));
									return;

								} else {
									price = 0.0;

								}
							}

							if (args[0].equalsIgnoreCase("*")) {

								if (Main.useConfirm_Add) {
									setConfirm(addConfirm, sender, C.EVERYONE.toString(), playerplot, p,
											DBFunc.everyone, size, plots.size(), price);

								} else {
									addMember(addConfirm, price * plots.size(), sender, p, playerplot, DBFunc.everyone,
											C.EVERYONE.toString());

								}

								return;

							} else {
								UUID uuid = UUIDHandler.getUUIDFromString(args[0]);
								if (uuid == null || uuid.toString().isEmpty()) {

									sender.sendMessage(Lang.PlAYERNOTFOUND.toString().replaceAll("%player%", args[0]));

									MCUtils.setConfirmCancelled(sender, p, addConfirm, false);

									return;

								}

								if (Main.useConfirm_Add) {

									setConfirm(addConfirm, sender, args[0], playerplot, p, uuid, size, plots.size(),
											price);

								} else {

									addMember(addConfirm, price * plots.size(), sender, p, playerplot, uuid, args[0]);

								}

							}
						} else {
							return;
						}

					}

				}).runTaskAsynchronously(Main.plugin);

			} else {

				if (args.length > 1) {

					sender.sendMessage(Lang.NO_NAME_SPACE.toString());

				} else {

					MCUtils.sendHelpMessageWithPrice(Lang.ADD_MEMBER_HELP, Lang.ADD_MEMBER_PRICE_DEFAULT,
							Lang.ADD_MEMBER_HELP_DEFAULT, playerplot, sender, "add", loc);

					return true;

				}

			}
			return true;

		}
		return true;

	}
}
