plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

dependencies {
    api(project(":surf-tutorial-api"))
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.tutorial.server.survival.PaperMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    authors.add("red")
}