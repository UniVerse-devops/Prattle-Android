plugins {
  id("signal-sample-app")
}

android {
  namespace = "org.spark.qrtest"

  defaultConfig {
    applicationId = "org.spark.qrtest"
  }
}

dependencies {
  implementation(project(":qr"))

  implementation(libs.google.zxing.android.integration)
  implementation(libs.google.zxing.core)
}
