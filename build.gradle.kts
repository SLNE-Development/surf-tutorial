buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
    dependencies {
        classpath("dev.slne.surf:surf-api-gradle-plugin:1.21.11+")
    }
}

allprojects {
    group = "dev.slne.surf.tutorial"
    version = findProperty("version") as String
}

//subprojects {
//    afterEvaluate {
//        plugins.withType<PublishingPlugin> {
//            configure<PublishingExtension> {
//                repositories {
//                    slneReleases()
//                }
//            }
//        }
//    }
//}