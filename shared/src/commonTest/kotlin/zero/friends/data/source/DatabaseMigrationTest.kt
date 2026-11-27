package zero.friends.data.source

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import zero.friends.data.entity.GamerEntity
import zero.friends.data.entity.GamerEntity.Companion.toGamer
import zero.friends.data.entity.GamerEntity.Companion.toGamerEntity
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.model.WinnerOption

class DatabaseMigrationTest {

    private val legacyJson = Json { ignoreUnknownKeys = true }

    private class RecordingStatement : SQLiteStatement {
        var stepped = false
        var closed = false

        override fun bindBlob(index: Int, value: ByteArray) = Unit
        override fun bindDouble(index: Int, value: Double) = Unit
        override fun bindLong(index: Int, value: Long) = Unit
        override fun bindText(index: Int, value: String) = Unit
        override fun bindNull(index: Int) = Unit
        override fun getBlob(index: Int): ByteArray = ByteArray(0)
        override fun getDouble(index: Int): Double = 0.0
        override fun getLong(index: Int): Long = 0L
        override fun getText(index: Int): String = ""
        override fun isNull(index: Int): Boolean = true
        override fun getColumnCount(): Int = 0
        override fun getColumnName(index: Int): String = ""
        override fun getColumnType(index: Int): Int = 0
        override fun step(): Boolean {
            stepped = true
            return false
        }

        override fun reset() = Unit
        override fun clearBindings() = Unit
        override fun close() {
            closed = true
        }
    }

    private class RecordingConnection : SQLiteConnection {
        val executedSql = mutableListOf<String>()
        val statements = mutableListOf<RecordingStatement>()

        override fun inTransaction(): Boolean = false

        override fun prepare(sql: String): SQLiteStatement {
            executedSql += sql
            return RecordingStatement().also { statements += it }
        }

        override fun close() = Unit
    }

    @Test
    fun `MIGRATION_2_3 upgrades v2 to v3 and executes non-destructive ALTER TABLE DDL`() {
        assertEquals(2, MIGRATION_2_3.startVersion)
        assertEquals(3, MIGRATION_2_3.endVersion)

        val connection = RecordingConnection()
        MIGRATION_2_3.migrate(connection)

        assertEquals(
            listOf(
                "ALTER TABLE GamerEntity ADD COLUMN shakeCount INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE GamerEntity ADD COLUMN bombCount INTEGER NOT NULL DEFAULT 0",
            ),
            connection.executedSql
        )
        assertTrue(connection.statements.all { it.stepped && it.closed })
    }

    @Test
    fun `GamerEntity maps shakeCount and bombCount cleanly with zero defaults for legacy rows`() {
        val legacyEntity = GamerEntity(
            id = 10L,
            roundId = 2L,
            playerId = 5L,
            name = "플레이어 1",
            gameId = 1L,
            score = 7,
            go = 2,
            account = 900,
            winnerOption = "Winner",
        )
        val domain = legacyEntity.toGamer()
        assertEquals(0, domain.shakeCount)
        assertEquals(0, domain.bombCount)
        assertEquals(WinnerOption.Winner, domain.winnerOption)

        val roundTrip = Gamer(
            id = 11L,
            roundId = 3L,
            playerId = 6L,
            gameId = 1L,
            name = "플레이어 2",
            score = 5,
            go = 1,
            shakeCount = 2,
            bombCount = 1,
            winnerOption = WinnerOption.Winner,
        ).toGamerEntity().toGamer()
        assertEquals(2, roundTrip.shakeCount)
        assertEquals(1, roundTrip.bombCount)
    }

    @Test
    fun `legacy RoundTrace JSON without shakeCount or bombCount deserializes cleanly`() {
        val v2Json = """
            {
              "roundId": 1,
              "gameId": 1,
              "winnerId": 1,
              "totalAmount": 700,
              "terms": [
                {
                  "gamerId": 1,
                  "label": "P1",
                  "amount": 700,
                  "factors": {
                    "isSeller": false,
                    "baseScore": 7,
                    "goCount": 0,
                    "scorePerPoint": 100,
                    "bakCount": 0,
                    "fuckAmount": 0,
                    "ddadakAmount": 0,
                    "sellAmount": 0,
                    "receiverCount": 1
                  }
                }
              ]
            }
        """.trimIndent()

        val decoded = legacyJson.decodeFromString<RoundTrace>(v2Json)
        assertEquals(0, decoded.terms.single().factors.shakeCount)
        assertEquals(0, decoded.terms.single().factors.bombCount)
    }
}
