package com.darksoldier1404.dgb.commands;

import com.darksoldier1404.dgb.GiftBox;
import com.darksoldier1404.dgb.functions.DGBFunction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class DGBCommand implements CommandExecutor, TabCompleter {
    private final GiftBox plugin = GiftBox.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("권한이 없습니다.");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용 가능합니다.");
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(plugin.prefix + "/dgb 생성 <이름> - 선물 박스를 생성합니다.");
            p.sendMessage(plugin.prefix + "/dgb 보상 <이름> - 선물 박스 보상을 설정합니다.");
            p.sendMessage(plugin.prefix + "/dgb cmd <이름> <CustomModalData> - 선물 박스 쿠폰 아이템의 커스텀 모델 데이터를 설정합니다.");
            p.sendMessage(plugin.prefix + "0으로 설정하면 삭제됩니다.");
            p.sendMessage(plugin.prefix + "/dgb 쿠폰 <이름> - 선물 박스 쿠폰 아이템을 설정합니다.");
            p.sendMessage(plugin.prefix + "/dgb 쿠폰발급 <이름> (닉네임) - 자신 또는 대상에게 선물 박스 쿠폰 아이템을 발급합니다.");
            p.sendMessage(plugin.prefix + "/dgb 삭제 <이름> - 선물 박스를 삭제합니다.");
            p.sendMessage(plugin.prefix + "/dgb 목록 - 선물 박스 목록을 표시합니다.");
            p.sendMessage(plugin.prefix + "/dgb reload - config 설정 파일을 다시 불러옵니다.");
            return false;
        }
        if (args[0].equals("생성")) {
            if (args.length == 1) {
                p.sendMessage(plugin.prefix + "생성할 선물 박스의 이름을 입력해주세요.");
                return false;
            }
            DGBFunction.createGiftBox(p, args[1]);
            return false;
        }
        if (args[0].equals("보상")) {
            if (args.length == 1) {
                p.sendMessage(plugin.prefix + "보상을 설정할 선물 박스의 이름을 입력해주세요.");
                return false;
            }
            DGBFunction.openPrizeSetting(p, args[1]);
            return false;
        }
        if (args[0].equals("cmd")) {
            if (args.length == 1) {
                p.sendMessage(plugin.prefix + "커스텀 모델 데이터를 설정할 선물 박스의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(plugin.prefix + "커스텀 모델 데이터를 입력해주세요.");
                return false;
            }
            DGBFunction.setCustomModelData(p, args[1], args[2]);
            return false;
        }
        if (args[0].equals("쿠폰")) {
            if (args.length == 1) {
                p.sendMessage(plugin.prefix + "쿠폰을 설정할 선물 박스의 이름을 입력해주세요.");
                return false;
            }
            DGBFunction.openCouponSetting(p, args[1]);
            return false;
        }
        if (args[0].equals("쿠폰발급")) {
            if (args.length == 1) {
                p.sendMessage(plugin.prefix + "발급할 선물 박스의 이름을 입력해주세요.");
                return false;
            }
            if (args.length == 2) {
                DGBFunction.getCoupon(p, args[1]);
                return false;
            }
            if (args.length == 3) {
                try {
                    Player target = Bukkit.getPlayer(args[2]);
                    DGBFunction.getCoupon(p, args[1], target);
                    return false;
                } catch (Exception e) {
                    p.sendMessage(plugin.prefix + "잘못된 대상입니다.");
                    return false;
                }
            }
            return false;
        }
        if (args[0].equals("삭제")) {
            if (args.length == 1) {
                p.sendMessage(plugin.prefix + "삭제할 선물 박스의 이름을 입력해주세요.");
                return false;
            }
            DGBFunction.deleteGiftBox(p, args[1]);
            return false;
        }
        if (args[0].equals("reload")) {
            DGBFunction.reloadConfig();
            p.sendMessage(plugin.prefix + "콘피그 설정 파일이 리로드 되었습니다.");
            return false;
        }
        if(args[0].equals("목록")) {
            DGBFunction.listGiftBoxs(p);
            return false;
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                return Arrays.asList("생성", "보상", "쿠폰", "쿠폰발급", "cmd", "삭제", "reload", "목록");
            }
            if (args.length == 2) {
                if (plugin.config.get("GiftBoxs") != null) {
                    return plugin.config.getConfigurationSection("GiftBoxs").getKeys(false).stream().collect(Collectors.toList());
                }
            }
        }
        return null;
    }
}
