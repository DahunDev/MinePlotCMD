package net.daniel.plotcmd.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.google.common.base.Optional;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.intellectualcrafters.plot.util.WorldUtil;

import net.daniel.Plotcmds.main.ConfirmHolder;
import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;

public class MCUtils {
	/*
	 * 
	 * private static UUID toUUID(String id) { return
	 * UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" +
	 * id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20,
	 * 32)); }
	 * 
	 */

	public static boolean isLong(String string) {
		try {
			Long.parseLong(string);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	public static boolean isNumber(String string) {
		try {
			Double.parseDouble(string);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	public static boolean checkBalance(Player player, double calcedPrice, CommandSender sender, ConfirmHolder holder) {
		if (Main.Eco.getBalance(player) >= calcedPrice) {

			return true;

		} else {

			if (holder.useConfirm()) {

				sender.sendMessage(Lang.CANCEL_BY_NOMONEY.toString().replaceAll("%money_need%",
						String.format("%.2f", calcedPrice - Main.Eco.getBalance(player))));

				MCUtils.setConfirmCancelled(sender, player, holder, true);

				return false;

			} else {

				sender.sendMessage(Lang.NO_MONEY.toString().replaceAll("%money_need%",
						String.format("%.2f", calcedPrice - Main.Eco.getBalance(player))));

				MCUtils.setConfirmCancelled(sender, player, holder, false);

				return false;

			}

		}
	}

	public static boolean checkforConfirm(Plot playerplot, CommandSender sender, Player p, ConfirmHolder holder,
			String OtherPlotPerm) {

		if (playerplot != null) {

			if (playerplot.hasOwner()) {
				if (playerplot.isOwner(p.getUniqueId()) || sender.hasPermission(OtherPlotPerm)) {

					return true;

				} else {
					sender.sendMessage(Lang.NOT_YOUR_PLOT.toString());
					setConfirmCancelled(sender, p, holder, false);

					return false;
				}

			} else {

				sender.sendMessage(Lang.PLOT_OWNER_NOT_SET.toString());
				setConfirmCancelled(sender, p, holder, false);
				return false;

			}

		} else {

			sender.sendMessage(Lang.NOT_IN_PLOT.toString());
			setConfirmCancelled(sender, p, holder, false);

			return false;

		}
	}

	public static String removeLastChar(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		return str.substring(0, str.length() - 1);
	}

	public static boolean checkPlayerPerm(CommandSender sender, String perm) {
		if (sender instanceof Player) {

			return checkPerm(sender, perm);

		} else {
			sender.sendMessage(Lang.INGAME_ONLY.toString());
			return false;
		}
	}

	public static boolean checkPerm(CommandSender sender, String perm) {
		if (sender.hasPermission(perm)) {
			return true;
		} else {
			sender.sendMessage(Lang.NO_PERM.toString());
			return false;
		}
	}

	public static void setConfirmCancelled(CommandSender sender, Player player, ConfirmHolder holder,
			boolean isSilent) {

		if (holder.isRequested && holder.useConfirm() && !isSilent) {
			sender.sendMessage(holder.getCancelMsg());
		}

		holder.isRequested = false;

	}


	public static boolean isVaildBiome(String biome) {

		int biomeN = WorldUtil.IMP.getBiomeFromString(biome);
		if (biomeN == -1) {
			return false;
		} else {
			return true;
		}

	}

	public static String getPlayerList(Set<UUID> list) {

		StringBuffer players = new StringBuffer();

		int size = list.size();

		if (size < 1 || list.isEmpty()) {

			return Lang.EMPTY_LIST.toString();
		}

		int index = 0;
		for (UUID uuid : list) {

			String name = UUIDHandler.getName(uuid);
			if (uuid == DBFunc.everyone) {
				name = C.EVERYONE.toString();
			}
			if (name == null) {
				players.append(Lang.UNKNOWN.toString());
			} else {
				players.append(name);
			}

			if (index < size - 1) {
				players.append(", ");
			}

			index++;

		}

		return players.toString();

	}

	public static String msTo_Time(long ms) {
		if (ms == Long.MAX_VALUE || ms == 9223372036854775807L) {
			return Lang.FOREVER.toString();
		} else {
			DateFormat format = new SimpleDateFormat(Lang.DATE_FORMAT.toString());

			Date result = new Date(ms);

			if (ms > System.currentTimeMillis()) {
				return Lang.NOT_EXPIRED_COLOR.toString() + format.format(result);

			} else {
				return Lang.EXPIRED_COLOR.toString() + format.format(result);

			}

		}

	}

	public static String getExpireDate(Plot plot) {

		long[] temp = getExpireDateAndUpdate(plot);

		return msTo_Time(temp[0]);

	}

	/**
	 * @param plot 만기일자를 확인 할 플롯
	 * @return 만기일자들; 1번째 데이터는 실질적인 만기일자, 2번째 데이터는 FLAG의 만기일자, 3번째 데이터는 소유자 접속일 기반
	 *         만기일자
	 */

	public static long[] getExpireDateAndUpdate(Plot plot) {

		long[] expireDates = new long[3];
		Optional<?> optional = plot.getFlag(Flags.KEEP);

		if (optional.isPresent()) {

			if (optional.get() instanceof Long) {

				expireDates[1] = (long) optional.get();
			}

		}

		if (optional.toString().toLowerCase().contains("true")) {
			expireDates[1] = 9223372036854775807L;
		}

		Iterator<UUID> owners = plot.getOwners().iterator();

		ArrayList<Long> lastseens = new ArrayList<>();
		while (owners.hasNext()) {
			UUID uuid = owners.next();

			long lastseen = System.currentTimeMillis() - Main.expireManager.getAge(uuid);

			lastseens.add(lastseen);

		}

		Collections.sort(lastseens);

		long lastseen = 0L;

		if (lastseens.size() > 0) {
			lastseen = lastseens.get(lastseens.size() - 1);

		}

		expireDates[2] = lastseen + Main.expire_days_lastseen * 86400000L;

		expireDates[0] = expireDates[1];

		if ((expireDates[2] / 100000) > (expireDates[1] / 100000)) {

			expireDates[0] = expireDates[2];

			plot.setFlag(Flags.KEEP, expireDates[2]);

		}

		return expireDates;
	}

	public static void cancelConfirmLater(CommandSender sender, ConfirmHolder holder, Player player) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, new Runnable() {
			@Override
			public void run() {

				long timepassed = (System.currentTimeMillis() / 1000L) - holder.lastReqTime;

				if (timepassed >= Main.confirm_sec - 0.5) {

					MCUtils.setConfirmCancelled(sender, player, holder, false);

				}

			}

		}, Main.confirm_sec * 20L);
	}

	public static boolean sendHelpMessageWithPrice(Lang help, Lang defaultPrice, Lang defaultHelp, Plot playerplot,
			CommandSender sender, String configarg, Location loc) {

		if (playerplot == null) {
			sender.sendMessage(defaultHelp.toString().replaceAll("%price%", defaultPrice.toString()));
			return true;

		} else {
			String world = loc.getWorld().getName();

			double realprice = Main.get().getConfig().getDouble("Price-by-World." + world + "." + configarg,
					Double.MIN_VALUE);

			if (realprice != Double.MIN_VALUE) {

				realprice = realprice * playerplot.getConnectedPlots().size();
				sender.sendMessage(help.toString().replaceAll("%price%", realprice + ""));

				return true;

			} else {

				if (Main.cancelIfConfigNotSet) {
					sender.sendMessage(Lang.CONFIG_NOT_SET.toString());
					System.out.println(Lang.CONFIG_NOT_SET_CONSOLE.toString().replaceAll("%config_node%",
							"Price-by-World." + world + "." + configarg));
					return false;

				} else {
					sender.sendMessage(help.toString().replaceAll("%price%", "0.0"));
					return true;

				}
			}

		}

	}

	public static String toString(double num) {
		return String.format("%.1f", num);
	}

	public static void replaceAll(StringBuffer builder, String from, String to) {

		if ((!isLong(to)) && isNumber(to)) {
			double val = Double.parseDouble(to);
			to = String.format("%.1f", val);
		}

		int index = builder.indexOf(from);
		while (index != -1) {
			builder.replace(index, index + from.length(), to);
			index += to.length(); // Move to the end of the replacement
			index = builder.indexOf(from, index);
		}
	}

	public static void replaceAll(StringBuffer builder, String from, Object to) {

		int index = builder.indexOf(from);
		String temp = String.valueOf(to);

		if ((!isLong(temp)) && isNumber(temp)) {
			double val = Double.parseDouble(temp);
			temp = String.format("%.1f", val);
		}
		while (index != -1) {

			builder.replace(index, index + from.length(), temp);
			index += temp.length(); // Move to the end of the replacement
			index = builder.indexOf(from, index);
		}
	}

	public static String convertWithIteration(HashMap<Flag<?>, Object> map) {
		StringBuilder mapAsString = new StringBuilder("");
		int count = 0;

		for (Flag<?> key : map.keySet()) {

			mapAsString.append(key.getName() + ":" + map.get(key) + ", ");
			count++;
		}

		if (count > 0) {
			mapAsString.delete(mapAsString.length() - 2, mapAsString.length());

		} else {
			return Lang.EMPTY_LIST.toString();
		}

		return mapAsString.toString();

	}

}
