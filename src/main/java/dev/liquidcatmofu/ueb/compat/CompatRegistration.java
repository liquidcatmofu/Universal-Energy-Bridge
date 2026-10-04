package dev.liquidcatmofu.ueb.compat;

/** Setup-time compat registration that must run before BlockEntity capability attachment. */
@FunctionalInterface
public interface CompatRegistration {
    void register();
}
