/*
 * Copyright (C) 2014 by maciekmm <m.mionskowski@maciekmm.tk>
 * This file is part of AntiAutoSoup project.
 * AntiAutoSoup can not be copied and/or distributed without the express permission of maciekmm
 */

package tk.maciekmm.antiautosoup;

import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class BanInfo {
    private long banned;
    private int banTime;
    private int bans;
    private String nick;
    private final ConfigurationSection section;

    public static BanInfo fromConfig(ConfigurationSection section,String name) {
        if(section!=null) {
            long banned = section.getLong("banned");
            int banTime = section.getInt("banTime");
            int bans = section.getInt("bans");
            return new BanInfo(section,banned,banTime,bans,name);
        }
        return new BanInfo(null,0,0,0,name);
    }

    public static BanInfo fromUUID(UUID uuid, String name, AntiAutoSoup plugin) {
        ConfigurationSection section =plugin.getBans().getConfigurationSection(uuid.toString());
        if(section==null) {
            section = plugin.getBans().createSection(uuid.toString());
        }
        return fromConfig(section,name);
    }

    public static BanInfo getByName(String name,ConfigurationSection section) {
        for(String ban : section.getKeys(false)) {
            ConfigurationSection banSection = section.getConfigurationSection(ban);
            if(banSection.getString("nick").equalsIgnoreCase(name)) {
                return fromConfig(banSection,name);
            }
        }
        return fromConfig(null,name);
    }

    public BanInfo(ConfigurationSection section, long banned, int banTime, int bans, String nick) {
        this.section = section;
        this.banned = banned;
        this.banTime = banTime;
        this.nick = nick;
        this.bans = bans;
    }

    public void save() {
        this.section.set("banned",banned);
        this.section.set("banTime",this.banTime);
        this.section.set("bans",this.bans);
        this.section.set("nick",this.nick);
    }

    public boolean isBanned() {
        return this.getBanTime()>0;
        //return this.banned+1000*60*banTime-System.currentTimeMillis()>0;
    }

    public void unban() {
        banned = 0;
        this.bans--;
        this.save();
    }

    public int getBanNumber() {
        return this.bans;
    }

    public void ban(AntiAutoSoup plugin) {
        this.banned = System.currentTimeMillis();
        this.bans++;
        if(this.bans>=plugin.getConfig().getInt("bans.banAttempts")) {
            this.banTime = Integer.MAX_VALUE;
        } else {
            this.banTime = plugin.getConfig().getInt("bans.banTime");
        }
        this.save();
    }

    public String getFriendlyBanTime() {
        return this.banTime==Integer.MAX_VALUE ? "permanent" : String.valueOf(getBanTime());
    }

    public int getBanTime() {
        return (int) (this.banTime-(System.currentTimeMillis()-this.banned)/1000/60);
    }

}
