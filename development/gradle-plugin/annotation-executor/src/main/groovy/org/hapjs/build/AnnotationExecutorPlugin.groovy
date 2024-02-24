/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hapjs.build

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 这是一个名为AnnotationExecutorPlugin的Gradle插件，它实现了Plugin<Project>接口。
 * 它的主要功能是配置Android项目的编译选项和注解处理器选项。
 *
 * 在apply方法中，它执行了以下操作：
 * 1. 创建了一个名为generatedAssetsDir的目录，该目录位于项目的构建目录下的"generated/hap/src/main/assets"路径中。
 *    这个目录可能用于存放生成的资源文件。
 * 2. 在generatedAssetsDir目录下，创建了一个名为generatedMetadataDir的目录，该目录的名称包含了项目的名称。
 *    这个目录可能用于存放由注解处理器生成的元数据。
 * 3. 配置了Android项目的默认配置的Java编译选项的注解处理器选项。设置了注解处理器的输出目录为generatedMetadataDir的绝对路径。
 *    这意味着注解处理器将把生成的文件输出到这个目录。
 * 4. 设置了Android项目的编译选项的源代码兼容性和目标兼容性为Java 1.8版本。这意味着项目的源代码需要符合Java 1.8的语法规则，
 *    编译后的字节码也需要能在Java 1.8的环境下运行。
 * 5. 将generatedAssetsDir目录添加到了Android项目的主源集的资源目录列表中。这意味着在构建项目时，Gradle会把这个目录下的文件视为资源文件，
 *    一并打包到最终的APK文件中。
 *
 * 总的来说，这个插件主要用于配置Android项目的编译和注解处理过程，以便于在构建项目时，能正确地处理注解并生成相应的资源和元数据文件。
 */

class AnnotationExecutorPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def generatedAssetsDir = new File(project.buildDir, "generated/hap/src/main/assets")
        def generatedMetadataDir = new File(generatedAssetsDir, "hap/" + project.name)

        project.android.defaultConfig.javaCompileOptions.annotationProcessorOptions.arguments =
                [ outputDir :  generatedMetadataDir.absolutePath]
        project.android.compileOptions.sourceCompatibility = JavaVersion.VERSION_1_8
        project.android.compileOptions.targetCompatibility = JavaVersion.VERSION_1_8
        project.android.sourceSets.main.assets.srcDirs += generatedAssetsDir
    }
}