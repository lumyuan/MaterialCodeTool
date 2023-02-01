package ly.android.material.code.tool.common

import androidx.room.Room
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.sql.NoteDataBase

object DataBase {

    val noteDataBase by lazy {
        Room.databaseBuilder(
            MaterialCodeToolApplication.application, NoteDataBase::class.java, "note_table"
        ).addMigrations()
            .allowMainThreadQueries()
            .build()
    }

}