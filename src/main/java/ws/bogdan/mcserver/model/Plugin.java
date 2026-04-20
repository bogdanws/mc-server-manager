package ws.bogdan.mcserver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Plugin {
    private String name;
    private String version;
    private String author;
    private boolean enabled;
    private List<Plugin> dependencies = new ArrayList<>();

    public Plugin(String name, String version, String author) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.version = Objects.requireNonNull(version, "version must not be null");
        this.author = Objects.requireNonNull(author, "author must not be null");
        this.enabled = false;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setVersion(String version) {
        this.version = Objects.requireNonNull(version);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addDependency(Plugin p) {
        dependencies.add(Objects.requireNonNull(p, "dependency must not be null"));
    }

    public List<Plugin> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Plugin))
            return false;
        Plugin other = (Plugin) o;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Plugin{name='" + name + "', version='" + version + "', enabled=" + enabled + "}";
    }
}
