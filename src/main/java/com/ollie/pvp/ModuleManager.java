package com.ollie.pvp;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        register(new AimAssist());
        register(new TriggerBot());
        register(new AutoTotem());
        register(new AutoPot());
    }

    private void register(Module m) {
        modules.add(m);
    }

    public List<Module> all() {
        return modules;
    }

    public Module get(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    public void toggle(String name) {
        Module m = get(name);
        if (m == null) return;
        m.toggle();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                    Text.literal(m.getName() + " " + (m.isEnabled() ? "§aON" : "§cOFF")),
                    true
            );
        }
    }

    public int enabledCount() {
        int n = 0;
        for (Module m : modules) if (m.isEnabled()) n++;
        return n;
    }

    public void tick(MinecraftClient client) {
        for (Module m : modules) {
            if (m.isEnabled()) m.tick(client);
        }
    }
}
