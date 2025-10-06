package com.kotlinonly.moprog.database.users

import com.kotlinonly.moprog.data.auth.User
import com.kotlinonly.moprog.data.recipes.RecipeFilter
import com.kotlinonly.moprog.data.users.UserDetail
import com.kotlinonly.moprog.database.follows.FollowsRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import com.kotlinonly.moprog.database.utils.Repository
import com.kotlinonly.moprog.database.utils.updateWithTimestamps
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object UsersRepository: Repository<String, UsersEntity, User>(UsersEntity) {
    private fun UsersEntity.toUser() = User(
        id = this.id.value,
        name = this.name,
        email = this.email,
        profilePictureUrl = this.profilePictureUrl,
        coverPictureUrl = this.coverPictureUrl,
        method = this.method,
        isEmailVerified = this.isEmailVerified,
        createdAt = this.createdAt,
        bio = this.bio
    )

    override fun UsersEntity.toDomain() = this.toUser()

    fun existsByEmail(email: String) = exists { Users.email eq email }

    fun updateName(id: String, name: String) = transaction {
        Users.updateWithTimestamps({ Users.id eq id }) {
            it[Users.name] = name
        } > 0
    }

    fun updateEmailVerified(id: String, isEmailVerified: Boolean) = transaction {
        Users.updateWithTimestamps({ Users.id eq id }) {
            it[Users.isEmailVerified] = isEmailVerified
        } > 0
    }

    fun findDetailById(id: String, filter: RecipeFilter) = transaction {
        val userEntity = entityClass.findById(id) ?: return@transaction null

        val totalFollower = FollowsRepository.countFollowerById(id)
        val totalFollowing = FollowsRepository.countFollowingById(id)
        val topRecipes = RecipesRepository.findAll(RecipeFilter(limit = 3))
        val totalRecipes = RecipesRepository.countByAuthorId(id, filter)

        UserDetail(
            id = id,
            email = userEntity.email,
            name = userEntity.name,
            profilePictureUrl = userEntity.profilePictureUrl,
            coverPictureUrl = userEntity.coverPictureUrl,
            bio = userEntity.bio,
            totalFollower = totalFollower,
            totalFollowing = totalFollowing,
            totalRecipes = totalRecipes,
            topRecipes = topRecipes
        )
    }
}