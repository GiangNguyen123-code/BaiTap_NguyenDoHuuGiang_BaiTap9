package com.huugiang.studentmanagerapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huugiang.studentmanagerapp.database.entity.ClassEntity
import com.huugiang.studentmanagerapp.database.entity.StudentEntity

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classEntity: ClassEntity)

    @Query("SELECT * FROM class_table ORDER BY id ASC")
    fun getAllClasses(): LiveData<List<ClassEntity>>

    @Query("SELECT COUNT(*) FROM class_table")
    suspend fun getClassCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    @Query("SELECT * FROM student_table ORDER BY updated_at DESC")
    fun getAllStudents(): LiveData<List<StudentEntity>>

    @Query("SELECT * FROM student_table WHERE class_id = :classId ORDER BY updated_at DESC")
    fun getStudentsByClass(classId: Int): LiveData<List<StudentEntity>>

    @Query("UPDATE student_table SET is_starred = :isStarred WHERE id = :studentId")
    suspend fun updateStarredStatus(studentId: Int, isStarred: Boolean)
}