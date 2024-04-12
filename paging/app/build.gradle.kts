plugins {
  id("signal-sample-app")
}

android {
  namespace = "org.spark.pagingtest"

  defaultConfig {
    applicationId = "org.spark.pagingtest"
  }
}

dependencies {
  implementation(project(":paging"))
}
