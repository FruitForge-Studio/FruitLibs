package com.fruitforge.fruitLibs.core.event.events;

import com.fruitforge.fruitLibs.api.event.Event;

import java.util.Set;

public class ConfigReloadEvent extends Event {

    private final Set<String> reloadedFiles;
    private final long reloadTime;

    public ConfigReloadEvent(Set<String> reloadedFiles, long reloadTime) {
        this.reloadedFiles = reloadedFiles;
        this.reloadTime = reloadTime;
    }

    public Set<String> getReloadedFiles() {
        return reloadedFiles;
    }

    public long getReloadTime() {
        return reloadTime;
    }

    public int getFileCount() {
        return reloadedFiles.size();
    }
}