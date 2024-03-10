package com.agile.planner.scripter;

public class PreProcessor {

    private boolean defaultConfig;

    private boolean log;

    private boolean imprt;

    private boolean exprt;

    private boolean build;

    private boolean stats;

    private boolean html;

    public PreProcessor(boolean defaultConfig, boolean log, boolean imprt, boolean exprt, boolean build, boolean stats, boolean html) {
        this.defaultConfig = defaultConfig;
        this.log = log;
        this.imprt = imprt;
        this.exprt = exprt;
        this.build = build;
        this.stats = stats;
        this.html = html;
    }

    public boolean isDefaultConfig() {
        return defaultConfig;
    }

    public boolean isLog() {
        return log;
    }

    public boolean isImprt() {
        return imprt;
    }

    public boolean isExprt() {
        return exprt;
    }

    public boolean isBuild() {
        return build;
    }

    public boolean isStats() {
        return stats;
    }

    public boolean isHtml() {
        return html;
    }
}
