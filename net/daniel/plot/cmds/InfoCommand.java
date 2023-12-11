package net.daniel.plot.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Optional;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.MainUtil;
import com.plotsquared.bukkit.util.BukkitUtil;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.plotcmd.Utils.MCUtils;

public class InfoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.hasPermission("MinePlotCmd.info")) {

			if (args.length > 0) {

				Plot plot = null;

				String id = args[0];

				if (sender instanceof Player) {
					Player p = (Player) sender;

					PlotPlayer pp = BukkitUtil.getPlayer(p);
					plot = MainUtil.getPlotFromString(pp, id, false);

				} else {
					plot = MainUtil.getPlotFromString(null, id, false);

				}

				checkAndSendMSG(plot, sender, true);

				return true;

			} else {

				if (sender instanceof Player) {
					Player p = (Player) sender;

					Plot plot = Main.plotAPI.getPlot(p.getLocation());

					checkAndSendMSG(plot, sender, false);

				} else {

					sender.sendMessage(Lang.INGAME_ONLY.toString());
					sender.sendMessage(Lang.PLOT_INFO_HELP.toString());
					return true;

				}

			}

			return true;

		} else {
			sender.sendMessage(Lang.NO_PERM.toString());
			return true;
		}

	}

	private void checkAndSendMSG(Plot plot, CommandSender sender, boolean args) {

		if (plot != null) {

			if (plot.hasOwner()) {

				String biome = plot.getBiome();

				(new BukkitRunnable() {

					@Override
					public void run() {
						sendInfo(plot, sender, biome);
						return;

					}
				}).runTaskAsynchronously(Main.plugin);
			} else {
				sender.sendMessage(Lang.PLOT_OWNER_NOT_SET.toString());
				return;
			}

		} else {

			if (args) {

				sender.sendMessage(Lang.PLOT_INFO_HELP.toString());

			} else {
				sender.sendMessage(Lang.NOT_IN_PLOT.toString());

			}

			return;

		}

	}

	private void sendInfo(Plot plot, CommandSender sender, String biome) {

		StringBuffer info = new StringBuffer();
		info.append(Lang.PLOT_INFO.toString());

		MCUtils.replaceAll(info, "%ID%", plot.getId().x + ";" + plot.getId().y);

		MCUtils.replaceAll(info, "%owner%", MCUtils.getPlayerList(plot.getOwners()));

		MCUtils.replaceAll(info, "%biome%", biome);

		Optional<?> optional = plot.getFlag(Flags.PRICE);

		if (optional.isPresent()) {

			if (optional.get() instanceof Double) {

				double price = (double) optional.get();
				MCUtils.replaceAll(info, "%sell_price%", price);

			}
		} else {
			MCUtils.replaceAll(info, "%sell_price%", Lang.NOT_FOR_SELL.toString());
		}

		if (sender instanceof Player) {
			Player p = (Player) sender;

			MCUtils.replaceAll(info, "%canBuild%",
					(plot.isAdded(p.getUniqueId()) && !plot.isDenied(p.getUniqueId())) + "");
		} else {
			MCUtils.replaceAll(info, "%canBuild%", "false");

		}

		MCUtils.replaceAll(info, "%rate%", plot.getAverageRating() + "");

		MCUtils.replaceAll(info, "%trusted%", MCUtils.getPlayerList(plot.getTrusted()));

		MCUtils.replaceAll(info, "%members%", MCUtils.getPlayerList(plot.getMembers()));

		MCUtils.replaceAll(info, "%denied%", MCUtils.getPlayerList(plot.getDenied()));

		String alias = plot.getAlias();

		if (alias.length() > 0) {
			MCUtils.replaceAll(info, "%ailas%", alias);

		} else {
			MCUtils.replaceAll(info, "%ailas%", Lang.EMPTY_LIST.toString());
		}

		MCUtils.replaceAll(info, "%flags%", MCUtils.convertWithIteration(plot.getFlags()));
		MCUtils.replaceAll(info, "%expire_date%", MCUtils.getExpireDate(plot));

		sender.sendMessage(info.toString());

	}

}
