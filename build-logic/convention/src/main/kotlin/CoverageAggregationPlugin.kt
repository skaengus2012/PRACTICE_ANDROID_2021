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

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.*
import com.nlab.reminder.convention.aggregateTestCoverage
import com.nlab.reminder.convention.getJacocoTestClassDirectories
import com.nlab.reminder.convention.getJacocoTestSourcesDirectories
import com.nlab.reminder.convention.unitTestTaskName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.attributes.*
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoCoverageReport
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import java.io.File

/**
 * Jacoco aggregation plugin.
 *
 * jacoco-report-aggregation was not support AGP yet.
 * So, custom script will be used until AGP support.
 *
 * @author Doohyun
 * @see [reference](https://medium.com/@gmazzo65/generating-android-jvm-aggregated-coverage-reports-53e912b2e63c)
 * @see [agp_guide](https://docs.gradle.org/current/userguide/jacoco_report_aggregation_plugin.html)
 */
class CoverageAggregationPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        check(this == rootProject) {
            "The aggregation coverage plugin should be applied to the root project only."
        }

        with(pluginManager) {
            apply("base")
            apply("jacoco-report-aggregation")
        }

        val androidAggregatedVariantAttribute: Attribute<Boolean> =
            Attribute.of("com.android.variants.aggregated", Boolean::class.javaObjectType)
        val jacocoAggregation = configurations.getByName("jacocoAggregation")

        allprojects {
            plugins.withId("jacoco") {
                val childDependency = (dependencies.create(project) as ModuleDependency)
                val hasAndroidPlugins: Boolean = plugins.findPlugin("com.android.base") != null
                if (hasAndroidPlugins) {
                    childDependency.attributes {
                        attribute(androidAggregatedVariantAttribute, true)
                    }
                }

                jacocoAggregation.dependencies.add(childDependency)
            }

            plugins.withId("com.android.base") {
                val android = the<TestedExtension>()
                val androidComponents =
                    extensions.getByName<AndroidComponentsExtension<*, *, *>>("androidComponents")
                android.buildTypes.configureEach {
                    extensions.add(typeOf<Property<Boolean>>(), ::aggregateTestCoverage.name, objects.property())
                }
                android.productFlavors.configureEach {
                    extensions.add(typeOf<Property<Boolean>>(), ::aggregateTestCoverage.name, objects.property())
                }

                val jacocoVariants = objects.namedDomainObjectSet(Variant::class)
                androidComponents.onVariants { variant ->
                    afterEvaluate {
                        val hasJacocoTask = tasks.any { task -> task.name.contains("jacoco") }
                        if (hasJacocoTask) {
                            jacocoVariants.add(variant)
                        }
                    }
                }

                configurations.create("codeCoverageExecutionData") {
                    isCanBeConsumed = true
                    isCanBeResolved = false
                    isVisible = false
                    attributes {
                        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.VERIFICATION))
                        attribute(TestSuiteType.TEST_SUITE_TYPE_ATTRIBUTE, objects.named(TestSuiteType.UNIT_TEST))
                        attribute(
                            VerificationType.VERIFICATION_TYPE_ATTRIBUTE,
                            objects.named(VerificationType.JACOCO_RESULTS)
                        )
                    }
                    jacocoVariants.all variant@{
                        val execData = this@variant.unitTestTaskName()
                            ?.let { unitTestName -> tasks.named(unitTestName) }
                            ?.map { it.the<JacocoTaskExtension>().destinationFile!! }
                            ?: return@variant
                        outgoing.artifact(execData) {
                            type = ArtifactTypeDefinition.BINARY_DATA_TYPE
                        }
                    }
                }

                configurations.create("codeCoverageSources") {
                    isCanBeConsumed = true
                    isCanBeResolved = false
                    isVisible = false
                    attributes {
                        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.VERIFICATION))
                        attribute(TestSuiteType.TEST_SUITE_TYPE_ATTRIBUTE, objects.named(TestSuiteType.UNIT_TEST))
                        attribute(
                            VerificationType.VERIFICATION_TYPE_ATTRIBUTE,
                            objects.named(VerificationType.MAIN_SOURCES)
                        )
                    }
                    jacocoVariants.all variant@{
                        val sources = objects.setProperty(File::class)
                        sources.addAll(getJacocoTestSourcesDirectories(this@variant))
                        outgoing.artifacts(sources) {
                            type = ArtifactTypeDefinition.DIRECTORY_TYPE
                        }
                    }
                }

                val allVariantsClassesForCoverageReport by tasks.registering(Sync::class) {
                    jacocoVariants.all variant@{
                        dependsOn(this@variant.unitTestTaskName())
                        from(getJacocoTestClassDirectories(this@variant))
                    }
                    into(provider { temporaryDir })
                    duplicatesStrategy = DuplicatesStrategy.WARN // in case of duplicated classes
                }
                configurations.create("codeCoverageElements") {
                    isCanBeConsumed = true
                    isCanBeResolved = false
                    isVisible = false
                    attributes {
                        attribute(androidAggregatedVariantAttribute, true)
                        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
                        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
                        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.CLASSES))
                    }
                    jacocoVariants.all variant@{
                        outgoing.artifact(allVariantsClassesForCoverageReport) {
                            type = ArtifactTypeDefinition.JVM_CLASS_DIRECTORY
                        }
                    }
                }
            }
        }

        with(the<ReportingExtension>().reports) {
            create("jacocoTestReport", JacocoCoverageReport::class.java) {
                testType.set(TestSuiteType.UNIT_TEST)

                // control the report generation
                // reportTask.get().reports {
                //    html.required.set(false)
                // }
            }
        }
    }
}