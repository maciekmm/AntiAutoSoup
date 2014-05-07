/*
 * Copyright (C) 2014 by maciekmm <m.mionskowski@maciekmm.tk>
 * This file is part of AntiAutoSoup project.
 * AntiAutoSoup can not be copied and/or distributed without the express permission of maciekmm
 */

package tk.maciekmm.antiautosoup;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tk.maciekmm.antiautosoup.commands.AsBan;
import tk.maciekmm.antiautosoup.commands.AsBanInfo;
import tk.maciekmm.antiautosoup.commands.AsUnban;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class AntiAutoSoup extends JavaPlugin {
    private static ResourceBundle messages = ResourceBundle.getBundle("messages");
    private FileConfiguration bans;

    public void onEnable() {
        this.saveDefaultConfig();
        this.loadBans();
        this.getCommand("asban").setExecutor(new AsBan(this));
        this.getCommand("asunban").setExecutor(new AsUnban(this));
        this.getCommand("asbaninfo").setExecutor(new AsBanInfo(this));
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getScheduler().runTaskTimer(this,new Runnable() {
            @Override
            public void run() {
                saveBans();
            }
        },10000,10000);
    }

    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        this.saveBans();
    }

    public static String getString(String key, Object... args) {
        return MessageFormat.format(messages.getString(key), args).replace("\u00A0", " ");
    }

    private void loadBans() {
        File bansFile = new File(getDataFolder(), "bans.yml");
        if (!bansFile.exists()) {
            try {
                bansFile.createNewFile();
            } catch (IOException e) {
                this.getLogger().severe("Could not save bans file.");
            }
        }
        this.bans = YamlConfiguration.loadConfiguration(bansFile);
    }

    private void saveBans() {
        if (this.bans == null) {
            return;
        }
        try {
            this.getBans().save(new File(getDataFolder(), "bans.yml"));
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save ban config", ex);
        }
    }

    public FileConfiguration getBans() {
        if (this.bans == null) {
            loadBans();
        }
        return this.bans;
    }
}
