package ly.android.material.code.tool.sql

import androidx.room.Database
import androidx.room.RoomDatabase
import ly.android.material.code.tool.data.entity.ClipDataBean
import ly.android.material.code.tool.sql.dao.ClipDao

@Database(entities = [ClipDataBean::class], version = 1, exportSchema = false)
abstract class ClipDataBase: RoomDatabase() {

    abstract fun clipDao(): ClipDao

}