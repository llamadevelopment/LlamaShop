package net.lldv.llamashop;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import net.lldv.llamashop.commands.ShopCommand;
import net.lldv.llamashop.components.forms.FormListener;
import net.lldv.llamashop.components.forms.FormWindows;
import net.lldv.llamashop.components.language.Language;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class LlamaShop extends PluginBase {

    public LinkedHashMap<String, Set<String>> cachedCategory = new LinkedHashMap<>();
    public LinkedHashMap<String, String[]> cachedShopItem = new LinkedHashMap<>();

    @Getter
    private static LlamaShop instance;

    @Getter
    private FormWindows formWindows;

    @Override
    public void onEnable() {
        instance = this;
        try {
            this.saveDefaultConfig();
            Language.init();
            this.loadPlugin();
            this.formWindows = new FormWindows(this);
            this.getLogger().info("§aLlamaShop successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().error("§4Failed to load LlamaShop.");
        }
    }

    private void loadPlugin() {
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
        this.getServer().getCommandMap().register("llamashop", new ShopCommand(this));

        this.getConfig().getSection("ShopForm").getAll().getKeys(false).forEach(s -> {
            Set<String> list = new HashSet<>();
            this.cachedCategory.put(s, list);
            this.getConfig().getStringList("ShopForm." + s).forEach(e -> {
                String[] f = e.split(":");
                this.cachedShopItem.put(f[0], f);
                list.add(e);
            });
        });
    }

}