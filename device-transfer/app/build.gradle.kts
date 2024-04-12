plugins {
  id("signal-sample-app")
}

android {
  namespace = "org.spark.devicetransfer.app"

  defaultConfig {
    applicationId = "org.spark.devicetransfer.app"

    ndk {
      abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
    }

    buildConfigField("String", "LIBSIGNAL_VERSION", "\"libsignal ${libs.versions.libsignal.client.get()}\"")
  }
}

dependencies {
  implementation(project(":device-transfer"))
}
