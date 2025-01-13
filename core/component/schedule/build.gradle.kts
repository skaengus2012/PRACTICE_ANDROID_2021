plugins {
    alias(libs.plugins.nlab.android.library)
    alias(libs.plugins.nlab.android.library.component.compose)
    alias(libs.plugins.nlab.android.library.component.view)
}

android {
    namespace = "com.nlab.reminder.core.component.schedule"
}

dependencies {
    implementation(projects.core.android)
    implementation(projects.core.androidx.recyclerview)
    implementation(projects.core.component.toolbar)
    implementation(projects.core.dataDi)
    implementation(projects.core.kotlinxCoroutine)
    implementation(projects.core.translation)

    implementation(libs.androidx.constaintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.transition.ktx)

    testImplementation(projects.core.dataTest)
}