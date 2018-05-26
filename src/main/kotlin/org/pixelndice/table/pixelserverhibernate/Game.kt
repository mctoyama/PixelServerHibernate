package org.pixelndice.table.pixelserverhibernate

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


@Entity
class Game{

    fun constructor(){}

    @Id
    var uuid: String = UUID.randomUUID().toString()

    @ManyToOne
    @JoinColumn(name = "Account_id", nullable = false)
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
    var refresh: LocalDateTime = LocalDateTime.now().plusHours(4)

    override fun equals(other: Any?): Boolean {

        if (other == null)
            return false

        if (other !is Game)
            return false

        if (other === this)
            return true

        return this.uuid == other.uuid
    }

    override fun hashCode(): Int {
        return Objects.hash(uuid)
    }
}