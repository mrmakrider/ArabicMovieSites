// use an integer for version numbers
version = 1

cloudstream {
    // All of these properties are optional, you can safely remove them

    language = "ar"
    // description = "Plugin for TukTukCinema Arabic streaming site"
    authors = listOf("YourUsername")

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
     * */
    status = 1 // 0: Down, 1: Ok, 2: Slow, 3: Beta only

    tvTypes = listOf(
        "TvSeries",
        "Movie",
        "Anime",
        "AsianDrama"
    )

    iconUrl = "https://www.google.com/s2/favicons?domain=tuk.cam&sz=%size%"
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("com.github.recloudstream:gradle:-SNAPSHOT")
    implementation("org.jsoup:jsoup:1.13.1")
}
