package com.github.nyaon08.rtustudio.staffcall.command;

import com.github.nyaon08.rtustudio.staffcall.StaffCall;
import com.github.nyaon08.rtustudio.staffcall.data.Help;
import com.github.nyaon08.rtustudio.staffcall.inventory.StaffInventory;
import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import kr.rtuserver.framework.bukkit.api.format.ComponentFormatter;
import kr.rtuserver.framework.bukkit.api.player.RSPlayer;
import kr.rtuserver.protoweaver.api.proxy.ProxyLocation;
import kr.rtuserver.protoweaver.api.proxy.ProxyPlayer;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MainCommand extends RSCommand<StaffCall> {

    public MainCommand(StaffCall plugin) {
        super(plugin, "staffcall");
    }

    @Override
    protected boolean execute(RSCommandData data) {
        UUID uuid = player().getUniqueId();

        if (getPlugin().getStaffManager().contains(uuid)) {
            chat().announce(player(), ComponentFormatter.mini(message().get(player(), "alreadyRequest")));
            return true;
        }

        String reason = !data.isEmpty() ? data.args(0) : "없음";

        for (Player player : getPlugin().getServer().getOnlinePlayers()) {
            if (player.hasPermission(getPlugin().getName() + ".help.receive")) {
                String helpMessage = message().get(player, "helpRequest")
                        .replace("%player%", player().getName());

                TextComponent messageComponent = Component.text(helpMessage)
                        .clickEvent(ClickEvent.callback(audience -> {
                            if (!getPlugin().getStaffManager().contains(uuid)) {
                                return;
                            }

                            player().openInventory(new StaffInventory(getPlugin(), player()).getInventory());

                        }))
                        .hoverEvent(HoverEvent.showText(Component.text("사유 : " + reason + "\n클릭하여 이동")));

                chat().announce(player, ComponentFormatter.mini(messageComponent));
            }
        }

        chat().announce(player(), ComponentFormatter.mini(message().get(player(), "request")));
        getPlugin().getStaffManager().add(uuid, new Help(uuid, reason, new Date()));
        return true;
    }

    @Override
    protected List<String> tabComplete(RSCommandData data) {
        if (data.args(0).isEmpty()) return List.of(message().get(player(), "reason"));
        return List.of();
    }

    @Override
    protected void reload(RSCommandData data) {
    }

}
