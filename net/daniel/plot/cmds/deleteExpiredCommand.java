package net.daniel.plot.cmds;

import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.intellectualcrafters.plot.object.Plot;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.plotcmd.Utils.MCUtils;

public class deleteExpiredCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.hasPermission("MinePlotCMD.resetExpired")) {

			if (args.length == 0) {

				(new BukkitRunnable() {
					public void run() {

						long start = System.currentTimeMillis();
						ArrayList<Plot> expiredPlots = new ArrayList<Plot>();

						ArrayList<Plot> deletedPlots = new ArrayList<Plot>();
						ArrayList<Plot> failedPlots = new ArrayList<Plot>();

						
						java.util.Set<Plot> allPlots = Main.plotAPI.getAllPlots();

						for (Plot plot : allPlots) {

							long[] temp = MCUtils.getExpireDateAndUpdate(plot);

							if (System.currentTimeMillis() + 1000L > temp[0]) {
								expiredPlots.add(plot);
							}

						}
						
						
						for(Plot plot : expiredPlots) {
														
							
							if (plot.getRunning() > 0) {
								failedPlots.add(plot);
								break;
							}
							
							
							boolean result = plot.deletePlot(new Runnable() {
								@Override
								public void run() {
									plot.removeRunning();
									deletedPlots.add(plot);
								}
							
							});
							if (result) {
								plot.addRunning();
								
							}
						}
						
						
						long took = System.currentTimeMillis() - start;
						
						
						String deleted = Lang.withPlaceHolder(Lang.DELETE_EXPIRED_INFO, new String[] {"%time%", "%deleted_plots%"}, took, deletedPlots);
						
						String failed = Lang.DELETE_EXPIRED_FAILED_RUNNING.toString().replaceAll("%failed_plots%",String.valueOf(failedPlots) );

						sender.sendMessage(deleted);
						System.out.println(deleted);
						sender.sendMessage(failed);
						System.out.println(failed);
					
					}

				}).runTaskAsynchronously(Main.plugin);

				return true;

			} else {

				sender.sendMessage(Lang.DELETE_EXPIRED_HELP.toString());
				return true;
			}

		} else {

			sender.sendMessage(Lang.NO_PERM.toString());
			return true;

		}

	}

}
