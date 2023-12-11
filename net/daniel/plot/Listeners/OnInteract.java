package net.daniel.plot.Listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.intellectualcrafters.plot.object.Plot;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;

public class OnInteract implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onClickBlock(PlayerInteractEvent e) {
		
	
		Block block = e.getClickedBlock();
		Player player = e.getPlayer();
		Plot plot = Main.plotAPI.getPlot(player.getLocation());
		if (plot != null && block != null && !plot.isAdded(player.getUniqueId())) {

			if (Main.plugin.getConfig().getStringList("Protection-Blocks").contains(block.getType().toString())
					&& (!player.hasPermission("MinePlotCMD.bypassProtection"))) {

				e.setCancelled(true);
				player.sendMessage(Lang.PLOT_PROTECTED_BLOCK.toString());

			}

		}		
		

	}

}
