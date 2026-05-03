package ws.bogdan.mcserver.service;

import ws.bogdan.mcserver.audit.AuditService;
import ws.bogdan.mcserver.exception.PluginDependencyException;
import ws.bogdan.mcserver.model.Plugin;

public class PluginService {
    private final ServerState state;

    public PluginService(ServerState state) {
        this.state = state;
    }

    public void installPlugin(Plugin p) {
        AuditService.getInstance().logAction("INSTALL_PLUGIN",
                "name=" + p.getName() + ";version=" + p.getVersion() + ";author=" + p.getAuthor());
        for (Plugin dep : p.getDependencies()) {
            boolean satisfied = state.getPlugins().stream()
                    .anyMatch(installed -> installed.equals(dep) && installed.isEnabled());
            if (!satisfied) {
                throw new PluginDependencyException(
                        "Cannot install '" + p.getName() + "': dependency '" + dep.getName()
                                + "' is not installed/enabled");
            }
        }
        state.getPlugins().add(p);
        p.setEnabled(true);
        System.out.println("Installed plugin: " + p.getName() + " v" + p.getVersion());
    }

    public void disablePlugin(String name) {
        AuditService.getInstance().logAction("DISABLE_PLUGIN", "name=" + name);
        Plugin target = state.getPlugins().stream()
                .filter(p -> p.getName().equals(name) && p.isEnabled())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found or already disabled: " + name));

        for (Plugin other : state.getPlugins()) {
            if (other.isEnabled() && !other.equals(target)) {
                boolean depends = other.getDependencies().stream()
                        .anyMatch(dep -> dep.equals(target));
                if (depends) {
                    throw new PluginDependencyException(
                            "Cannot disable '" + name + "': plugin '" + other.getName() + "' depends on it");
                }
            }
        }
        target.setEnabled(false);
        System.out.println("Disabled plugin: " + name);
    }
}
