package com.huugiang.studentmanagerapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_table")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "class_id")
    val classId: Int,

    @ColumnInfo(name = "student_code")
    val studentCode: String,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "gpa")
    val gpa: Double,

    @ColumnInfo(name = "is_starred")
    val isStarred: Boolean = false,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)