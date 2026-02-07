plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

dependencies {
    api(project(":surf-tutorial-api"))
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.tutorial.paper.PaperMain")
    authors.add("red")
    foliaSupported(true)

    generateLibraryLoader(false)
}