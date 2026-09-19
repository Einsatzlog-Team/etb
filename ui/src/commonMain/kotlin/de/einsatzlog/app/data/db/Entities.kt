package de.einsatzlog.app.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import de.einsatzlog.app.domain.MessageType

/** An incident / operation. `endedAtEpochMs != null` means closed → read-only. */
@Entity(tableName = "einsatz")
data class EinsatzEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val startedAtEpochMs: Long,
    val endedAtEpochMs: Long?,
    val createdAtEpochMs: Long,
)

@Entity(
    tableName = "log_entry",
    foreignKeys = [
        ForeignKey(
            entity = EinsatzEntity::class,
            parentColumns = ["id"],
            childColumns = ["einsatzId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("einsatzId")],
)
data class LogEntryEntity(
    @PrimaryKey val id: String,
    val einsatzId: String,
    /** The moment the message arrived — auto-set, corrections only via backdate (spec 003). */
    val timestampEpochMs: Long,
    val type: MessageType,
    /** "Von" — sender call sign. */
    val source: String?,
    /** "Zu" — receiver call sign. */
    val target: String?,
    val message: String,
    val createdAtEpochMs: Long,
)

/** Deployed vehicle (logbook rail, UI in v2 — schema prepared from v1, spec 001). */
@Entity(
    tableName = "vehicle",
    foreignKeys = [
        ForeignKey(
            entity = EinsatzEntity::class,
            parentColumns = ["id"],
            childColumns = ["einsatzId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("einsatzId")],
)
data class VehicleEntity(
    @PrimaryKey val id: String,
    val einsatzId: String,
    val callSign: String,
    val addedAtEpochMs: Long,
)

/** People/resource count entry (logbook rail, UI in v2 — schema prepared from v1). */
@Entity(
    tableName = "resource_entry",
    foreignKeys = [
        ForeignKey(
            entity = EinsatzEntity::class,
            parentColumns = ["id"],
            childColumns = ["einsatzId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("einsatzId")],
)
data class ResourceEntryEntity(
    @PrimaryKey val id: String,
    val einsatzId: String,
    val label: String,
    val count: Int,
    val timestampEpochMs: Long,
)
