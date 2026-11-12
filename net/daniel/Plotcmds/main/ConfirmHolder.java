package net.daniel.Plotcmds.main;

import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.object.Plot;

public abstract class ConfirmHolder {
	public boolean isRequested;

	public double price;
	public Player player;
	public Plot playerplot;
	public int plotsize;
	public long lastReqTime;
	public abstract String getCancelMsg();
	public abstract boolean useConfirm();


	ConfirmHolder() { 
		isRequested = false;
	}
}
