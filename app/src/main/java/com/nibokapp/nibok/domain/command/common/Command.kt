package com.nibokapp.nibok.domain.command.common

/**
 * A command can be executed to realize a use case of the application.
 *
 * @param T the type of the commands's result
 */
interface Command<out T> {

    /**
     * Execute a command.
     *
     * @return the result of the command's execution
     */
    fun execute(): T
}