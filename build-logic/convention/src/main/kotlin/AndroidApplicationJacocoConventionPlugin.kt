/*
 * Copyright (C) 2023 The N's lab Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.nlab.reminder.convention.configureJacocoToolVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.*

/**
 * @author Doohyun
 */
class AndroidApplicationJacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.gradle.jacoco")
                apply("com.android.application")
            }
            configureJacocoToolVersion()

            val extension = extensions.getByType<ApplicationAndroidComponentsExtension>()
            val jacocoTestReport = tasks.create("jacocoTestReport")

            extension.onVariants { variant ->
                val testTaskName = "test${variant.name.capitalize(Locale.getDefault())}UnitTest"
                val reportTask = tasks.register("jacoco${testTaskName.capitalize(Locale.getDefault())}Report", JacocoReport::class) {
                    dependsOn(testTaskName)

                    reports {
                        xml.required.set(true)
                        html.required.set(true)
                    }

                    classDirectories.setFrom(
                        fileTree("$buildDir/tmp/kotlin-classes/${variant.name}") {
                            exclude(listOf(
                                "**/nlab/**"
                            ))
                        }
                    )

                    sourceDirectories.setFrom(files("$projectDir/src/main/java", "$projectDir/src/main/kotlin"))
                    executionData.setFrom(file("$buildDir/jacoco/$testTaskName.exec"))
                }

                jacocoTestReport.dependsOn(reportTask)
            }

            tasks.withType<Test>().configureEach {
                configure<JacocoTaskExtension> {
                    // Required for JaCoCo + Robolectric
                    // https://github.com/robolectric/robolectric/issues/2230
                    // TODO: Consider removing if not we don't add Robolectric
                    isIncludeNoLocationClasses = true

                    // Required for JDK 11 with the above
                    // https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
                    excludes = listOf("jdk.internal.*")
                }
            }

        }
    }
}