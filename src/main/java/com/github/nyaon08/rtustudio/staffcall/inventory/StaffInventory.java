package com.github.nyaon08.rtustudio.staffcall.inventory;

import com.github.nyaon08.rtustudio.staffcall.StaffCall;
import com.github.nyaon08.rtustudio.staffcall.configuration.IconConfig;
import com.github.nyaon08.rtustudio.staffcall.data.Help;
import com.github.nyaon08.rtustudio.staffcall.manager.StaffManager;
import kr.rtuserver.framework.bukkit.api.format.ComponentFormatter;
import kr.rtuserver.framework.bukkit.api.inventory.RSInventory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.apache.commons.collections4.ListUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

public class StaffInventory extends RSInventory<StaffCall> {

    private final IconConfig iconConfig;

    private final Player player;
    private final StaffManager manager;

    private final List<Help> data = new ArrayList<>();

    @Getter
    private final Inventory inventory;

    private int page = 0;
    private int maxPage = 0;

    public StaffInventory(StaffCall plugin, Player player) {
        super(plugin);
        this.iconConfig = plugin.getIconConfig();
        this.manager = plugin.getStaffManager();
        this.player = player;
        Component title = ComponentFormatter.mini(message().get(player, "title"));
        this.inventory = createInventory(54, title);
        data.addAll(manager.getRequests().values());
        this.maxPage = Math.max(partition().size() - 1, 0);
        loadPage(0);
    }

    private List<List<Help>> partition() {
        if (data.isEmpty()) return new ArrayList<>();
        return ListUtils.partition(data, 45);
    }

    public void refresh() {
        this.data.clear();
        this.data.addAll(manager.getRequests().values());
        this.maxPage = Math.max(partition().size() - 1, 0);
        this.page = Math.min(page, maxPage);
        loadPage(page);
    }

    private List<Help> page(int page) {
        List<List<Help>> partition = partition();
        if (partition.isEmpty()) return new ArrayList<>();
        else return partition.get(page);
    }

    protected void loadPage(int page) {
        this.page = page;
        inventory.clear();
        inventory.setItem(45, pageIcon(Navigation.FIRST));
        inventory.setItem(46, pageIcon(Navigation.PREVIOUS));
        inventory.setItem(52, pageIcon(Navigation.NEXT));
        inventory.setItem(53, pageIcon(Navigation.LAST));
        for (Help help : page(page)) inventory.addItem(item(help));
    }

    private ItemStack pageIcon(Navigation navigation) {
        String name = navigation.name().toLowerCase();
        String available = navigation.check(page, maxPage) ? "available" : "unavailable";
        String display = message().get(player, "icon.menu.pagination." + name + "." + available);
        display = display.replace("[current]", String.valueOf(page + 1)).replace("[max]", String.valueOf(maxPage + 1));
        return iconConfig.get("menu.pagination." + name + "." + available, display);
    }

    protected void loadPage(Navigation navigation) {
        if (!navigation.check(page, maxPage)) return;
        switch (navigation) {
            case FIRST -> loadPage(0);
            case PREVIOUS -> loadPage(page - 1);
            case NEXT -> loadPage(page + 1);
            case LAST -> loadPage(maxPage);
        }
    }

    private ItemStack item(Help help) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();

        Player target = getPlugin().getServer().getPlayer(help.uuid());
        meta.setOwningPlayer(target);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private List<Component> toComponents(String message) {
        List<Component> result = new ArrayList<>();
        String[] split = message.split("\n");
        for (String str : split) result.add(ComponentFormatter.mini("<!italic><white>" + str));
        return result;
    }

    @Override
    public boolean onClick(Event<InventoryClickEvent> event, Click click) {
        if (inventory.isEmpty()) return false;
        if (event.isInventory()) return false;
        int slot = click.slot();
        if (slot < 0) return false;
        switch (slot) {
            case 45 -> loadPage(Navigation.FIRST);
            case 46 -> loadPage(Navigation.PREVIOUS);
            case 52 -> loadPage(Navigation.NEXT);
            case 53 -> loadPage(Navigation.LAST);
            default -> {
                List<Help> list = page(page);
                if (list.size() <= slot) return false;
                Help help = list.get(slot);

                getPlugin().getStaffManager().remove(help.uuid());
                Player target = getPlugin().getServer().getPlayer(help.uuid());

                event.player().teleport(Objects.requireNonNull(target).getLocation());
                chat().announce(target, ComponentFormatter.mini(message().get(target, "staffAccept").replace("%player%", event.player().getName())));

                loadPage(Math.min(page, maxPage));
            }
        }
        return false;
    }

    @Override
    public boolean onDrag(Event<InventoryDragEvent> event, Drag drag) {
        return true;
    }

    @Override
    public void onClose(Event<InventoryCloseEvent> event) {

    }

    @RequiredArgsConstructor
    protected enum Navigation {
        FIRST((page, maxPage) -> page != 0),
        PREVIOUS((page, maxPage) -> page > 0),
        NEXT((page, maxPage) -> page < maxPage),
        LAST((page, maxPage) -> !Objects.equals(page, maxPage));

        private final BiPredicate<Integer, Integer> condition;

        public boolean check(int page, int maxPage) {
            return condition.test(page, maxPage);
        }
    }


}
