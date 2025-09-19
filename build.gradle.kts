plugins {
    // These plugins are declared but not applied here; they’ll be applied in subprojects
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

subprojects {
    // Ensure all subprojects use the correct Kotlin and JVM settings
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17" // Gradle 9 requires JVM 17+
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
