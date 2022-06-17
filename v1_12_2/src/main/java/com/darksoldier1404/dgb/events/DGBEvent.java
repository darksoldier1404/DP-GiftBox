package com.darksoldier1404.dgb.events;

import com.darksoldier1404.dgb.GiftBox;
import com.darksoldier1404.dgb.functions.DGBFunction;
import com.darksoldier1404.dgb.functions.SettingType;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class DGBEvent implements Listener {
    private final GiftBox plugin = GiftBox.getInstance();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (DGBFunction.currentInv.containsKey(e.getPlayer().getUniqueId())) {
            DInventory inv = DGBFunction.currentInv.get(e.getPlayer().getUniqueId());
            if (inv.isValidHandler(plugin)) {
                Tuple<String, SettingType> tpl = (Tuple<String, SettingType>) inv.getObj();
                if (tpl.getB() == SettingType.COUPON) {
                    DGBFunction.saveCouponSetting(p, inv);
                }
                if (tpl.getB() == SettingType.PRIZE) {
                    DGBFunction.savePrizeSetting(p, inv);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (DGBFunction.currentInv.containsKey(e.getWhoClicked().getUniqueId())) {
            DInventory inv = DGBFunction.currentInv.get(e.getWhoClicked().getUniqueId());
            if (inv.isValidHandler(GiftBox.getInstance())) {
                Tuple<String, SettingType> tpl = (Tuple<String, SettingType>) inv.getObj();
                if (tpl.getB() == SettingType.COUPON) {
                    if (e.getRawSlot() < 54 && e.getSlot() != 13) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (e.getItem() != null) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (NBT.hasTagKey(e.getItem(), "DGB")) {
                    String name = NBT.getStringTag(e.getItem(), "DGB");
                    DGBFunction.givePrize(e.getPlayer(), name, e.getItem());
                    e.setCancelled(true);
                }
            }
        }
    }
}
