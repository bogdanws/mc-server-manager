package ws.bogdan.mcserver.model;

import java.util.Objects;

public class Achievement {
    private String id;
    private String title;
    private String description;
    private int xpReward;
    private Achievement parentAchievement;

    public Achievement(String id, String title, String description, int xpReward, Achievement parentAchievement) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = description;
        this.xpReward = xpReward;
        this.parentAchievement = parentAchievement;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getXpReward() {
        return xpReward;
    }

    public Achievement getParentAchievement() {
        return parentAchievement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Achievement))
            return false;
        Achievement other = (Achievement) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Achievement{id='" + id + "', title='" + title + "', xpReward=" + xpReward + "}";
    }
}
