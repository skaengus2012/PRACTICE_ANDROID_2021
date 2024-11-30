plugins {
    alias(libs.plugins.nlab.android.library)
    alias(libs.plugins.nlab.android.hilt)
}

android {
    namespace = "com.nlab.reminder.core.foundation.di"
}

dependencies {
    api(projects.core.foundation)

    implementation(projects.core.foundationImpl)
}