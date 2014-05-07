/*
 * Copyright (C) 2014 by maciekmm <m.mionskowski@maciekmm.tk>
 * This file is part of AntiAutoSoup project.
 * AntiAutoSoup can not be copied and/or distributed without the express permission of maciekmm
 */

package tk.maciekmm.antiautosoup;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;

import java.util.HashMap;

public class PlayerListener implements Listener {
    private final HashMap<Player,PlayerCheck> check = new HashMap<>();
    private final AntiAutoSoup plugin;

    public PlayerListener(AntiAutoSoup plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getCurrentItem()!=null&&event.getCurrentItem().getType()==Material.MUSHROOM_SOUP) {
            check.get(event.getWhoClicked()).setLastClick();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        check.get(event.getPlayer()).setLastClose();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getPlayer().hasPermission("antiautosoup.bypass")) {
            return;
        }
        if(event.getItem()!=null&&event.getItem().getType()== Material.MUSHROOM_SOUP) {
            event.getItem().setType(Material.BOWL);
            PlayerCheck pl = check.get(event.getPlayer());
            pl.setLastInteract();
            if(!pl.isLegit(PlayerCheck.CheckMoment.INTERACT)) {
                this.notLegit(event.getPlayer(), pl);
            }
        }
    }

    @EventHandler
    public void onHold(PlayerItemHeldEvent event) {
        if(event.getPlayer().hasPermission("antiautosoup.bypass")) {
            return;
        }
        if(event.getPlayer().getItemInHand()!=null&&event.getPlayer().getItemInHand().getType()== Material.MUSHROOM_SOUP) {
            PlayerCheck pl = check.get(event.getPlayer());
            pl.setHeld();
            if(!pl.isLegit(PlayerCheck.CheckMoment.HELD)) {
                this.notLegit(event.getPlayer(),pl);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.check.put(event.getPlayer(),new PlayerCheck());
    }

    @EventHandler
    public void onPreLogin(PlayerLoginEvent event) {
        BanInfo info = BanInfo.fromUUID(event.getPlayer().getUniqueId(), event.getPlayer().getName(), plugin);
        if(info.isBanned()) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED,AntiAutoSoup.getString("hacking.ban", info.getFriendlyBanTime()));
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        this.check.remove(event.getPlayer());
    }

    public void notLegit(Player player,PlayerCheck check) {
        if(!this.isModOnline()) {
            BanInfo info = BanInfo.fromUUID(player.getUniqueId(), player.getName(), plugin);
            info.ban(plugin);
            player.kickPlayer(AntiAutoSoup.getString("hacking.ban", info.getFriendlyBanTime()));
        } else if(check.getLastInform()==Long.MIN_VALUE||System.currentTimeMillis()-check.getLastInform()>2000) {
            check.setInform();
            for(Player online : Bukkit.getOnlinePlayers()) {
                if(online.hasPermission("antiautosoup.moderator")) {
                    online.sendMessage(AntiAutoSoup.getString("hacking.broadcast",player.getName()));
                }
            }
        }
    }

    public boolean isModOnline() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("antiautosoup.moderator")) {
                return true;
            }
        }
        return false;
    }
}
