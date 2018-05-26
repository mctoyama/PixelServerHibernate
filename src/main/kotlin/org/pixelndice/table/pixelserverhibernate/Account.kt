package org.pixelndice.table.pixelserverhibernate

import org.hibernate.Session
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.time.LocalDateTime
import java.util.*
import java.util.regex.Pattern
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.persistence.*
import kotlin.experimental.or
import kotlin.experimental.xor

@Entity
class Account{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column
    var name: String = ""

    @Column(unique = true, nullable = false)
    var email: String = ""

    @Column
    var password: String = ""

    @Column
    var creationDate: LocalDateTime = LocalDateTime.now()

    fun hash(){
        password = hash(password.toCharArray())
    }

    override fun equals(other: Any?): Boolean {

        if (other == null)
            return false

        if (other !is Account)
            return false

        if (other === this)
            return true

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return "$id - $name - $email - $password - $creationDate"
    }

    companion object {

        fun login(session: Session, email: String, password: String): Account? {

            val query = session.createQuery("from Account acc WHERE acc.email=:email", Account::class.java)
            query.setParameter("email", email)
            val list = query.list()
            session.close()

            if( list.size == 0 )
                return null

            val account = list[0]
            val auth = authenticate(password.toCharArray(), account.password)

            if( !auth )
                return null

            return account
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        private val random = SecureRandom()

        /**
         * Each token produced by this class uses this identifier as a prefix.
         */
        private val ID = "$31$"

        /**
         * The minimum recommended cost 16, used by default
         * 0 < cost < 31
         */
        private val COST = 16

        private val ALGORITHM = "PBKDF2WithHmacSHA1"

        private val SIZE = 128

        private val layout = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})")

        /**
         * Hash a password for storage.
         *
         * @return a secure authentication token to be stored for later authentication
         */
        private fun hash(password: CharArray): String {
            val salt = ByteArray(SIZE / 8)
            random.nextBytes(salt)
            val dk = pbkdf2(password, salt, 1 shl COST)
            val hash = ByteArray(salt.size + dk.size)
            System.arraycopy(salt, 0, hash, 0, salt.size)
            System.arraycopy(dk, 0, hash, salt.size, dk.size)
            val enc = Base64.getUrlEncoder().withoutPadding()
            return ID + COST + '$' + enc.encodeToString(hash)
        }

        /**
         * Authenticate with a password and a stored password token.
         *
         * @return true if the password and token match
         */
        private fun authenticate(password: CharArray, token: String): Boolean {
            val m = layout.matcher(token)
            if (!m.matches())
                throw IllegalArgumentException("Invalid token format")
            val iterations = iterations(Integer.parseInt(m.group(1)))
            val hash = Base64.getUrlDecoder().decode(m.group(2))
            val salt = Arrays.copyOfRange(hash, 0, SIZE / 8)
            val check = pbkdf2(password, salt, iterations)
            var zero: Byte = 0
            for (idx in check.indices)
                zero = zero.or( hash[salt.size + idx].xor(check[idx]) )

            val tmpZero: Byte = 0

            return zero == tmpZero
        }

        private fun iterations(cost: Int): Int {
            if (cost < 0 || cost > 31)
                throw IllegalArgumentException("cost: " + cost)
            return 1 shl cost
        }

        private fun pbkdf2(password: CharArray, salt: ByteArray, iterations: Int): ByteArray {
            val spec = PBEKeySpec(password, salt, iterations, SIZE)
            try {
                val f = SecretKeyFactory.getInstance(ALGORITHM)
                return f.generateSecret(spec).encoded
            } catch (ex: NoSuchAlgorithmException) {
                throw IllegalStateException("Missing algorithm: " + ALGORITHM, ex)
            } catch (ex: InvalidKeySpecException) {
                throw IllegalStateException("Invalid SecretKeyFactory", ex)
            }

        }
    }
}