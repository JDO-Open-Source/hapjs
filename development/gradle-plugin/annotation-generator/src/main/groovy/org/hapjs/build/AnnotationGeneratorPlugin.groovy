/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hapjs.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class AnnotationGeneratorPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // 获取项目的构建目录
        def buildDir = project.buildDir
        // 判断是否为 application project
        if (project.configurations.findByName('apk') != null) {
            // 定义Java输出目录
            def javaOutputDir = new File(buildDir, "generated/hap/src/main/java")
            // 将Java输出目录添加到源代码目录中
            project.android.sourceSets.main.java.srcDirs += javaOutputDir

            project.afterEvaluate {
                // 遍历所有的应用变体
                project.android.applicationVariants.all { variant ->
                    // 获取构建类型
                    def buildType = variant.name
                    // 构建类型首字母大写
                    def capitalizeBuildType = buildType.capitalize()
                    // 获取编译Java任务
                    def compileJavaTask = project.tasks.getByName(
                            "compile${capitalizeBuildType}JavaWithJavac")
                    // 获取合并资源任务
                    def mergeAssetsTask = project.tasks.getByName(
                            "merge${capitalizeBuildType}Assets")
                    // 将编译Java任务依赖于合并资源任务
                    compileJavaTask.dependsOn(mergeAssetsTask)
                    // 在编译Java任务执行前执行以下操作
                    compileJavaTask.doFirst {
                        // 定义合并后的资源目录
                        def mergedAssetsDir = new File(buildDir,
                                "intermediates/merged_assets/${buildType}/out/hap")
                        // 如果合并后的资源目录不存在，则重新定义合并后的资源目录
                        if (!mergedAssetsDir.exists()) {
                            mergedAssetsDir = new File(buildDir,
                                    "intermediates/merged_assets/${buildType}/merge${capitalizeBuildType}Assets/out/hap")
                        }
                        // 定义card.json文件
                        def cardJsonFile = new File(buildDir, "intermediates/merged_assets/${buildType}/out/hap/card.json")
                        // 如果card.json文件不存在，则将cardJsonFile设置为null
                        if (!cardJsonFile.exists()) {
                            cardJsonFile = null
                        }
                        // 创建Java资源处理器，并执行处理操作，传入合并后的资源目录、Java输出目录和card.json文件
                        def processor = new JavaResourceProcessor(mergedAssetsDir, javaOutputDir, cardJsonFile)
                        // 执行处理操作
                        processor.process()
                    }
                }
            }
        } else {
            // 如果不是application project，执行以下操作
            project.afterEvaluate {
                // 遍历所有的库变体
                project.android.libraryVariants.all { variant ->
                    // 获取构建类型
                    def buildType = variant.name
                    // 构建类型首字母大写
                    def capitalizeBuildType = buildType.capitalize()
                    // 判断是否启用R8，如果未启用，则使用Proguard
                    def proguardType = project.properties['android.enableR8'] ==
                            'false' ? 'Proguard' : 'R8'
                    // 获取混淆任务
                    def proguardTask = project.tasks.findByName(
                            "transformClassesAndResourcesWith${proguardType}For${capitalizeBuildType}")
                    // 如果混淆任务存在，则在任务开始前执行以下操作
                    if (proguardTask != null) {
                        proguardTask.doFirst {
                            // 定义混淆规则文件
                            def proguardFile = new File(buildDir,
                                    "intermediates/proguard-rules/${buildType}/aapt_rules.txt")
                            // 如果混淆规则文件存在，则读取并修改内容
                            if (proguardFile.exists()) {
                                def text = new StringBuilder()
                                proguardFile.withReader('UTF-8') { reader ->
                                    reader.eachLine {
                                        // 如果行中不包含"${applicationId}"，则添加到text中
                                        if (!it.contains("\${applicationId}")) {
                                            text.append(it).append('\n')
                                        }
                                    }
                                }
                                // 将修改后的内容写回混淆规则文件
                                proguardFile.write(text.toString(), 'UTF-8')
                            }
                        }
                    }
                }
            }
        }
    }
}