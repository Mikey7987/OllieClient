package com.ollie.pvp;

import com.ollie.pvp.gui.Category;
import net.minecraft.client.MinecraftClient;

public abstract class Module {

    private final String name;
    private final Category category;
    private boolean enabled;

    protected Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public String getName()        { return name; }
    public Category getCategory()  { return category; }
    public boolean isEnabled()     { return enabled; }
    public void setEnabled(boolean on) { this.enabled = on; }
    public void toggle()           { this.enabled = !this.enabled; }

    public abstract void tick(MinecraftClient client);
}
