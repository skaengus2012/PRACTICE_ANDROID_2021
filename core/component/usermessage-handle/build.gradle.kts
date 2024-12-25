plugins {
    alias(libs.plugins.nlab.android.library)
    alias(libs.plugins.nlab.android.library.compose.component)
    alias(libs.plugins.nlab.android.library.di)
    alias(libs.plugins.nlab.android.library.jacoco)
    kotlin("kapt")
}

android {
    namespace = "com.nlab.reminder.core.component.usermessage.handle"
}

dependencies {
    implementation(projects.core.annotation)
    implementation(projects.core.component.usermessage)
    implementation(projects.core.kotlinxCoroutine)
    implementation(projects.core.statekit)
    kapt(projects.statekit.compiler)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)

    testImplementation(projects.core.uitextTest)
    testImplementation(projects.statekit.test)
    testImplementation(projects.testkit)
}