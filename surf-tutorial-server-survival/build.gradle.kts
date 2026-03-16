plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.tutorial.server.survival.PaperMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    authors.add("red")

    serverDependencies {
        register("surf-npc-paper")
        register("surf-protect")
    }
}

dependencies {
    compileOnly("dev.slne.surf.npc:surf-npc-api:1.21.11-1.6.0-SNAPSHOT")
    compileOnly(files("libs/surf-protect-1.21.11-3.0.1-SNAPSHOT-all.jar"))
}