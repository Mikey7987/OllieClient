package com.ollie.pvp.gui;

public enum Category {
    COMBAT("Combat",   "C"),
    MOVEMENT("Movement","M"),
    RENDER("Render",   "R"),
    PLAYER("Player",   "P"),
    MISC("Misc",       "X");

    public final String displayName;
    public final String icon;

    Category(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }
}
