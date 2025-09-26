version = "1.0.0"

cloudstream {
    language = "ar"
    authors = listOf("YourName")
    
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

    iconUrl = "https://tuk.cam/favicon.ico"
}
