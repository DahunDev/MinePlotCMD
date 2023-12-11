package net.daniel.plot.cmds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.util.UUIDHandler;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;

public class MigrateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.hasPermission("MinePlotCMD.migrate")) {

			if (args.length == 2) {

				(new BukkitRunnable() {
					public void run() {

						UUID from = UUIDHandler.getUUID(args[0], null);

						UUID to = UUIDHandler.getUUID(args[1], null);

						ArrayList<Plot> ownedPlots = new ArrayList<Plot>();
						ArrayList<Plot> trustedPlots = new ArrayList<Plot>();
						ArrayList<Plot> addedPlots = new ArrayList<Plot>();

						java.util.Set<Plot> allPlots = Main.plotAPI.getAllPlots();

						if (from == null || to == null) {

							if (from == null && to == null) {

								sender.sendMessage(Lang.PlAYERNOTFOUND.toString().replaceAll("%player%",
										args[0] + ", " + args[1]));
								return;
							} else if (from == null) {
								sender.sendMessage(Lang.PlAYERNOTFOUND.toString().replaceAll("%player%", args[0]));
								return;

							} else {
								sender.sendMessage(Lang.PlAYERNOTFOUND.toString().replaceAll("%player%", args[1]));
								return;

							}

						}

						for (Plot plot : allPlots) {
							if (plot.isOwner(from) && !plot.isMerged()) {
								ownedPlots.add(plot);
							}

							HashSet<UUID> members = plot.getMembers();

							if (members.contains(from)) {
								addedPlots.add(plot);
							}

							HashSet<UUID> trusteds = plot.getTrusted();

							if (trusteds.contains(from)) {
								trustedPlots.add(plot);
							}

						}

						for (Plot plot : ownedPlots) {
							plot.setOwner(to);
						}

						sender.sendMessage(Lang.withPlaceHolder(Lang.MIGRATED_OWNER,
								new String[] { "%plotlist%", "%from%", "%to%" }, ownedPlots, args[0], args[1]));

						System.out.println(Lang.withPlaceHolder(Lang.MIGRATED_MEMBER_CONSOLE,
								new String[] { "%plotlist%", "%from%", "%to%" }, ownedPlots, args[0], args[1]));

						for (Plot plot : trustedPlots) {
							plot.addTrusted(to);

						}

						sender.sendMessage(Lang.withPlaceHolder(Lang.MIGRATED_TRUSTED,
								new String[] { "%plotlist%", "%from%", "%to%" }, trustedPlots, args[0], args[1]));
						System.out.println(Lang.withPlaceHolder(Lang.MIGRATED_TRUSTED_CONSOLE,
								new String[] { "%plotlist%", "%from%", "%to%" }, trustedPlots, args[0], args[1]));

						for (Plot plot : addedPlots) {
							plot.addMember(to);

						}

						sender.sendMessage(Lang.withPlaceHolder(Lang.MIGRATED_MEMBER,
								new String[] { "%plotlist%", "%from%", "%to%" }, addedPlots, args[0], args[1]));
						System.out.println(Lang.withPlaceHolder(Lang.MIGRATED_MEMBER_CONSOLE,
								new String[] { "%plotlist%", "%from%", "%to%" }, addedPlots, args[0], args[1]));

						return;

					}

				}).runTaskAsynchronously(Main.plugin);

				return true;

			} else {

				sender.sendMessage(Lang.MIGRATE_HELP.toString());
				return true;
			}

		} else {

			sender.sendMessage(Lang.NO_PERM.toString());
			return true;

		}

	}

}
