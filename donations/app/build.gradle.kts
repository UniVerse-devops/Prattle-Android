plugins {
  id("signal-sample-app")
}

android {
  namespace = "org.spark.donations.app"

  defaultConfig {
    applicationId = "org.spark.donations.app"
  }
}

dependencies {
  implementation(project(":donations"))
  implementation(project(":core-util"))
}
