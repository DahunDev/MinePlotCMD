package net.daniel.plot.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.plotcmd.Utils.MCUtils;

public class PlotChatSpyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (  MCUtils.checkPlayerPerm(sender, "MinePlotCMD.Admin.localChat")   ) {
			
			Player player = (Player) sender;
	

			if(Main.spyPlayers.contains(player.getUniqueId())) {
				
				Main.spyPlayers.remove(player.getUniqueId());

				sender.sendMessage(Lang.PLOT_CHAT_SPY_DISABLED.toString());

			
				
			}else {
				Main.spyPlayers.add(player.getUniqueId());
				
				sender.sendMessage(Lang.PLOT_CHAT_SPY_ENABLED.toString());


				
			}
			
			
			
		}
		
		return true;	
	}
}