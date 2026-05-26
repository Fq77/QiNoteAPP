plugins {
    alias(libs.plugins.android.application) apply false
    kotlin("android") version libs.versions.kotlin.get() apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
}
