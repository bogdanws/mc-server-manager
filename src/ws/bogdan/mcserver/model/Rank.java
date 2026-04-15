package ws.bogdan.mcserver.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Rank implements Comparable<Rank> {
    private String name;
    private String prefix;
    private String color;
    private Set<String> permissions;
    private int weight;

    public Rank(String name, String prefix, String color, Set<String> permissions, int weight) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.prefix = Objects.requireNonNull(prefix, "prefix must not be null");
        this.color = Objects.requireNonNull(color, "color must not be null");
        this.permissions = new HashSet<>(Objects.requireNonNull(permissions, "permissions must not be null"));
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public boolean hasPermission(String perm) {
        return permissions.contains(perm);
    }

    public void grantPermission(String perm) {
        permissions.add(Objects.requireNonNull(perm, "perm must not be null"));
    }

    @Override
    public int compareTo(Rank other) {
        return Integer.compare(other.weight, this.weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Rank))
            return false;
        Rank other = (Rank) o;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Rank{name='" + name + "', prefix='" + prefix + "', weight=" + weight + "}";
    }
}
