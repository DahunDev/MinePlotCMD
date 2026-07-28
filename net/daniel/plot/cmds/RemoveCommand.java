package net.daniel.plot.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.util.EventUtil;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.plotsquared.bukkit.util.BukkitUtil;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.Plotcmds.main.PlayerConfirmHolder.RemoveMemberConfirm;
import net.daniel.Plotcmds.main.PlayerConfirmHolder.RemoveTrustConfirm;
import net.daniel.plotcmd.Utils.MCUtils;

public class RemoveCommand implements CommandExecutor, TabCompleter {

	String undenyPerm = "MinePlotCMD.undeny";
	String unTrustPerm = "MinePlotCMD.unTrust";
	String removeMemberPerm = "MinePlotCMD.removeMember";

	String bypassMemPerm = "MinePlotCMD.removeMember.forOtherPlot";
	String bypassTrustedPerm = "MinePlotCMD.unTrust.forOtherPlot";

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length >= 2) {
			return null; // Default completion
		}

		List<String> list = new ArrayList<>();

		if (sender.hasPermission(undenyPerm)) {
			list.add("차단");
		}

		if (sender.hasPermission(unTrustPerm)) {
			list.add("멤버");
		}

		if (sender.hasPermission(removeMemberPerm)) {
			list.add("약식멤버");
		}

		return list;
	}

	private void setRemoveMemberConfirm(RemoveMemberConfirm memeberConfirm, CommandSender sender, String nick,
			Plot playerplot, Player p, UUID uuid, int plotsize) {

		MCUtils.setConfirmCancelled(sender, p, memeberConfirm, false);

		memeberConfirm.isRequested = true;
		memeberConfirm.nick = nick;
		memeberConfirm.player = p;
		memeberConfirm.playerplot = playerplot;
		memeberConfirm.price = 0;
		memeberConfirm.plotsize = plotsize;
		memeberConfirm.uuid = uuid;
		memeberConfirm.lastReqTime = System.currentTimeMillis() / 1000L;

		sender.sendMessage(Lang.withPlaceHolder(Lang.REMOVE_MEMBER_CONFIRM,
				new String[] { "%plot%", "%target%", "%cmd_confirm%", "%sec%" }, memeberConfirm.playerplot, nick,
				"/땅해제 약식멤버 작업확인", Main.confirm_sec));

		MCUtils.cancelConfirmLater(sender, memeberConfirm, p);
	}

	private void setRemoveTrustConfirm(RemoveTrustConfirm untrustConfirm, CommandSender sender, String nick,
			Plot playerplot, Player p, UUID uuid, int plotsize) {

		MCUtils.setConfirmCancelled(sender, p, untrustConfirm, false);

		untrustConfirm.isRequested = true;
		untrustConfirm.nick = nick;
		untrustConfirm.player = p;
		untrustConfirm.playerplot = playerplot;
		untrustConfirm.price = 0;
		untrustConfirm.plotsize = plotsize;
		untrustConfirm.uuid = uuid;
		untrustConfirm.lastReqTime = System.currentTimeMillis() / 1000L;

		sender.sendMessage(Lang.withPlaceHolder(Lang.UNTRUST_CONFIRM,
				new String[] { "%plot%", "%target%", "%cmd_confirm%", "%sec%" }, untrustConfirm.playerplot, nick,
				"/땅해제 멤버 작업확인", Main.confirm_sec));

		MCUtils.cancelConfirmLater(sender, untrustConfirm, p);
	}

	private void undeny(UUID target, Player player, String nick, Plot playerplot) {

		if (!playerplot.getDenied().contains(target)) {
			player.sendMessage(Lang.ALREADY_UNDENIED.toString().replaceAll("%target%", nick));
			return;
		}

		playerplot.removeDenied(target);

		player.sendMessage(
				Lang.withPlaceHolder(Lang.PLOT_UNDENY, new String[] { "%target%", "%plot%" }, nick, playerplot));

		System.out.println(Lang.withPlaceHolder(Lang.PLOT_UNDENY_CONSOLE,
				new String[] { "%player%", "%target%", "%plot%" }, player.getName(), nick, playerplot));
	}

	private void removeMember(RemoveMemberConfirm memberConfirm, CommandSender sender, Player player, Plot playerplot,
			UUID uuid, String nick) {

		if (!MCUtils.checkforConfirm(playerplot, sender, player, memberConfirm, bypassMemPerm)) {
			memberConfirm.isRequested = false;
			return;
		}

		if (playerplot.getConnectedPlots().size() != memberConfirm.plotsize
				&& Main.useConfirm_removeMember) {

			memberConfirm.isRequested = false;
			sender.sendMessage(Lang.CANCEL_BY_SIZE_CHANGE.toString());
			return;
		}

		if (!playerplot.getMembers().contains(uuid)) {
			sender.sendMessage(Lang.NOT_MEMBER.toString().replaceAll("%target%", nick));

			MCUtils.setConfirmCancelled(sender, player, memberConfirm, false);
			return;
		}

		playerplot.removeMember(uuid);

		sender.sendMessage(Lang.withPlaceHolder(Lang.PLOT_REMOVE_MEMBER,
				new String[] { "%target%", "%plot%" }, nick, playerplot));

		System.out.println(Lang.withPlaceHolder(Lang.PLOT_REMOVE_MEMBER_CONSOLE,
				new String[] { "%player%", "%target%", "%plot%" }, player.getName(), nick, playerplot));

		memberConfirm.isRequested = false;
	}

	private void unTrust(RemoveTrustConfirm untrustConfirm, CommandSender sender, Player player, Plot playerplot,
			UUID uuid, String nick) {

		if (!MCUtils.checkforConfirm(playerplot, sender, player, untrustConfirm, bypassTrustedPerm)) {
			untrustConfirm.isRequested = false;
			return;
		}

		if (playerplot.getConnectedPlots().size() != untrustConfirm.plotsize
				&& Main.useConfirm_unTrust) {

			untrustConfirm.isRequested = false;
			sender.sendMessage(Lang.CANCEL_BY_SIZE_CHANGE.toString());
			return;
		}

		if (!playerplot.getTrusted().contains(uuid)) {
			sender.sendMessage(Lang.NOT_TRUSTED.toString().replaceAll("%target%", nick));

			MCUtils.setConfirmCancelled(sender, player, untrustConfirm, false);
			return;
		}

		playerplot.removeTrusted(uuid);
		EventUtil.manager.callMember(BukkitUtil.getPlayer(player), playerplot, uuid, true);

		sender.sendMessage(Lang.withPlaceHolder(Lang.PLOT_UNTRUST,
				new String[] { "%target%", "%plot%" }, nick, playerplot));

		System.out.println(Lang.withPlaceHolder(Lang.PLOT_UNTRUST_CONSOLE,
				new String[] { "%player%", "%target%", "%plot%" }, player.getName(), nick, playerplot));

		untrustConfirm.isRequested = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player p = (Player) sender;
		Location loc = p.getLocation();
		Plot playerplot = Main.plotAPI.getPlot(loc);

		if (args.length < 1) {
			sender.sendMessage(Lang.PLOT_REMOVE_HELP.toString());
			return true;
		}

		(new BukkitRunnable() {
			public void run() {

				switch (args[0]) {
				case "차단":

					if (!MCUtils.checkPlayerPerm(sender, undenyPerm)) {
						break;
					}

					if (playerplot == null) {
						sender.sendMessage(Lang.NOT_IN_PLOT.toString());
						return;
					}

					if (!playerplot.hasOwner()) {
						sender.sendMessage(Lang.PLOT_OWNER_NOT_SET.toString());
						return;
					}

					if (!playerplot.isOwner(p.getUniqueId())
							&& !sender.hasPermission("MinePlotCMD.undeny.forOtherPlot")) {

						sender.sendMessage(Lang.NOT_YOUR_PLOT.toString());
						return;
					}

					if (args.length != 2) {
						sender.sendMessage(Lang.PLOT_UNDENY_HELP.toString());
						return;
					}

					if (args[1].equalsIgnoreCase("*")) {
						undeny(DBFunc.everyone, p, C.EVERYONE.toString(), playerplot);
						return;
					}

					UUID target = UUIDHandler.getUUIDFromString(args[1]);

					if (target == null || target.toString().length() <= 0) {
						sender.sendMessage(
								Lang.PlAYERNOTFOUND.toString().replaceAll("%player%", args[1]));
						return;
					}

					undeny(target, p, args[1], playerplot);
					return;

				case "멤버":

					if (!MCUtils.checkPlayerPerm(sender, unTrustPerm)) {
						break;
					}

					RemoveTrustConfirm unTrustConfirm = Main.getData()
							.get(p.getUniqueId().toString()).removeTrust;

					if (!MCUtils.checkforConfirm(playerplot, sender, p, unTrustConfirm, bypassTrustedPerm)) {
						break;
					}

					if (args.length != 2) {

						if (args.length > 2) {
							sender.sendMessage(Lang.NO_NAME_SPACE.toString());
						} else {
							sender.sendMessage(Lang.PLOT_UNTRUST_HELP.toString());
						}

						break;
					}

					if (args[1].equalsIgnoreCase("작업확인")
							|| args[1].equalsIgnoreCase("확인")) {

						if (!Main.useConfirm_unTrust || !unTrustConfirm.isRequested) {
							unTrustConfirm.isRequested = false;

							sender.sendMessage(Lang.NOT_REQUESTED_CONFIRM.toString());
							return;
						}

						unTrust(unTrustConfirm, sender, unTrustConfirm.player,
								unTrustConfirm.playerplot, unTrustConfirm.uuid,
								unTrustConfirm.nick);

						return;
					}

					final java.util.Set<Plot> plots = playerplot.getConnectedPlots();

					// 땅이 합쳐진 경우 합쳐진 만큼 금액 배수 적용 필요

					if (args[1].equalsIgnoreCase("*")) {

						if (Main.useConfirm_Add) {
							setRemoveTrustConfirm(unTrustConfirm, sender, C.EVERYONE.toString(),
									playerplot, p, DBFunc.everyone, plots.size());
						} else {
							unTrust(unTrustConfirm, sender, p, playerplot, DBFunc.everyone,
									C.EVERYONE.toString());
						}

						return;
					}

					UUID uuid = UUIDHandler.getUUIDFromString(args[1]);

					if (uuid == null || uuid.toString().isEmpty()) {
						sender.sendMessage(
								Lang.PlAYERNOTFOUND.toString().replaceAll("%player%", args[1]));

						MCUtils.setConfirmCancelled(sender, p, unTrustConfirm, false);
						return;
					}

					if (Main.useConfirm_unTrust) {
						setRemoveTrustConfirm(unTrustConfirm, sender, args[1], playerplot, p,
								uuid, plots.size());
					} else {
						unTrust(unTrustConfirm, sender, p, playerplot, uuid, args[1]);
					}

					break;

				case "약식멤버":

					if (!MCUtils.checkPlayerPerm(sender, removeMemberPerm)) {
						break;
					}

					RemoveMemberConfirm memberConfirm = Main.getData()
							.get(p.getUniqueId().toString()).removeMember;

					if (!MCUtils.checkforConfirm(playerplot, sender, p, memberConfirm, bypassMemPerm)) {
						break;
					}

					if (args.length != 2) {

						if (args.length > 2) {
							sender.sendMessage(Lang.NO_NAME_SPACE.toString());
						} else {
							sender.sendMessage(Lang.PLOT_UNTRUST_HELP.toString());
						}

						break;
					}

					if (args[1].equalsIgnoreCase("작업확인")
							|| args[1].equalsIgnoreCase("확인")) {

						if (!Main.useConfirm_removeMember || !memberConfirm.isRequested) {
							memberConfirm.isRequested = false;

							sender.sendMessage(Lang.NOT_REQUESTED_CONFIRM.toString());
							return;
						}

						removeMember(memberConfirm, sender, memberConfirm.player,
								memberConfirm.playerplot, memberConfirm.uuid, memberConfirm.nick);

						return;
					}

					final java.util.Set<Plot> memberPlots = playerplot.getConnectedPlots();

					// 땅이 합쳐진 경우 합쳐진 만큼 금액 배수 적용 필요

					if (args[1].equalsIgnoreCase("*")) {

						if (Main.useConfirm_removeMember) {
							setRemoveMemberConfirm(memberConfirm, sender, C.EVERYONE.toString(),
									playerplot, p, DBFunc.everyone, memberPlots.size());
						} else {
							removeMember(memberConfirm, sender, p, playerplot, DBFunc.everyone,
									C.EVERYONE.toString());
						}

						return;
					}

					UUID memberUuid = UUIDHandler.getUUIDFromString(args[1]);

					if (memberUuid == null || memberUuid.toString().isEmpty()) {
						sender.sendMessage(
								Lang.PlAYERNOTFOUND.toString().replaceAll("%player%", args[1]));

						MCUtils.setConfirmCancelled(sender, p, memberConfirm, false);
						return;
					}

					if (Main.useConfirm_removeMember) {
						setRemoveMemberConfirm(memberConfirm, sender, args[1], playerplot, p,
								memberUuid, memberPlots.size());
					} else {
						removeMember(memberConfirm, sender, p, playerplot, memberUuid, args[1]);
					}

					break;

				default:
					sender.sendMessage(Lang.PLOT_REMOVE_HELP.toString());
					break;
				}
			}

		}).runTaskAsynchronously(Main.plugin);

		return true;
	}
}
