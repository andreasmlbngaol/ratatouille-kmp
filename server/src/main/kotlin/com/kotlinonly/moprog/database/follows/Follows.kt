package com.kotlinonly.moprog.database.follows

import com.kotlinonly.moprog.database.users.Users
import com.kotlinonly.moprog.database.utils.LongBaseTable

object Follows: LongBaseTable("follows") {
    val followerId = reference("follower_id", Users)
    val followedId = reference("followed_id", Users)

    init {
        uniqueIndex(followerId, followedId)
    }
}