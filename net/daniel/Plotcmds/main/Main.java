
package net.daniel.Plotcmds.main;

import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {

	public final Logger logger;
	public static ExpireManager expireManager;
	public static Economy Eco;
	public static Essentials essentials;

	public static PlotAPI plotAPI;

	public static Biome biome;

	private static String pluginName;
	private static final Map<String, PlayerConfirmHolder> data =
			new ConcurrentHashMap<>();
	public static Main plugin;

	// One published generation for config.yml, lang.yml and list templates.
	private volatile ReloadSnapshot currentConfiguration;
	private String embeddedConfigYaml;
	private final AtomicInteger reloadGate = new AtomicInteger(0);
	private static final int RELOADING = Integer.MIN_VALUE;
	public static Set<UUID> spyPlayers;

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
		try {
			this.reloadConfiguration();
		} catch (Exception ex) {
			getLogger().log(Level.SEVERE, "Unable to load configuration", ex);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		final PluginDescriptionFile pdFile = this.getDescription();
		Main.pluginName = pdFile.getName();
		if (!this.SetupEconomy()) {
			Bukkit.getConsoleSender().sendMessage("§6§l[ Mine Plot CMD ] §c§lEconomy §f플러그인이 인식되지 않았으므로, 비활성화 됩니다.");
			this.getServer().getPluginManager().disablePlugin((Plugin) this);
			return;
		}

		spyPlayers = ConcurrentHashMap.newKeySet();

		this.getCommand("땅초기화").setExecutor(reloadSensitive(new ClearPlotCommand()));
		this.getCommand("땅삭제").setExecutor(reloadSensitive(new deletePlotCommand()));
		this.getCommand("땅바이옴").setExecutor(reloadSensitive(new BiomePlotCommand()));

		this.getCommand("땅멤버").setExecutor(reloadSensitive(new TrustCommand()));
		this.getCommand("땅약식멤버").setExecutor(reloadSensitive(new AddCommand()));
		this.getCommand("땅계정이전").setExecutor(new MigrateCommand());
		this.getCommand("땅보존기간").setExecutor(reloadSensitive(new keepPlotCommand()));
		this.getCommand("땅정보").setExecutor(reloadSensitive(new InfoCommand()));
		this.getCommand("땅목록").setExecutor(reloadSensitive(new ListCommand()));


		this.getCommand("땅해제").setExecutor(reloadSensitive(new RemoveCommand()));

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

		getLogger().info(String.valueOf(pdFile.getFullName() + "이(가) 활성화 되었습니다."));

	}


	public static Main get() {
		return (Main) Bukkit.getPluginManager().getPlugin(pluginName);
	}

	public File createLangFile() {
		return new File(getDataFolder(), "lang.yml");
	}

	/** Retained for API compatibility; returns a detached copy, never live snapshot state. */
	public FileConfiguration getLangConfig() {
		return currentConfiguration.copyLanguageConfiguration();
	}

	public static ReloadSnapshot snapshot() {
		return plugin.currentConfiguration;
	}

	private CommandExecutor reloadSensitive(CommandExecutor executor) {
		return (sender, command, label, args) -> {
			if (!tryEnterCommand()) {
				sender.sendMessage(Lang.RELOAD_BUSY.toString());
				return true;
			}
			try {
				return executor.onCommand(sender, command, label, args);
			} finally {
				leaveCommand();
			}
		};
	}

	private boolean tryEnterCommand() {
		while (true) {
			int state = reloadGate.get();
			if (state < 0 || state == Integer.MAX_VALUE) {
				return false;
			}
			if (reloadGate.compareAndSet(state, state + 1)) {
				return true;
			}
		}
	}

	private void leaveCommand() {
		reloadGate.decrementAndGet();
	}

	/**
	 * Retain an already admitted command while a PlotSquared callback is outstanding.
	 * Unlike tryEnterCommand(), this must work AFTER the reload flag was set:
	 * an in-flight command is allowed to finish its existing callback chain.
	 * Must be called before the original guarded runnable returns.
	 */
	public static CallbackLease retainCallback() {
		Main owner = plugin;
		while (true) {
			int state = owner.reloadGate.get();
			int active = state & Integer.MAX_VALUE;
			if (active == 0 || active == Integer.MAX_VALUE) {
				throw new IllegalStateException("No active protected command to retain");
			}
			if (owner.reloadGate.compareAndSet(state, state + 1)) {
				return new CallbackLease(owner);
			}
		}
	}

	public static final class CallbackLease {
		private final Main owner;
		private final AtomicBoolean completed = new AtomicBoolean(false);

		private CallbackLease(Main owner) { this.owner = owner; }

		/** Idempotent: covers failed submission, synchronous throw and callback completion. */
		public void complete() {
			if (completed.compareAndSet(false, true)) owner.leaveCommand();
		}
	}

	/** Counts the scheduled async body, not just the command-dispatch callback. */
	public static void runGuardedAsync(CommandSender sender, Runnable work) {
		Main owner = plugin;
		if (!owner.tryEnterCommand()) {
			sender.sendMessage(Lang.RELOAD_BUSY.toString());
			return;
		}
		try {
			Bukkit.getScheduler().runTaskAsynchronously(owner, () -> {
				try {
					work.run();
				} finally {
					owner.leaveCommand();
				}
			});
		} catch (RuntimeException ex) {
			owner.leaveCommand();
			throw ex;
		}
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

	/** Resource acquisition is on the server thread, never inside the YAML worker. */
	private String readEmbeddedDefaults() throws IOException {
		try (InputStream source = getResource("config.yml")) {
			if (source == null) throw new IOException("Missing embedded config.yml resource");
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int count;
			while ((count = source.read(buffer)) != -1) bytes.write(buffer, 0, count);
			return new String(bytes.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
		}
	}

	/** Synchronous boot load only (Bukkit main thread). */
	private void reloadConfiguration() throws Exception {
		saveDefaultConfig();
		embeddedConfigYaml = readEmbeddedDefaults();
		ReloadSnapshot loaded = ReloadSnapshot.load(createConfigFile(), createLangFile(), embeddedConfigYaml);
		currentConfiguration = loaded;
		loaded.saveLanguageDefaults(createLangFile());
	}

	private boolean beginReload() {
		while (true) {
			int state = reloadGate.get();
			if (state < 0) return false;
			if (reloadGate.compareAndSet(state, state | RELOADING)) return true;
		}
	}

	private void unlockReload() {
		reloadGate.getAndUpdate(state -> state & Integer.MAX_VALUE);
	}

	private void asyncReload(CommandSender sender) {
		if (!beginReload()) {
			sender.sendMessage(Lang.RELOAD_ALREADY_RUNNING.toString());
			return;
		}
		// The original reload recreated a deleted config.yml from the jar.
		// This Bukkit method is called only on the main thread.
		try {
			saveDefaultConfig();
		} catch (RuntimeException ex) {
			reloadFailed(sender, ex);
			return;
		}
		final File configFile = createConfigFile();
		final File langFile = createLangFile();
		final String defaults = embeddedConfigYaml;
		try {
			Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
				try {
					// No Bukkit getConfig()/reloadConfig() or other server API on the worker.
					ReloadSnapshot fresh = ReloadSnapshot.load(configFile, langFile, defaults);
					// Original behavior: update lang.yml with any missing default messages.
					// Disk writes stay off the Bukkit primary thread.
					try {
						fresh.saveLanguageDefaults(langFile);
					} catch (IOException ex) {
						getLogger().log(Level.WARNING, "Could not persist lang.yml defaults", ex);
					}
					Bukkit.getScheduler().runTask(this, () ->
							commitReloadWhenIdle(sender, fresh, System.nanoTime() + TimeUnit.SECONDS.toNanos(15)));
				} catch (Exception ex) {
					Bukkit.getScheduler().runTask(this, () -> reloadFailed(sender, ex));
				}
			});
		} catch (RuntimeException ex) {
			unlockReload();
			throw ex;
		}
		sender.sendMessage(Lang.RELOAD_STARTED.toString());
	}

	/** Always called on the Bukkit primary thread. Old tasks must drain before swap. */
	private void commitReloadWhenIdle(CommandSender sender, ReloadSnapshot fresh, long deadline) {
		if (reloadGate.get() != RELOADING) {
			if (System.nanoTime() >= deadline) {
				reloadFailed(sender, new IllegalStateException("Active commands did not finish within 15 seconds"));
				return;
			}
			Bukkit.getScheduler().runTaskLater(this, () -> commitReloadWhenIdle(sender, fresh, deadline), 1L);
			return;
		}
		try {
			// Single volatile publication of config, language and both templates.
			currentConfiguration = fresh;
			sender.sendMessage(Lang.RELOAD_COMPLETE.toString());
		} finally {
			unlockReload();
		}
	}

	private void reloadFailed(CommandSender sender, Exception ex) {
		try {
			getLogger().log(Level.WARNING, "Configuration reload failed; previous snapshot retained", ex);
			sender.sendMessage(Lang.RELOAD_FAILED.toString());
		} finally {
			unlockReload();
		}
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

		if (!commandLabel.equalsIgnoreCase("Mineplotcmd")) {
			return false;
		}

		if (!sender.hasPermission("MinePlotCMD.reload")) {
			sender.sendMessage("§b§l[ §f§lMine SV §b§l] §c권한이 없습니다. 필요한 권한: MinePlotCMD.reload");
			return true;
		}

		if (args.length == 0) {

			sender.sendMessage("§b§l[ §f§lMine Plot CMD §b§l] §e/Mineplotcmd reload §f: 플러그인 설정 리로드");
			return true;

		}

		if (args[0].equalsIgnoreCase("reload")) {
			asyncReload(sender);
			return true;
		}

		sender.sendMessage("§b§l[ §f§lMine Plot CMD §b§l] §e/Minecmd reload §f: 플러그인 설정 리로드");
		return true;
	}

	public static Map<String, PlayerConfirmHolder> getData() {
		return data;
	}

}
