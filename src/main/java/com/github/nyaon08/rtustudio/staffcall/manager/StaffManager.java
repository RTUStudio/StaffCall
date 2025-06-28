package com.github.nyaon08.rtustudio.staffcall.manager;

import com.github.nyaon08.rtustudio.staffcall.StaffCall;
import com.github.nyaon08.rtustudio.staffcall.data.Help;
import com.google.gson.JsonObject;
import kr.rtuserver.framework.bukkit.api.platform.JSON;
import kr.rtuserver.framework.bukkit.api.storage.Storage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class StaffManager {

    private final StaffCall plugin;

    public void add(UUID uuid, Help help) {
        System.out.println("add uuid: " + uuid);
        Storage storage = plugin.getStorage();
        storage.add("staffcall", JSON.of("uuid", uuid.toString()).append("message", help.message())).join();
    }

    public void remove(UUID uuid) {
        System.out.println("remove uuid: " + uuid);
        Storage storage = plugin.getStorage();
        storage.set("staffcall", JSON.of("uuid", uuid.toString()), JSON.of()).join();
    }

    public Map<UUID, Help> getRequests() {
        Storage storage = plugin.getStorage();
        List<JsonObject> list = storage.get("staffcall", JSON.of()).join();

        Map<UUID, Help> requests = new HashMap<>();
        for (JsonObject jsonObject : list) {
            UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
            String message = jsonObject.get("message").getAsString();
            requests.put(uuid, new Help(uuid, message, new java.util.Date()));
        }
        return requests;
    }

    public boolean contains(UUID uuid) {
        Storage storage = plugin.getStorage();
        List<JsonObject> list = storage.get("staffcall", JSON.of("uuid", uuid.toString())).join();
        return !list.isEmpty();
    }

}
