package com.darksoldier1404.dgb.functions;

import com.darksoldier1404.dgb.GiftBox;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SuppressWarnings("all")
public class DGBFunction {
    private static final GiftBox plugin = GiftBox.getInstance();

    public static void createGiftBox(Player p, String name) {
        if (isExistGiftBox(name)) {
            p.sendMessage(plugin.prefix + "이미 존재하는 선물박스입니다.");
            return;
        }
        plugin.config.set("GiftBoxs." + name + ".CustomModelData", 0);
        p.sendMessage(plugin.prefix + "선물박스가 생성되었습니다.");
        saveConfig();
    }

    public static void deleteGiftBox(Player p, String name) {
        if (!isExistGiftBox(name)) {
            p.sendMessage(plugin.prefix + "존재하지 않는 선물박스입니다.");
            return;
        }
        plugin.config.set("GiftBoxs." + name, null);
        p.sendMessage(plugin.prefix + "선물박스가 삭제되었습니다.");
        saveConfig();
    }

    public static void openCouponSetting(Player p, String name) {
        if (!isExistGiftBox(name)) {
            p.sendMessage(plugin.prefix + "존재하지 않는 선물박스입니다.");
            return;
        }
        DInventory inv = new DInventory(null, "선물박스 쿠폰 설정", 27, plugin);
        inv.setObj(Tuple.of(name, SettingType.COUPON));
        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, pane);
        }
        if (plugin.config.get("GiftBoxs." + name + ".Coupon") != null) {
            inv.setItem(13, plugin.config.getItemStack("GiftBoxs." + name + ".Coupon"));
        } else {
            inv.setItem(13, new ItemStack(Material.AIR));
        }
        p.openInventory(inv);
    }

    public static void openPrizeSetting(Player p, String name) {
        if (!isExistGiftBox(name)) {
            p.sendMessage(plugin.prefix + "존재하지 않는 선물박스입니다.");
            return;
        }
        DInventory inv = new DInventory(null, "선물박스 상자 설정", 54, plugin);
        inv.setObj(Tuple.of(name, SettingType.PRIZE));
        if (plugin.config.get("GiftBoxs." + name + ".Prize") != null) {
            for (String key : plugin.config.getConfigurationSection("GiftBoxs." + name + ".Prize").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), plugin.config.getItemStack("GiftBoxs." + name + ".Prize." + key));
            }
        }
        p.openInventory(inv);
    }

    public static void setCustomModelData(Player p, String name, String sCustomModelData) {
        if (!isExistGiftBox(name)) {
            p.sendMessage(plugin.prefix + "존재하지 않는 선물박스입니다.");
            return;
        }
        try {
            plugin.config.set("GiftBoxs." + name + ".CustomModelData", Integer.parseInt(sCustomModelData));
            p.sendMessage(plugin.prefix + "선물박스의 커스텀 모델 데이터가 설정되었습니다.");
        } catch (NumberFormatException e) {
            p.sendMessage(plugin.prefix + "잘못된 값입니다.");
            return;
        }
        saveConfig();
    }

    public static void saveCouponSetting(Player p, DInventory inv) {
        String name = ((Tuple<String, SettingType>) inv.getObj()).getA();
        plugin.config.set("GiftBoxs." + name + ".Coupon", inv.getItem(13));
        p.sendMessage(plugin.prefix + "쿠폰이 저장되었습니다.");
        saveConfig();
    }

    public static void savePrizeSetting(Player p, DInventory inv) {
        String name = ((Tuple<String, SettingType>) inv.getObj()).getA();
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null) {
                plugin.config.set("GiftBoxs." + name + ".Prize." + i, inv.getItem(i));
            }
        }
        p.sendMessage(plugin.prefix + "보상이 저장되었습니다.");
        saveConfig();
    }

    public static void getCoupon(Player p, String name) {
        if (!isExistGiftBox(name)) {
            p.sendMessage(plugin.prefix + "존재하지 않는 선물박스입니다.");
            return;
        }
        if (plugin.config.get("GiftBoxs." + name + ".Coupon") == null) {
            p.sendMessage(plugin.prefix + "쿠폰이 없습니다.");
            return;
        }
        ItemStack coupon = plugin.config.getItemStack("GiftBoxs." + name + ".Coupon");
        p.getInventory().addItem(NBT.setStringTag(coupon, "DGB", name));
        p.sendMessage(plugin.prefix + "쿠폰을 발급하였습니다.");
    }

    public static void getCoupon(Player p, String name, Player target) {
        if (!isExistGiftBox(name)) {
            p.sendMessage(plugin.prefix + "존재하지 않는 선물박스입니다.");
            return;
        }
        if (plugin.config.get("GiftBoxs." + name + ".Coupon") == null) {
            p.sendMessage(plugin.prefix + "쿠폰이 없습니다.");
            return;
        }
        ItemStack coupon = plugin.config.getItemStack("GiftBoxs." + name + ".Coupon");
        if (target.getInventory().firstEmpty() == -1) {
            p.sendMessage(plugin.prefix + "대상 인벤토리가 가득 찼습니다.");
            return;
        }
        target.getInventory().addItem(NBT.setStringTag(coupon, "DGB", name));
        p.sendMessage(plugin.prefix + "쿠폰을 지급하였습니다.");
    }

    public static void givePrize(Player p, String name, ItemStack cp) {
        if (!isExistGiftBox(name)) {
            p.sendMessage(plugin.prefix + "존재하지 않는 선물박스입니다.");
            return;
        }
        if (plugin.config.get("GiftBoxs." + name + ".Prize") == null) {
            p.sendMessage(plugin.prefix + "보상이 없습니다.");
            return;
        }
        List<ItemStack> items = new ArrayList<>();
        for (String key : plugin.config.getConfigurationSection("GiftBoxs." + name + ".Prize").getKeys(false)) {
            items.add(plugin.config.getItemStack("GiftBoxs." + name + ".Prize." + key));
        }
        if (items.size() == 0) {
            p.sendMessage(plugin.prefix + "보상이 없습니다.");
            cp.setAmount(cp.getAmount() - 1);
            return;
        }
        ItemStack[] playerItems = p.getInventory().getStorageContents();
        Inventory inv = Bukkit.createInventory(null, 36, "보상");
        inv.setContents(playerItems);
        Map<Integer, ItemStack> leftOver = new HashMap<>();
        for (ItemStack i : items) {
            leftOver.putAll(inv.addItem(i));
        }
        if(!leftOver.isEmpty()) {
            p.sendMessage(plugin.prefix + "인벤토리 공간이 부족합니다.");
            return;
        }else{
            for(ItemStack i : items) {
                p.getInventory().addItem(i);
            }
            p.sendMessage(plugin.prefix + "보상을 수령하였습니다.");
            cp.setAmount(cp.getAmount() - 1);
        }
    }

        public static void listGiftBoxs(Player p) {
        if (plugin.config.getConfigurationSection("GiftBoxs") == null) {
            p.sendMessage(plugin.prefix + "선물박스가 없습니다.");
            return;
        }
        p.sendMessage(plugin.prefix + "<<< 선물박스 목록 >>>");
        for (String key : plugin.config.getConfigurationSection("GiftBoxs").getKeys(false)) {
            p.sendMessage(plugin.prefix + key);
        }
    }

    public static boolean isExistGiftBox(String name) {
        return plugin.config.get("GiftBoxs." + name) != null;
    }

    public static void saveConfig() {
        ConfigUtils.savePluginConfig(plugin, plugin.config);
    }

    public static void reloadConfig() {
        plugin.config = ConfigUtils.reloadPluginConfig(plugin, plugin.config);
        plugin.prefix = ColorUtils.applyColor(plugin.config.getString("Settings.prefix"));
    }
}
