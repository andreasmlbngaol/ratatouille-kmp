package com.kotlinonly.moprog.database.users

import com.kotlinonly.moprog.database.utils.BaseEntityClass
import com.kotlinonly.moprog.database.utils.StringBaseEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class UsersEntity(id: EntityID<String>): StringBaseEntity(id) {
    var name by Users.name
    var email by Users.email
    var profilePictureUrl by Users.profilePictureUrl
    var coverPictureUrl by Users.coverPictureUrl
    var method by Users.method
    var bio by Users.bio
    var isEmailVerified by Users.isEmailVerified

    companion object: BaseEntityClass<String, UsersEntity>(Users)
}