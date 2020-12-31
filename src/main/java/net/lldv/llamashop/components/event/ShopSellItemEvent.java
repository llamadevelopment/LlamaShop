package net.lldv.llamashop.components.event;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ShopSellItemEvent extends Event {

    private final Player player;
    private final String name;
    private final int item;
    private final int meta;
    private final int amount;
    private final double price;
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

}
