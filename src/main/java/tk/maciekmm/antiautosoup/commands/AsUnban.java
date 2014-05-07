/*
 * Copyright (C) 2014 by maciekmm <m.mionskowski@maciekmm.tk>
 * This file is part of AntiAutoSoup project.
 * AntiAutoSoup can not be copied and/or distributed without the express permission of maciekmm
 */

package tk.maciekmm.antiautosoup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tk.maciekmm.antiautosoup.AntiAutoSoup;
import tk.maciekmm.antiautosoup.BanInfo;

public class AsUnban implements CommandExecutor {
    private final AntiAutoSoup plugin;

    public AsUnban(AntiAutoSoup plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        BanInfo ban = BanInfo.getByName(args[0], plugin.getBans());
        if(!ban.isBanned()) {
            sender.sendMessage(AntiAutoSoup.getString("ban.not_banned", args[0]));
            return true;
        }
        ban.unban();
        sender.sendMessage(AntiAutoSoup.getString("ban.unban", args[0]));
        return true;
    }
}
