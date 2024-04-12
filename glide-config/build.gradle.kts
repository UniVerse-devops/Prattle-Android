plugins {
  id("signal-library")
  id("com.google.devtools.ksp")
}

android {
  namespace = "org.spark.glide"
}

dependencies {
  implementation(libs.glide.glide)
  ksp(libs.glide.ksp)
}
