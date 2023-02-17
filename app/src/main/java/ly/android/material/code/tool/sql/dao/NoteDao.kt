package ly.android.material.code.tool.sql.dao

import androidx.room.*
import ly.android.material.code.tool.data.entity.NoteBean

@Dao
interface NoteDao {

    @Insert
    fun addNote(noteBean: NoteBean)

    @Insert
    fun addNotes(list: List<NoteBean>)

    @Delete
    fun deleteNote(noteBean: NoteBean)

    @Query("delete from NoteBean")
    fun deleteAll()

    @Update
    fun updateNote(noteBean: NoteBean)

    @Query("select * from NoteBean Order by rank desc")
    fun queryAllNote(): List<NoteBean>?

    @Query("select * from NoteBean where (content like '%' || :regex || '%') or (title like '%' || :regex || '%') Order by rank desc")
    fun queryNotes(regex: String): List<NoteBean>?

    @Query("select * from NoteBean where id = :id")
    fun queryNote(id: Long): NoteBean?

    @Query("select count(*) from NoteBean")
    fun length(): Int

}