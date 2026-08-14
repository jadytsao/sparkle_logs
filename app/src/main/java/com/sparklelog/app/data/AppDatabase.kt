package com.sparklelog.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE feelings ADD COLUMN emoji TEXT DEFAULT NULL")
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS sparkle_feelings (
                sparkleId INTEGER NOT NULL,
                feelingId INTEGER NOT NULL,
                PRIMARY KEY(sparkleId, feelingId),
                FOREIGN KEY(sparkleId) REFERENCES sparkles(id) ON DELETE CASCADE,
                FOREIGN KEY(feelingId) REFERENCES feelings(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_sparkle_feelings_feelingId ON sparkle_feelings(feelingId)")
        db.execSQL("INSERT INTO sparkle_feelings (sparkleId, feelingId) SELECT id, feelingId FROM sparkles")

        db.execSQL(
            """
            CREATE TABLE sparkles_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                text TEXT NOT NULL,
                timestampMillis INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("INSERT INTO sparkles_new (id, text, timestampMillis) SELECT id, text, timestampMillis FROM sparkles")
        db.execSQL("DROP TABLE sparkles")
        db.execSQL("ALTER TABLE sparkles_new RENAME TO sparkles")
    }
}

@Database(
    entities = [Feeling::class, Sparkle::class, SparkleFeelingCrossRef::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feelingDao(): FeelingDao
    abstract fun sparkleDao(): SparkleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sparkle_log.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { INSTANCE = it }
            }
    }
}
