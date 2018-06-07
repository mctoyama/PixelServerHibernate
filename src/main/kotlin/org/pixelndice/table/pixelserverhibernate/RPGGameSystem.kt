package org.pixelndice.table.pixelserverhibernate

import org.pixelndice.table.pixelprotocol.Protobuf

enum class RPGGameSystem{

    GENERIC;

    companion object {

        /**
         * Convert from Protobuf to Enum
         */
        fun fromProtobuf(rpg: org.pixelndice.table.pixelprotocol.Protobuf.RPGGameSystem): org.pixelndice.table.pixelserverhibernate.RPGGameSystem {

            return when(rpg){
                Protobuf.RPGGameSystem.GENERIC -> GENERIC
                else -> GENERIC
            }
        }

        /**
         * Convert from Enum to Protobuf
         */
        fun toProtobuf(rpg: org.pixelndice.table.pixelserverhibernate.RPGGameSystem): org.pixelndice.table.pixelprotocol.Protobuf.RPGGameSystem {
            return when(rpg){
                GENERIC -> Protobuf.RPGGameSystem.GENERIC
            }
        }

    }
}