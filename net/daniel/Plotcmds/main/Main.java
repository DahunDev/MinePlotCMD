
package net.daniel.Plotcmds.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.Essentials;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.commands.Biome;
import com.intellectualcrafters.plot.util.expiry.ExpireManager;

import net.daniel.plot.Listeners.OnInteract;
import net.daniel.plot.Listeners.OnPlaceBreak;
import net.daniel.plot.cmds.AddCommand;
import net.daniel.plot.cmds.BiomePlotCommand;
import net.daniel.plot.cmds.ClearPlotCommand;
import net.daniel.plot.cmds.InfoCommand;
import net.daniel.plot.cmds.ListCommand;
import net.daniel.plot.cmds.MigrateCommand;
import net.daniel.plot.cmds.PlotChatCommand;
import net.daniel.plot.cmds.PlotChatSpyCommand;
import net.daniel.plot.cmds.RemoveCommand;
import net.daniel.plot.cmds.TrustCommand;
import net.daniel.plot.cmds.deleteExpiredCommand;
import net.daniel.plot.cmds.deletePlotCommand;
import net.daniel.plot.cmds.keepPlotCommand;
import net.daniel.plotcmd.Utils.FileUtils;
import net.daniel.plotcmd.Utils.ThrowsRunnable;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {
	private final FileConfiguration langConfig = new YamlConfiguration();

	public final Logger logger;
	public static ExpireManager expireManager;
	public static Economy Eco;
	public static Essentials essentials;

	public static PlotAPI plotAPI;

	public static Biome biome;

	private static String pluginName;
	private static HashMap<String, PlayerConfirmHolder> data = new HashMap<String, PlayerConfirmHolder>();
	public static Main plugin;

	public static int confirm_sec;
	public static boolean useConfirm_Add;
	public static boolean useConfirm_Trust;
	public static boolean useConfirm_Biome;
	public static boolean useConfirm_Clear;
	public static boolean useConfirm_delete;
	public static boolean useConfirm_unTrust;
	public static boolean useConfirm_removeMember;
	public static boolean cancelIfConfigNotSet;
	public static int expire_days_lastseen;
	
	public static ArrayList<UUID> spyPlayers;

	public static ArrayList<String> values_info = new ArrayList<String>();
	public static ArrayList<Integer> index_index = new ArrayList<Integer>();
	public static ArrayList<Integer> index_ID = new ArrayList<Integer>();
	public static ArrayList<Integer> index_owner = new ArrayList<Integer>();

	public static ArrayList<String> values_footer = new ArrayList<String>();
	public static ArrayList<Integer> index_Previous = new ArrayList<Integer>();
	public static ArrayList<Integer> index_Next = new ArrayList<Integer>();

	public Main() {
		this.logger = Logger.getLogger("Minecraft");
		plugin = this;

	}

	@Override
	public void onDisable() {
		final PluginDescriptionFile pdFile = this.getDescription();
		data.clear();
		System.out.println(String.valueOf(pdFile.getName()) + " " + pdFile.getVersion() + "이(가) 비활성화 되었습니다.");

	}

	@Override
	public void onEnable() {

		Plugin essentialsPlugin = Bukkit.getPluginManager().getPlugin("Essentials");

		plotAPI = new PlotAPI();
		biome = new Biome();
		
		expireManager = new ExpireManager();
		Main.essentials = (Essentials) essentialsPlugin;
		this.reloadConfiguration();

		final PluginDescriptionFile pdFile = this.getDescription();
		Main.pluginName = pdFile.getName();
		if (!this.SetupEconomy()) {
			Bukkit.getConsoleSender().sendMessage("§6§l[ Mine Plot CMD ] §c§lEconomy §f플러그인이 인식되지 않았으므로, 비활성화 됩니다.");
			this.getServer().getPluginManager().disablePlugin((Plugin) this);
			return;
		}
		

		spyPlayers = new ArrayList<UUID>();
		
		this.getCommand("땅초기화").setExecutor(new ClearPlotCommand());
		this.getCommand("땅삭제").setExecutor(new deletePlotCommand());
		this.getCommand("땅바이옴").setExecutor(new BiomePlotCommand());

		this.getCommand("땅멤버").setExecutor(new TrustCommand());
		this.getCommand("땅약식멤버").setExecutor(new AddCommand());
		this.getCommand("땅계정이전").setExecutor(new MigrateCommand());
		this.getCommand("땅보존기간").setExecutor(new keepPlotCommand());
		this.getCommand("땅정보").setExecutor(new InfoCommand());
		this.getCommand("땅목록").setExecutor(new ListCommand());
		
		
		this.getCommand("땅해제").setExecutor(new RemoveCommand());
		
		this.getCommand("만기일자지난땅청소").setExecutor(new deleteExpiredCommand());
		
		List<String> chatCMDs = new ArrayList<String>();
		
		chatCMDs.add("pc");
		chatCMDs.add("plotchat");
		chatCMDs.add("플롯채팅");
		
		getCommand("땅채팅").setExecutor(new PlotChatCommand());
		getCommand("땅채팅").setAliases(chatCMDs);
		

		this.getCommand("땅채팅스파이").setExecutor(new PlotChatSpyCommand());

		for (Player p : getServer().getOnlinePlayers()) {
			Main.getData().put(p.getUniqueId().toString(), new PlayerConfirmHolder());
		}
			
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new OnPlaceBreak(), this);
		getServer().getPluginManager().registerEvents(new OnInteract(), this);

		splitJSON(); 
		getLogger().info(String.valueOf(pdFile.getFullName() + "이(가) 활성화 되었습니다."));

	}

	private void splitJSON() {

		StringBuffer PLOT_INFO = new StringBuffer(Lang.PLOT_INFO_FOR_LIST.toString());
		initMSG_OF_PLOT_INFO(PLOT_INFO);

		StringBuffer FOOTER = new StringBuffer(Lang.PLOT_LIST_FOOTER.toString());
		initMSG_OF_LIST_FOOTER(FOOTER);

	
	}

	public static Main get() {
		return (Main) Bukkit.getPluginManager().getPlugin(pluginName);
	}

	public File createLangFile() {
		return new File(getDataFolder(), "lang.yml");
	}

	private void doInputOutput(ThrowsRunnable runnable, String errorMessage) {
		try {
			runnable.run();
		} catch (FileNotFoundException ex) {
			// Ignore
		} catch (Exception ex) {
			getLogger().log(Level.WARNING, errorMessage, ex);
		}
	}

	public FileConfiguration getLangConfig() {
		return langConfig;
	}

	public void loadConfigurations() {
		// saveResource("gui.yml", false);
		doInputOutput(() -> langConfig.load(createLangFile()), "Exception while lang load.");

	}

	public void saveConfigurations() {

		doInputOutput(() -> langConfig.save(FileUtils.writeEnsure(createLangFile())), "Exception when lang save.");
	}

	private boolean SetupEconomy() {
		if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
			Bukkit.getConsoleSender().sendMessage("§6§l[ Mine Plot CMD ] §c§lVault §f플러그인이 인식되지 않았으므로, 서버가 종료 됩니다.");
			Bukkit.shutdown();
			return false;
		}
		Bukkit.getConsoleSender().sendMessage("§6§l[ Mine Plot CMD ] §a§lVault §f플러그인이 인식 되었습니다.");
		RegisteredServiceProvider<Economy> EconomyProvider = this.getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (EconomyProvider != null) {
			Eco = (Economy) EconomyProvider.getProvider();
		}
		if (Eco != null) {
			return true;
		}
		return false;
	}


	public File createConfigFile() {
		return new File(getDataFolder(), "config.yml");
	}

	public void reloadConfiguration() {
		PluginDescriptionFile pdFile = this.getDescription();
		File config = new File("plugins/" + pdFile.getName() + "/config.yml");
		if (config.exists()) {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);
			this.saveDefaultConfig();
			for (String key : cfg.getConfigurationSection("").getKeys(true)) {
				if (!this.getConfig().contains(key)) {
					this.getConfig().set(key, cfg.get(key));
				}
			}
		} else {
			this.saveDefaultConfig();
		}
		this.reloadConfig();

		Main.useConfirm_Add = getConfig().getBoolean("UseConfirm.Add", false);
		Main.useConfirm_Biome = getConfig().getBoolean("UseConfirm.Biome", false);
		Main.useConfirm_Trust = getConfig().getBoolean("UseConfirm.Trust", false);
		Main.useConfirm_Clear = getConfig().getBoolean("UseConfirm.Clear", true);
		Main.useConfirm_delete = getConfig().getBoolean("UseConfirm.Delete", true);
		Main.useConfirm_removeMember = getConfig().getBoolean("UseConfirm.RemoveMember", false);
		Main.useConfirm_unTrust = getConfig().getBoolean("UseConfirm.UnTrust", false);
		
		
		Main.cancelIfConfigNotSet = getConfig().getBoolean("cancelCMDIfConfigNotSet", true);
		Main.confirm_sec = getConfig().getInt("confirmSec", 15);
		Main.expire_days_lastseen = getConfig().getInt("Expire_Days_After_OWNER_JOIN", 30);

		
		
		loadConfigurations();
		Lang.init(langConfig);
		saveConfigurations();

	}

	public static boolean isNumber(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void JoinEvent(PlayerLoginEvent e) {

		Main.getData().put(e.getPlayer().getUniqueId().toString(), new PlayerConfirmHolder());

	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel,
			final String[] args) {

		if (commandLabel.equalsIgnoreCase("Mineplotcmd")) {
			if (sender.hasPermission("MinePlotCMD.reload")) {
				if (args.length == 0) {

					sender.sendMessage("§b§l[ §f§lMine Plot CMD §b§l] §e/Mineplotcmd reload §f: 플러그인 설정 리로드");
					return true;

				} else {
					if (args[0].equalsIgnoreCase("reload")) {

						(new BukkitRunnable() {
							public void run() {

								reloadConfiguration();
								splitJSON();
							}

						}).runTaskLaterAsynchronously(this, 0L);

						sender.sendMessage("§b§l[ §f§lMine Plot CMD §b§l] §f플러그인 설정 리로드 완료");
						return true;

					} else {

						sender.sendMessage("§b§l[ §f§lMine Plot CMD §b§l] §e/Minecmd reload §f: 플러그인 설정 리로드");
						return true;

					}

				}

			} else {

				sender.sendMessage("§b§l[ §f§lMine SV §b§l] §c권한이 없습니다. 필요한 권한: MinePlotCMD.reload");
				return true;

			}
		}

		return false;

	}

	public static HashMap<String, PlayerConfirmHolder> getData() {
		return data;
	}

	// 현재는 불필요
	public void setData(HashMap<String, PlayerConfirmHolder> data) {
		Main.data = data;
	}

	private void initMSG_OF_PLOT_INFO(StringBuffer val) {
		Main.values_info.clear();
		Main.index_index.clear();
		Main.index_ID.clear();
		Main.index_owner.clear();
		int arg = 0;

		while (contains(val, "%index%") || contains(val, "%plotID%") || contains(val, "%owner%")) {

			String check = getClosest_INFO(val);
			int length = check.length();
			int index = val.indexOf(check);

			if (index == 0) {
				Main.values_info.add(check);

				val.delete(0, index + length);

				switch (check) {
				case "%index%":
					Main.index_index.add(arg);
					break;

				case "%plotID%":
					Main.index_ID.add(arg);
					break;

				case "%owner%":
					Main.index_owner.add(arg);
					break;

				default:
					break;
				}

				arg++;

			} else {

				Main.values_info.add(val.substring(0, index));

				switch (check) {
				case "%index%":
					Main.index_index.add(arg + 1);
					break;

				case "%plotID%":
					Main.index_ID.add(arg + 1);
					break;

				case "%owner%":
					Main.index_owner.add(arg + 1);
					break;

				default:
					break;
				}

				Main.values_info.add(check);
				val.delete(0, index + length);

				arg += 2;

			}

		}

		Main.values_info.add(val.toString());

	}

	private void initMSG_OF_LIST_FOOTER(StringBuffer footer) {

		Main.values_footer.clear();
		Main.index_Previous.clear();
		Main.index_Next.clear();

		int arg = 0;

		while (contains(footer, "%previous%") || contains(footer, "%next%")) {

			String check = getClosest_Footer(footer);
			int length = check.length();
			int index = footer.indexOf(check);

			if (index == 0) {
				Main.values_footer.add(check);

				footer.delete(0, index + length);

				switch (check) {
				case "%previous%":
					Main.index_Previous.add(arg);
					break;

				case "%next%":
					Main.index_Next.add(arg);
					break;

				default:
					break;
				}

				arg++;

			} else {

				Main.values_footer.add(footer.substring(0, index));

				switch (check) {
				case "%previous%":
					Main.index_Previous.add(arg + 1);
					break;

				case "%next%":
					Main.index_Next.add(arg + 1);
					break;

				default:
					break;
				}

				Main.values_footer.add(check);
				footer.delete(0, index + length);

				arg += 2;

			}

		}

		Main.values_footer.add(footer.toString());

	}

	private int getSmallest(HashMap<Integer, String> test) {

		int rs = Integer.MAX_VALUE;

		for (Integer i : test.keySet()) {
			if (i < rs) {
				rs = i;
			}
		}

		return rs;

	}

	private boolean contains(StringBuffer val, String check) {
		if (val.indexOf(check) >= 0) {
			return true;
		}

		return false;
	}

	private String getClosest_INFO(StringBuffer buffer) {
		HashMap<Integer, String> list = new HashMap<Integer, String>();

		int index = buffer.indexOf("%index%");

		if (index < 0) {
			index = Integer.MAX_VALUE;
		}

		list.put(index, "%index%");

		int id = buffer.indexOf("%plotID%");

		if (id < 0) {
			id = Integer.MAX_VALUE;
		}

		list.put(id, "%plotID%");

		int owner = buffer.indexOf("%owner%");

		if (owner < 0) {
			owner = Integer.MAX_VALUE;
		}

		list.put(owner, "%owner%");
		return list.get(getSmallest(list));

	}

	private String getClosest_Footer(StringBuffer buffer) {
		HashMap<Integer, String> list = new HashMap<Integer, String>();

		int previous = buffer.indexOf("%previous%");

		if (previous < 0) {
			previous = Integer.MAX_VALUE;
		}

		list.put(previous, "%previous%");

		int next = buffer.indexOf("%next%");

		if (next < 0) {
			next = Integer.MAX_VALUE;
		}

		list.put(next, "%next%");

		return list.get(getSmallest(list));

	}

}
