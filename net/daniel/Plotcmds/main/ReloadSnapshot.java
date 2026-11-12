package net.daniel.Plotcmds.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/** Immutable generation: parsed off-thread, installed by one volatile reference assignment. */
public final class ReloadSnapshot {
    private final Map<String, Double> worldPrices;
    private final Map<String, Double> worldExpiryDays;
    private final List<String> protectionBlocks;
    private final Map<Lang, String> messages;
    private final String languageFileContents;

    public final Template info;
    public final Template footer;

    public final boolean useConfirm_Add;
    public final boolean useConfirm_Biome;
    public final boolean useConfirm_Trust;
    public final boolean useConfirm_Clear;
    public final boolean useConfirm_delete;
    public final boolean useConfirm_removeMember;
    public final boolean useConfirm_unTrust;
    public final boolean cancelIfConfigNotSet;
    public final int confirm_sec;
    public final int expire_days_lastseen;

    private ReloadSnapshot(YamlConfiguration config, YamlConfiguration defaults, YamlConfiguration language) {
        // The temporary YAML objects never escape this constructor.
        this.worldPrices = readNumbers(config, defaults, "Price-by-World.");
        this.worldExpiryDays = readNumbers(config, defaults, "Expire-Days-by-World.");
        this.protectionBlocks = Collections.unmodifiableList(
                new ArrayList<String>(config.getStringList("Protection-Blocks")));
        EnumMap<Lang, String> resolved = new EnumMap<Lang, String>(Lang.class);
        for (Lang key : Lang.values()) {
            resolved.put(key, key.toString(language));
        }
        this.messages = Collections.unmodifiableMap(resolved);
        this.languageFileContents = language.saveToString();
        this.info = Template.parse(message(Lang.PLOT_INFO_FOR_LIST),
                "%index%", "%plotID%", "%owner%");
        this.footer = Template.parse(message(Lang.PLOT_LIST_FOOTER),
                "%previous%", "%next%");

        useConfirm_Add = config.getBoolean("UseConfirm.Add", false);
        useConfirm_Biome = config.getBoolean("UseConfirm.Biome", false);
        useConfirm_Trust = config.getBoolean("UseConfirm.Trust", false);
        useConfirm_Clear = config.getBoolean("UseConfirm.Clear", true);
        useConfirm_delete = config.getBoolean("UseConfirm.Delete", true);
        useConfirm_removeMember = config.getBoolean("UseConfirm.RemoveMember", false);
        useConfirm_unTrust = config.getBoolean("UseConfirm.UnTrust", false);
        cancelIfConfigNotSet = config.getBoolean("cancelCMDIfConfigNotSet", true);
        confirm_sec = config.getInt("confirmSec", 15);
        expire_days_lastseen = config.getInt("Expire_Days_After_OWNER_JOIN", 30);
    }

    /** Replicates FileConfiguration.getDouble(path, fallback) for all configured worlds. */
    private static Map<String, Double> readNumbers(YamlConfiguration config,
                                                   YamlConfiguration defaults, String prefix) {
        Set<String> paths = new TreeSet<String>();
        paths.addAll(defaults.getKeys(true));
        paths.addAll(config.getKeys(true));
        Map<String, Double> values = new HashMap<String, Double>();
        for (String key : paths) {
            if (!key.startsWith(prefix)) continue;
            double value = config.getDouble(key, Double.NaN);
            if (!Double.isNaN(value)) {
                values.put(key.substring(prefix.length()), value);
            }
        }
        return Collections.unmodifiableMap(values);
    }

    public double getWorldPrice(String world, String type, double fallback) {
        Double price = worldPrices.get(world + "." + type);
        return price == null ? fallback : price.doubleValue();
    }

    public double getWorldExpireDays(String world, double fallback) {
        Double days = worldExpiryDays.get(world);
        return days == null ? fallback : days.doubleValue();
    }

    public List<String> getProtectionBlocks() {
        return protectionBlocks;
    }

    public String message(Lang key) {
        return messages.get(key);
    }

    /** Legacy external callers may inspect/edit this detached copy, not the published state. */
    public YamlConfiguration copyLanguageConfiguration() {
        YamlConfiguration copy = new YamlConfiguration();
        try {
            copy.loadFromString(languageFileContents);
        } catch (InvalidConfigurationException ex) {
            throw new IllegalStateException("Published language YAML unexpectedly invalid", ex);
        }
        return copy;
    }

    /** Preserve the original reload's lang.yml default-key writeback without exposing YAML. */
    public void saveLanguageDefaults(File languageFile) throws IOException {
        File parent = languageFile.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs() && !parent.isDirectory()) {
            throw new IOException("Cannot create language directory: " + parent);
        }
        Files.write(languageFile.toPath(), languageFileContents.getBytes(StandardCharsets.UTF_8));
    }

    public static ReloadSnapshot load(File configFile, File languageFile, String embeddedConfigYaml)
            throws IOException, InvalidConfigurationException {
        // Parsing and merging take place only on local, unpublished instances.
        YamlConfiguration defaults = new YamlConfiguration();
        defaults.loadFromString(embeddedConfigYaml);
        YamlConfiguration config = new YamlConfiguration();
        if (configFile.exists()) config.load(configFile);
        // Bukkit reloadConfig() applies embedded defaults. Keep that behavior on async reloads.
        config.setDefaults(defaults);
        YamlConfiguration language = new YamlConfiguration();
        if (languageFile.exists()) language.load(languageFile);
        Lang.init(language);
        return new ReloadSnapshot(config, defaults, language);
    }

    public static final class Template {
        public final List<String> segments;
        public final Set<Integer> first;
        public final Set<Integer> second;
        public final Set<Integer> third;

        private Template(List<String> segments, Set<Integer> first,
                         Set<Integer> second, Set<Integer> third) {
            this.segments = Collections.unmodifiableList(segments);
            this.first = Collections.unmodifiableSet(first);
            this.second = Collections.unmodifiableSet(second);
            this.third = Collections.unmodifiableSet(third);
        }

        static Template parse(String value, String firstKey, String secondKey) {
            return parse(value, firstKey, secondKey, null);
        }

        static Template parse(String value, String firstKey, String secondKey, String thirdKey) {
            StringBuilder regex = new StringBuilder(Pattern.quote(firstKey));
            regex.append("|").append(Pattern.quote(secondKey));
            if (thirdKey != null) regex.append("|").append(Pattern.quote(thirdKey));
            Matcher matcher = Pattern.compile(regex.toString()).matcher(value);
            List<String> segments = new ArrayList<String>();
            Set<Integer> first = new HashSet<Integer>();
            Set<Integer> second = new HashSet<Integer>();
            Set<Integer> third = new HashSet<Integer>();
            int offset = 0;
            while (matcher.find()) {
                if (matcher.start() > offset) segments.add(value.substring(offset, matcher.start()));
                int index = segments.size();
                String key = matcher.group();
                segments.add(key);
                if (key.equals(firstKey)) first.add(index);
                else if (key.equals(secondKey)) second.add(index);
                else third.add(index);
                offset = matcher.end();
            }
            segments.add(value.substring(offset));
            return new Template(segments, first, second, third);
        }
    }
}
