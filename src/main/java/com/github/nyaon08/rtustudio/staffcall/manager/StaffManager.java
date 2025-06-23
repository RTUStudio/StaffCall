package com.github.nyaon08.rtustudio.staffcall.manager;

import com.github.nyaon08.rtustudio.staffcall.StaffCall;
import com.github.nyaon08.rtustudio.staffcall.data.Help;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class StaffManager {

    private final StaffCall staffCall;

    @Getter
    private final Map<UUID, Help> requests = new HashMap<>();

    public void add(UUID uuid, Help help) {
        requests.put(uuid, help);
    }

    public void remove(UUID uuid) {
        requests.remove(uuid);
    }

    public Help get(UUID uuid) {
        return requests.get(uuid);
    }

    public boolean contains(UUID uuid) {
        return requests.containsKey(uuid);
    }

}
