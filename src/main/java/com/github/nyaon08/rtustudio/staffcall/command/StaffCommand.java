package com.github.nyaon08.rtustudio.staffcall.command;

import com.github.nyaon08.rtustudio.staffcall.StaffCall;
import com.github.nyaon08.rtustudio.staffcall.inventory.StaffInventory;
import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class StaffCommand extends RSCommand<StaffCall>{

    public StaffCommand(StaffCall plugin) {
        super(plugin, "callInfo", PermissionDefault.getByName(plugin.getName() + ".help.receive"));
    }

    @Override
    protected boolean execute(RSCommandData data) {
        player().openInventory(new StaffInventory(getPlugin(), player()).getInventory());
        return true;
    }

    @Override
    protected List<String> tabComplete(RSCommandData data) {
        return List.of();
    }

}
