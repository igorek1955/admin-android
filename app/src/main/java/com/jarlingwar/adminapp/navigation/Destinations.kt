package com.jarlingwar.adminapp.navigation

import com.jarlingwar.adminapp.R

interface Destination {
    val route: String
    val title: Int
}

object Destinations {
    object Auth : Destination {
        override val route: String = "auth"
        override val title: Int = R.string.authorization
    }

    object Splash : Destination {
        override val route: String = "splash"
        override val title: Int = 0
    }

    object PendingListings : Destination {
        override val route: String = "pending"
        override val title: Int = R.string.pending_listings
    }

    object PublishedListings : Destination {
        override val route: String = "published"
        override val title: Int = R.string.published_listings
    }

    object Listing : Destination {
        override val route: String = "listing"
        override val title: Int = 0
    }

    object User : Destination {
        override val route: String = "user"
        override val title: Int = 0
    }

    object Users : Destination {
        override val route: String = "users"
        override val title: Int = R.string.users
    }

    object ReportedUsers : Destination {
        override val route: String = "reportedUsers"
        override val title: Int = 0
    }

    object UserSearch : Destination {
        override val route: String = "userSearch"
        override val title: Int = 0
    }

    object ListingSearch : Destination {
        override val route: String = "listingSearch"
        override val title: Int = 0
    }

    object UserReviews : Destination {
        override val route: String = "userReviews"
        override val title: Int = 0
    }

    object Reviews : Destination {
        override val route: String = "reviews"
        override val title: Int = 0
    }
}



