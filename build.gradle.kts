plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.4.20"
}

group = "com.fordoun"

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    addTestListener(
        object : TestListener {
            override fun afterSuite(p0: TestDescriptor?, result: TestResult?) {
                if (result == null) {
                    return
                }
                if (p0?.parent == null) {
                    println(
                        "test result: ${result.resultType}. ${result.testCount} ran; ${result.successfulTestCount} passed; ${result.failedTestCount} failed"
                    )
                }
            }
        }
    )
}

kotlin {
    jvm()
    js(IR) {
        outputModuleName = "fordoun"
        browser {
            binaries.library()
        }
        generateTypeScriptDefinitions()
        // TODO: add Headless Chrome to Dockerfile
        browser {
            testTask {
                enabled = false
            }
        }
    }

    sourceSets {
        commonMain {}
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvmMain {}
        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        jsMain {}
        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
