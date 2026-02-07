plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

dependencies {
    api(project(":surf-tutorial-api"))
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.tutorial.paper.PaperMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    authors.add("red")
}