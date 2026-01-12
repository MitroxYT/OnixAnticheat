package dev.onixac.api.manager;

import dev.onixac.api.command.OnixCommandBase;

import java.util.Optional;

public interface ICommandManager {
    /**
     * @param name
     * @since 1.0
     * @return Возвращает комманду если не найдет
     */
    Optional<OnixCommandBase> getCommand(String name);
    /**
     * @param command
     * @since 1.0
     */
    void registerCommmand(OnixCommandBase command);
    /**
     * @param command
     * @since 1.0
     */
    void unregisterCommmand(String name);
}
