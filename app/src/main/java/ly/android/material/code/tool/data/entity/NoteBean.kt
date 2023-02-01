package ly.android.material.code.tool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteBean(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo val id: Long? = null,
    @ColumnInfo var title: String? = null,
    @ColumnInfo var content: String? = null,
    @ColumnInfo var language: String? = null,
    @ColumnInfo var rank: Int? = null,
    @ColumnInfo var createDate: Long? = null,
    @ColumnInfo var classify: Int? = null
)
