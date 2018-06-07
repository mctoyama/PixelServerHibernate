package org.pixelndice.table.pixelserverhibernate

import org.hibernate.annotations.NamedQueries
import org.hibernate.annotations.NamedQuery

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity

@NamedQueries(
        NamedQuery(name="deleteDuplicatedGame",
                   query="delete from Game where hostname = :hostname and port = :port"),
        NamedQuery(name="refreshGame",
                   query="from Game where refresh <= :now"),
        NamedQuery(name="selectGameForPlayer",
                   query="SELECT game FROM Game as game INNER JOIN game.players as players WHERE players.id = :player_id"),
        NamedQuery(name="gameByUUID",
                   query="from Game where uuid = :uuid"),
        NamedQuery(name="deleteGameByUUID",
                   query="delete Game where uuid = :uuid")
)
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
    @Enumerated(EnumType.STRING)
    var rpgGameSystem: RPGGameSystem = RPGGameSystem.GENERIC

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