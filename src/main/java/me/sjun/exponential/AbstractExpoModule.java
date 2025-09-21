package me.sjun.exponential;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Helper abstract middle class. It is fine to bypass this.
 */
public abstract class AbstractExpoModule implements ExpoModule {
    public AbstractExpoModule() {
        this.expo = null;
    }

    /**
     * The Expo instance. Non-null while registered.
     */
    protected ExpoBase expo;

    /**
     * Returns the Expo instance.
     * @return The Expo instance
     */
    public @NotNull ExpoBase getExpo() {
        return Optional.ofNullable(expo).orElseThrow(IllegalStateException::new);
    }

    @Override
    public final void onRegistered(@NotNull ExpoBase expo) {
        this.expo = expo;
        onRegistration();
    }

    @Override
    public void onUnregistered(@NotNull ExpoBase expo) {
        onUnregistration();
    }

    /**
     * Called upon registration.
     */
    protected abstract void onRegistration();

    /**
     * Called upon unregistration.
     */
    protected abstract void onUnregistration();
}
