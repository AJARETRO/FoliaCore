package dev.ajaretro.foliaCore.data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Team {

    private final String name;
    private UUID owner;
    private final Set<UUID> members;

    public Team(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.members = ConcurrentHashMap.newKeySet();
        this.members.add(owner);
    }

    public Team(String name, UUID owner, Set<UUID> members) {
        this.name = name;
        this.owner = owner;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public int getSize() {
        return members.size();
    }

    public Map<String, Object> serialize() {
        return Map.of(
                "owner", owner.toString(),
                "members", members.stream().map(UUID::toString).collect(Collectors.toList())
        );
    }

    public static Team deserialize(String name, Map<String, Object> map) {
        UUID owner = UUID.fromString((String) map.get("owner"));

        List<String> memberStrings = (List<String>) map.get("members");

        Set<UUID> concurrentSet = ConcurrentHashMap.newKeySet(memberStrings.size());

        for (String s : memberStrings) {
            concurrentSet.add(UUID.fromString(s));
        }

        return new Team(name, owner, concurrentSet);
    }
}