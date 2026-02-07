import dev.slne.surf.surfapi.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

dependencies {
    api(project(":surf-tutorial-api"))
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.tutorial.server.lobby.PaperMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    authors.add("red")

    serverDependencies {
        registerRequired("surf-tutorial-paper")
    }
}