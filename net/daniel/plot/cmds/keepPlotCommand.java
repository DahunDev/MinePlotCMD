package net.daniel.plot.cmds;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Optional;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.plotcmd.Utils.MCUtils;

public class keepPlotCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			if (sender.hasPermission("MinePlotCMD.keep")) {

				(new BukkitRunnable() {

					public void run() {

						Player p = (Player) sender;
						Location loc = p.getLocation();
						Plot playerplot = Main.plotAPI.getPlot(loc);

						if (playerplot != null) {

							if (playerplot.hasOwner()) {

								if (args.length > 0) {

									if (args[0].equalsIgnoreCase("조회") || args[0].equalsIgnoreCase("확인")) {

										sender.sendMessage(MCUtils.getExpireDate(playerplot));

									} else if (args[0].equalsIgnoreCase("갱신")) {

										if (sender.hasPermission("MinePlotCmd.keep.other")
												|| playerplot.isAdded(p.getUniqueId())) {

											double days = Main.get().getConfig().getDouble(
													"Expire-Days-by-World." + loc.getWorld().getName(), Double.NaN);

											if (days == Double.NaN) {

												if (Main.cancelIfConfigNotSet) {
													sender.sendMessage(Lang.CONFIG_NOT_SET.toString());
													System.out.println(Lang.CONFIG_NOT_SET_CONSOLE.toString()
															.replaceAll("%config_node%", "Expire-Days-by-World."
																	+ loc.getWorld().getName()));
													return;

												} else {
													days = 30.0;

												}
											}

											long expire = Math.round(days * 86400000) + System.currentTimeMillis();

											long expire_old = 0L;

											Optional<?> optional = playerplot.getFlag(Flags.KEEP);

											if (optional.isPresent()) {
												if (optional.get() instanceof Long) {
													expire_old = (long) optional.get();
												}
											}

											if (optional.toString().toLowerCase().contains("true")) {
												expire_old = 9223372036854775807L;
											}

											if (expire_old < expire) {

												playerplot.setFlag(Flags.KEEP, expire);

												// playerplot.setFlag(Flags.KEEP, expire);
												sender.sendMessage(Lang.withPlaceHolder(Lang.KEEP_PLOT_UPDATED,
														new String[] { "%expire_date%", "%plot%" },
														MCUtils.msTo_Time(expire), playerplot));

												System.out
														.println(Lang.withPlaceHolder(Lang.KEEP_PLOT_UPDATED_CONSOLE,
																new String[] { "%expire_date%", "%plot%" },
																MCUtils.msTo_Time(expire), playerplot));

											} else {
												sender.sendMessage(Lang.NOT_NEED_UPDATE_EXPIRE_DATE.toString()
														.replaceAll("%expire_date%", MCUtils.msTo_Time(expire_old)));

												return;
											}

										} else {
											sender.sendMessage(Lang.NOT_ALLOWED_PLOT.toString());
										}
									} else {
										sender.sendMessage(Lang.KEEP_PLOT_HELPS.toString());
										return;
									}

								} else {
									sender.sendMessage(Lang.KEEP_PLOT_HELPS.toString());
									return;

								}

							} else {
								sender.sendMessage(Lang.PLOT_OWNER_NOT_SET.toString());
								return;
							}

						} else {
							sender.sendMessage(Lang.NOT_IN_PLOT.toString());
							return;
						}

					}

				}).runTaskAsynchronously(Main.plugin);

			} else {

				sender.sendMessage(Lang.NO_PERM.toString());

				return true;
			}
		} else {
			sender.sendMessage(Lang.INGAME_ONLY.toString());
			return true;

		}

		return true;
	}

}
