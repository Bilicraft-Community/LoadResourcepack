package com.bilicraft.loadresourcepack;

import com.google.common.io.Files;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class LoadResourcepack extends JavaPlugin implements Listener {

    private String pack;
    private String sha1;
    private Component prompt;
    private boolean require;

    @Override
    public void onEnable() {
        saveResource("config.properties", false);

        var config = new Properties();
        try {
            config.load(Files.newReader(new File(getDataFolder(), "config.properties"), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }


        pack = config.getProperty("resource-pack", "");
        sha1 = config.getProperty("resource-pack-sha1", "");

        if (pack.isBlank() || sha1.isBlank()) Bukkit.getPluginManager().disablePlugin(this);

        require = Boolean.parseBoolean(config.getProperty("require-resource-pack", "false"));

        var promptRaw = config.getProperty("resource-pack-prompt", null);
        if (promptRaw.isBlank()) promptRaw = null;

        prompt = GsonComponentSerializer.gson().deserializeOrNull(promptRaw);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasResourcePack())
            e.getPlayer().setResourcePack(pack, sha1, require, prompt);
    }
}
