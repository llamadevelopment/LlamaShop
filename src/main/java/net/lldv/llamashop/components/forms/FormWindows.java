package net.lldv.llamashop.components.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.PlaySoundPacket;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamashop.LlamaShop;
import net.lldv.llamashop.components.forms.custom.CustomForm;
import net.lldv.llamashop.components.forms.simple.SimpleForm;
import net.lldv.llamashop.components.language.Language;

import java.util.concurrent.CompletableFuture;

public class FormWindows {

    private final LlamaShop instance;

    public FormWindows(LlamaShop instance) {
        this.instance = instance;
    }

    public void openShop(Player player) {
        SimpleForm.Builder form = new SimpleForm.Builder(Language.getNP("shop-title"), Language.getNP("shop-content"));
        this.instance.cachedCategory.keySet().forEach(category -> form.addButton(new ElementButton(category), e -> this.openCategory(player, category)));
        SimpleForm finalForm = form.build();
        finalForm.send(player);
    }

    public void openCategory(Player player, String category) {
        SimpleForm.Builder form = new SimpleForm.Builder(category, "");
        this.instance.cachedCategory.get(category).forEach(item -> {
            String[] s = item.split(":");
            form.addButton(new ElementButton(Language.getNP("category-item", s[0], s[3])), e -> this.openItemShop(player, s[0]));
        });
        SimpleForm finalForm = form.build();
        finalForm.send(player);
    }

    public void openItemShop(Player player, String e) {
        String[] s = this.instance.cachedShopItem.get(e);
        String name = s[0];
        int itemID = Integer.parseInt(s[1]);
        int itemMeta = Integer.parseInt(s[2]);
        double price = Double.parseDouble(s[3]);
        CustomForm form = new CustomForm.Builder(e)
                .addElement(new ElementInput(Language.getNP("itembuy-info", name, price), Language.getNP("itembuy-amount"), "1"))
                .onSubmit((f, r) -> {
                    try {
                        int amount = Integer.parseInt(r.getInputResponse(0));
                        if (amount <= 0) {
                            player.sendMessage(Language.get("invalid-amount"));
                            this.playSound(player, Sound.NOTE_BASS);
                            return;
                        }
                        this.sellItem(player, name, itemID, itemMeta, amount, price);
                    } catch (NumberFormatException g) {
                        player.sendMessage(Language.get("invalid-amount"));
                        this.playSound(player, Sound.NOTE_BASS);
                    }
                })
                .build();
        form.send(player);
    }

    public void sellItem(Player player, String name, int item, int meta, int amount, double price) {
        CompletableFuture.runAsync(() -> {
            double newPrice = price * amount;
            if (LlamaEconomy.getAPI().getMoney(player.getName()) >= newPrice) {
                Item finalItem = Item.get(item, meta, amount);
                if (player.getInventory().canAddItem(finalItem)) {
                    player.getInventory().addItem(finalItem);
                    LlamaEconomy.getAPI().reduceMoney(player.getName(), newPrice);
                    player.sendMessage(Language.get("item-bought", name, amount, LlamaEconomy.getAPI().getMoneyFormat().format(newPrice)));
                    this.playSound(player, Sound.NOTE_HARP);
                } else {
                    player.sendMessage(Language.get("inventory-full"));
                    this.playSound(player, Sound.NOTE_BASS);
                }
            } else {
                player.sendMessage(Language.get("no-money"));
                this.playSound(player, Sound.NOTE_BASS);
            }
        });
    }

    private void playSound(Player player, Sound sound) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.x = new Double(player.getLocation().getX()).intValue();
        packet.y = (new Double(player.getLocation().getY())).intValue();
        packet.z = (new Double(player.getLocation().getZ())).intValue();
        packet.volume = 1.0F;
        packet.pitch = 1.0F;
        player.dataPacket(packet);
    }

}
