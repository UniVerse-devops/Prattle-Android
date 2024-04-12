plugins {
  id("signal-sample-app")
}

android {
  namespace = "org.spark.spinnertest"

  defaultConfig {
    applicationId = "org.spark.spinnertest"
  }
}

dependencies {
  implementation(project(":spinner"))

  implementation(libs.androidx.sqlite)
  implementation(libs.signal.android.database.sqlcipher)
}
