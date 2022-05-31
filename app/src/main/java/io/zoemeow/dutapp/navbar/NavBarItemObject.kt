package io.zoemeow.dutapp.navbar

import io.zoemeow.dutapp.R

object NavBarItemObject {
    val BarItems = listOf(
        NavBarItems(
            title = "Home",
            imageId = R.drawable.ic_baseline_home_24,
            route = "home"
        ),
        NavBarItems(
            title = "News",
            imageId = R.drawable.ic_baseline_newspaper_24,
            route = "news"
        ),
        NavBarItems(
            title = "Subject",
            imageId = R.drawable.ic_baseline_subject_24,
            route = "subject"
        ),
        NavBarItems(
            title = "Settings",
            imageId = R.drawable.ic_baseline_settings_24,
            route = "settings"
        )
    )
}