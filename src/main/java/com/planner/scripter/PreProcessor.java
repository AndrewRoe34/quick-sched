package com.planner.scripter;

public class PreProcessor {

    private boolean defaultConfig;

    private boolean log;

    private boolean stats;

    private boolean html;

    public PreProcessor(boolean defaultConfig, boolean log, boolean stats, boolean html) {
        this.defaultConfig = defaultConfig;
        this.log = log;
        this.stats = stats;
        this.html = html;
    }

    public boolean isDefaultConfig() {
        return defaultConfig;
    }

    public boolean isLog() {
        return log;
    }

    public boolean isStats() {
        return stats;
    }

    public boolean isHtml() {
        return html;
    }
}
