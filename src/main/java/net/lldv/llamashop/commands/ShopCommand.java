package net.lldv.llamashop.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import net.lldv.llamashop.LlamaShop;

public class ShopCommand extends PluginCommand<LlamaShop> {

    public ShopCommand(LlamaShop owner) {
        super(owner.getConfig().getString("Commands.Shop.Name"), owner);
        this.setDescription(owner.getConfig().getString("Commands.Shop.Description"));
        this.setAliases(owner.getConfig().getStringList("Commands.Shop.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            this.getPlugin().getFormWindows().openShop(player);
        }
        return true;
    }
}
