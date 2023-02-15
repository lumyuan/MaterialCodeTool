package ly.android.material.code.tool.sql.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ly.android.material.code.tool.data.entity.ClipDataBean

@Dao
interface ClipDao {

    @Insert
    fun addData(clipDataBean: ClipDataBean)

    @Delete
    fun deleteClip(clipDataBean: ClipDataBean)

    @Query("delete from ClipDataBean")
    fun deleteAll()

    @Query("select * from ClipDataBean")
    fun queryClipDataList(): List<ClipDataBean>

    @Query("select count(*) from ClipDataBean")
    fun length(): Int

    @Query("select count(*) from ClipDataBean where text = :content")
    fun exits(content: String): Int

}