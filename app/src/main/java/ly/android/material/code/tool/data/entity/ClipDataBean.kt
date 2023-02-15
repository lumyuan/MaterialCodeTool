package ly.android.material.code.tool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ClipDataBean(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo val text: String?,
    @ColumnInfo var date: Long?
)
