package dev.slne.surf.tutorial.paper.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.tutorial.api.tutorial.Tutorial
import dev.slne.surf.tutorial.paper.service.tutorialService

class TutorialArgument(nodeName: String) :
    CustomArgument<Tutorial, String>(StringArgument(nodeName), { info ->
        tutorialService.getTutorial(info.input)
            ?: throw CustomArgumentException.fromAdventureComponent(
                buildText {
                    appendErrorPrefix()
                    error("Das Tutorial wurde nicht gefunden.")
                })
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                tutorialService.getTutorials().map { it.name }
            }
        )
    }
}

inline fun CommandTree.tutorialArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    TutorialArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.tutorialArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    TutorialArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.tutorialArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(TutorialArgument(nodeName).setOptional(optional).apply(block))