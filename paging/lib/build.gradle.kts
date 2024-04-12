plugins {
  id("signal-library")
}

android {
  namespace = "org.spark.paging"
}

dependencies {
  implementation(project(":core-util"))
}
