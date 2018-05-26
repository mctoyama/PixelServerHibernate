package org.pixelndice.table.pixelserverhibernate

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class Ping{

    fun constructor(){}

    @Id
    var uuid: String = UUID.randomUUID().toString()

    @OneToOne
    @JoinColumn(name = "Account_id", nullable = false, unique = true)
    var gm: Account? = null

    @Column(nullable = false)
    var campaign: String = ""

    @Column(nullable = false)
    var rpg: String = ""

    @ManyToMany
    var players = mutableSetOf<Account>()

    @Column(nullable = false)
    var hostname: String = ""

    @Column(nullable = false)
    var port: Int = 0

    @Column(nullable = false)
    var refresh: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var expire: LocalDateTime = LocalDateTime.now().plusMinutes(5)

    override fun equals(other: Any?): Boolean {

        if (other == null)
            return false

        if (other !is Ping)
            return false

        if (other === this)
            return true

        return this.uuid == other.uuid
    }

    override fun hashCode(): Int {
        return Objects.hash(uuid)
    }
}