plugins {
    alias(libs.plugins.kotlinJvm)
    application
}

application {
    mainClass = "com.genovich.consoleui.main.kt"
}

dependencies {
    implementation(projects.shared)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.components.oneof)
    implementation(libs.components.whileactive)
}