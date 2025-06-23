package com.github.nyaon08.rtustudio.staffcall;

import com.github.nyaon08.rtustudio.staffcall.command.MainCommand;
import com.github.nyaon08.rtustudio.staffcall.command.StaffCommand;
import com.github.nyaon08.rtustudio.staffcall.configuration.IconConfig;
import com.github.nyaon08.rtustudio.staffcall.manager.StaffManager;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import lombok.Getter;
import org.bukkit.permissions.PermissionDefault;

public class StaffCall extends RSPlugin {

    @Getter
    private static StaffCall instance;
    @Getter
    private IconConfig iconConfig;
    @Getter
    private StaffManager staffManager;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        iconConfig = new IconConfig(this);
        staffManager = new StaffManager(this);

        registerPermission(getPlugin().getName() + ".help.receive", PermissionDefault.FALSE);

        registerCommand(new MainCommand(this), true);
        registerCommand(new StaffCommand(this));
    }

    @Override
    public void disable() {
        instance = null;
    }

}
