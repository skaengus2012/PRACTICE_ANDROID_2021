plugins {
    alias(libs.plugins.nlab.jvm.library)
    alias(libs.plugins.nlab.jvm.library.jacoco)
}

dependencies {
    implementation(projects.core.data)

    testImplementation(projects.testkit)
    testImplementation(projects.core.dataTest)
}