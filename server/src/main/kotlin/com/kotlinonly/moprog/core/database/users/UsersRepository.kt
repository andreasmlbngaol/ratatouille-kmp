import com.kotlinonly.moprog.core.database.users.Users
import com.kotlinonly.moprog.core.database.users.toUser
import com.kotlinonly.moprog.data.auth.User
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object UsersRepository {
    fun findById(id: String) = transaction {
        Users
            .selectAll()
            .where { Users.id eq id }
            .map { it.toUser() }
            .firstOrNull()
    }

    fun save(user: User) = transaction {
        Users.insert {
            it[id] = user.id
            it[name] = user.name
            it[email] = user.email
            it[profilePictureUrl] = user.profilePictureUrl
            it[method] = user.method
            it[isEmailVerified] = user.isEmailVerified
            it[createdAt] = user.createdAt
        }
    }

}