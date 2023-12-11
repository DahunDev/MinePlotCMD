package net.daniel.plot.cmds;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Optional;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.UUIDHandler;

import net.daniel.Plotcmds.main.Lang;
import net.daniel.Plotcmds.main.Main;
import net.daniel.plotcmd.Utils.MCUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("MinePlotCMD.list")) {

			(new BukkitRunnable() {

				@Override
				public void run() {

					if (args.length < 1) {

						if (sender instanceof Player) {

							Player player = (Player) sender;
							String cmdval = label + " " + player.getName();
							sendList(sender, player.getUniqueId(), 1, cmdval, player.getName());

						} else {
							sender.sendMessage(Lang.INGAME_ONLY.toString());
							sender.sendMessage(Lang.PLOT_LIST_OTHER_HELP.toString());
							return;
						}

					} else {

						if (args.length == 2) {

							int page = 1;

							try {

								page = Integer.parseInt(args[1]);

							} catch (Exception e) {

								sender.sendMessage(Lang.NOT_PAGE_NUMBER.toString().replaceAll("%value%", args[1]));
								sender.sendMessage(Lang.PLOT_LIST_OTHER_HELP.toString());
								return;
							}

							if (page > 0) {

								UUID uuid = UUIDHandler.getUUIDFromString(args[0]);

								if (uuid != null && !uuid.toString().isEmpty()) {

									StringBuffer buffer = new StringBuffer(label);
									for (int i = 0; i < args.length - 1; i++) {
										buffer.append(" " + args[i]);
									}

									sendList(sender, uuid, page, buffer.toString(), args[0]);

								} else {
									sender.sendMessage(Lang.PlAYERNOTFOUND.toString().replaceAll("%player%", args[0]));
									return;
								}

							} else {
								sender.sendMessage(Lang.NOT_PAGE_NUMBER.toString().replaceAll("%value%", args[1]));
								sender.sendMessage(Lang.PLOT_LIST_OTHER_HELP.toString());

							}

						} else {

							if (args.length > 2) {
								sender.sendMessage(Lang.NO_NAME_SPACE.toString());
								sender.sendMessage(Lang.PLOT_LIST_OTHER_HELP.toString());

							} else {
								sender.sendMessage(Lang.PAGE_VALUE_NOT_SET.toString());

								sender.sendMessage(
										Lang.PLOT_LIST_MINE_HELP.toString().replaceAll("%player%", sender.getName()));

								sender.sendMessage(Lang.PLOT_LIST_OTHER_HELP.toString());

							}

						}

					}

				}
			}).runTaskAsynchronously(Main.plugin);

		}

		return true;
	}

	private void sendList(CommandSender sender, UUID uuid, int page, String cmd, String searchName) {

		List<Plot> plots = PS.get().sortPlotsByTemp(PS.get().getBasePlots(uuid));

		for (Plot plot : PS.get().getPlots()) {

			if (plot.getTrusted().contains(uuid) || plot.getMembers().contains(uuid)) {
				plots.add(plot);
			}
		}

		int totalSize = plots.size();
		int maxPage = (int) Math.ceil(totalSize / 10.0);
		if(maxPage < 1) {
			maxPage = 1;
		}

		if (page <= maxPage || page  == 1) {

			String currentPage = Lang.withPlaceHolder(Lang.CURRENT_PAGE,
					new String[] { "%current_page%", "%max_page%" }, page, maxPage);

			String head = Lang.withPlaceHolder(Lang.PLOT_LIST_HEADER,
					new String[] { "%page%", "%size%", "%player%" }, currentPage, totalSize, searchName);

			TextComponent plotListInfo = new TextComponent(head + "\n");

		
			if (totalSize > 0) {
				plotListInfo.addExtra(getInfoList(plots, page));

			} else {
				plotListInfo.addExtra(Lang.EMPTY_LIST.toString() + "\n");
			}

			plotListInfo.addExtra(getFooter(plots, page, maxPage, cmd));

			sender.spigot().sendMessage(plotListInfo);

		} else {

			sender.sendMessage(Lang.withPlaceHolder(Lang.OUT_BOUND_PAGE,
					new String[] { "%page%", "%minPage%", "%maxPage%" }, page, 1, maxPage));

			sender.sendMessage(Lang.PLOT_LIST_OTHER_HELP.toString());

		}

	}

	private TextComponent getFooter(List<Plot> plots, int page, int maxPage, String cmd) {

		TextComponent footer = new TextComponent();

		int valSize = Main.values_footer.size();
		for (int i = 0; i < valSize; i++) {

			if (Main.index_Previous.contains(i)) {

				if (page > 1) {
					TextComponent prev = new TextComponent(
							Lang.PAGE_EXIST_COLOR.toString() + Lang.PREVIOUS_PAGE.toString());

					prev.setClickEvent(
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + cmd + " " + String.valueOf(page - 1)));
					footer.addExtra(prev);

				} else {

					footer.addExtra(Lang.PAGE_NONEXIST_COLOR.toString() + Lang.PREVIOUS_PAGE.toString());

				}

			} else if (Main.index_Next.contains(i)) {

				if (page < maxPage) {
					TextComponent next = new TextComponent(
							Lang.PAGE_EXIST_COLOR.toString() + Lang.NEXT_PAGE.toString());
					next.setClickEvent(
							new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + cmd + " " + String.valueOf(page + 1)));

					footer.addExtra(next);

				} else {

					footer.addExtra(Lang.PAGE_NONEXIST_COLOR.toString() + Lang.NEXT_PAGE.toString());

				}

			} else {
				footer.addExtra(Main.values_footer.get(i));

			}

		}

		return footer;

	}

	private TextComponent getInfoList(List<Plot> plots, int page) {

		TextComponent list = new TextComponent();

		int Index = (page - 1) * 10;

		int endIndex = page * 10;

		if (endIndex > plots.size()) {
			endIndex = plots.size();
		}

		while (Index < endIndex) {

			list.addExtra(getPlotInfo(plots.get(Index), Index + 1));
			Index++;

		}

		return list;

	}

	private TextComponent getPlotInfo(Plot plot, int index) {

		TextComponent plotInfo = new TextComponent();

		StringBuffer hover = new StringBuffer(Lang.HOVER_PLOT_INFO.toString());

		MCUtils.replaceAll(hover, "%trusted%", MCUtils.getPlayerList(plot.getTrusted()));

		MCUtils.replaceAll(hover, "%members%", MCUtils.getPlayerList(plot.getMembers()));

		MCUtils.replaceAll(hover, "%flags%", MCUtils.convertWithIteration(plot.getFlags()));

		String hoverInfo = hover.toString();

		TextComponent index_ = getIndexHover(plot, index);
		TextComponent id = getHoverInfo(plot, hoverInfo);

		TextComponent owner = getOwners(plot);

		for (int i = 0; i < Main.values_info.size(); i++) {

			if (Main.index_index.contains(i)) {
				plotInfo.addExtra(index_);
			} else if (Main.index_ID.contains(i)) {
				plotInfo.addExtra(id);
			} else if (Main.index_owner.contains(i)) {
				plotInfo.addExtra(owner);
			} else {
				// String temp = placeholder(Main.values_info.get(i), replacefrom, replaceTo);
				String temp = Main.values_info.get(i)
						.replaceAll("%sell_price%", Lang.VALUE_COLOR.toString() + getPrice(plot))
						.replaceAll("%expire_date%", MCUtils.getExpireDate(plot));

				plotInfo.addExtra(new TextComponent(temp));
			}

		}

		plotInfo.addExtra("\n");

		return plotInfo;

	}

	private TextComponent getIndexHover(Plot plot, int index) {

		String id = plot.toString();

		TextComponent rs = new TextComponent(Lang.INDEX_NUMBER_COLOR.toString() + String.valueOf(index));

		rs.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(Lang.HOVER_LIST_VISIT_CMD.toString().replaceAll("%plot%", id)).create()));

		rs.setClickEvent(
				new ClickEvent(ClickEvent.Action.RUN_COMMAND, Lang.LIST_VISIT_CMD.toString().replaceAll("%plot%", id)));
		return rs;

	}

	private TextComponent getHoverInfo(Plot plot, String info) {
		TextComponent rs = new TextComponent(Lang.LIST_PLOTID_COLOR.toString() + plot.toString());

		rs.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(info).create()));
		rs.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
				Lang.LIST_INFO_CMD.toString().replaceAll("%plot%", plot.toString())));
		return rs;

	}

	private TextComponent getOwners(Plot plot) {
		TextComponent rs = new TextComponent(Lang.LIST_OWNER_COLOR.toString());

		int ownerSize = plot.getOwners().size();

		if (ownerSize < 1) {

			TextComponent t = new TextComponent(Lang.EMPTY_LIST.toString());

			t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(
							Lang.HOVER_ISONLINE.toString().replaceAll("%isOnline%", Lang.OFFLINE.toString()))
									.create()));

			rs.addExtra(t);
			return rs;

		} else {

			int index = 0;
			for (UUID uuid : plot.getOwners()) {

				String name = UUIDHandler.getName(uuid);
				if (name == null) {

					TextComponent t = new TextComponent(Lang.VALUE_COLOR.toString() + Lang.UNKNOWN.toString());
					t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(
									Lang.HOVER_ISONLINE.toString().replaceAll("%isOnline%", Lang.OFFLINE.toString()))
											.create()));

					rs.addExtra(t);

					if (index < ownerSize - 1) {
						rs.addExtra(", ");
					}

				} else {
					PlotPlayer pp = UUIDHandler.getPlayer(uuid);
					if (pp != null) {
						TextComponent t = new TextComponent(Lang.VALUE_COLOR.toString() + name);
						t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder(
										Lang.HOVER_ISONLINE.toString().replaceAll("%isOnline%", Lang.ONLINE.toString()))
												.create()));

						rs.addExtra(t);

						if (index < ownerSize - 1) {
							rs.addExtra(", ");
						}
					} else {
						TextComponent t = new TextComponent(Lang.VALUE_COLOR.toString() + name);
						t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
								Lang.HOVER_ISONLINE.toString().replaceAll("%isOnline%", Lang.OFFLINE.toString()))
										.create()));

						rs.addExtra(t);

						if (index < ownerSize - 1) {
							rs.addExtra(", ");
						}
					}
				}

				index++;
			}

		}

		return rs;

	}

	private String getPrice(Plot plot) {

		Optional<?> optional = plot.getFlag(Flags.PRICE);

		if (optional.isPresent()) {

			if (optional.get() instanceof Double) {

				double price = (double) optional.get();
				return String.format("%.1f", price);

			}
		}
		return Lang.NOT_FOR_SELL.toString();

	}

}
