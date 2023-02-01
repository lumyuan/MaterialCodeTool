package ly.android.material.code.tool.sql

import androidx.room.Database
import androidx.room.RoomDatabase
import ly.android.material.code.tool.data.entity.NoteBean
import ly.android.material.code.tool.sql.dao.NoteDao

@Database(entities = [NoteBean::class], version = 1, exportSchema = false)
abstract class NoteDataBase: RoomDatabase() {

    abstract fun noteDao(): NoteDao

}