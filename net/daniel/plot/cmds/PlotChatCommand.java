package net.daniel.plot.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.User;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.plotcmd.Utils.MCUtils;

public class PlotChatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (MCUtils.checkPlayerPerm(sender, "MinePlotCMD.localChat")) {

			if (args.length >= 1) {

				(new BukkitRunnable() {
					public void run() {

						StringBuilder str = new StringBuilder();
						for (int i = 0; i < args.length; i++) {
							str.append(args[i] + " ");
						}

						String msg = ChatColor.translateAlternateColorCodes('&', str.toString());
						msg = Matcher.quoteReplacement(MCUtils.removeLastChar(msg));

						Player player = (Player) sender;
						Plot playerplot = Main.plotAPI.getPlot(player.getLocation());

						if (playerplot != null) {

							List<PlotPlayer> players = playerplot.getPlayersInPlot();

							if (players.size() > 1) {

								ArrayList<UUID> exclude = new ArrayList<UUID>();
								String chat = Lang.PLOT_CHAT_MSG_FORMAT.toString()
										.replaceAll("%displayName%", player.getDisplayName()).replaceAll("%msg%", msg)
										.replaceAll("%player%", player.getName());

								String spyChat = Lang.PLOT_CHAT_SPY_MSG_FORAMT.toString()
										.replaceAll("%plotID%", playerplot.toString())
										.replaceAll("%displayName%", player.getDisplayName()).replaceAll("%msg%", msg)
										.replaceAll("%player%", player.getName());

								Main.plugin.logger.info(spyChat);
								final User send = Main.essentials.getUser(player);

								if(!send.isMuted()) {
									for (PlotPlayer p : players) {

										final User onlineUser = Main.essentials.getUser(p.getUUID());
																	
										if (!(onlineUser.isIgnoredPlayer(send))) {
											onlineUser.sendMessage(chat);
										}

										exclude.add(p.getUUID());

									}

									for (UUID uuid : Main.spyPlayers) {

										Player p = Bukkit.getPlayer(uuid);

										if (p != null && !exclude.contains(uuid)) {

											p.sendMessage(spyChat);

										}

									}
								}else {
									sender.sendMessage(Lang.PLOTCHAT_MUTED.toString());
								}
											

							} else {
								sender.sendMessage(Lang.PLOTCHAT_NO_PLAYERS.toString());
							}

						} else {
							sender.sendMessage(Lang.NOT_IN_PLOT.toString());
						}

					}

				}).runTaskAsynchronously(Main.plugin);

			} else {
				sender.sendMessage(Lang.PLOT_CHAT_HELP.toString().replaceAll("%command%", label));
			}

		}

		return true;
	}
}