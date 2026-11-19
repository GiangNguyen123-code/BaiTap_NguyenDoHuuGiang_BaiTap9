package com.huugiang.studentmanagerapp.repository

import androidx.lifecycle.LiveData
import com.huugiang.studentmanagerapp.database.dao.StudentDao
import com.huugiang.studentmanagerapp.database.entity.ClassEntity
import com.huugiang.studentmanagerapp.database.entity.StudentEntity

class StudentRepository(private val studentDao: StudentDao) {

    val allClasses: LiveData<List<ClassEntity>> = studentDao.getAllClasses()
    val allStudents: LiveData<List<StudentEntity>> = studentDao.getAllStudents()

    fun getStudentsByClass(classId: Int): LiveData<List<StudentEntity>> =
        studentDao.getStudentsByClass(classId)

    suspend fun insertClass(classEntity: ClassEntity) = studentDao.insertClass(classEntity)
    suspend fun getClassCount(): Int = studentDao.getClassCount()

    suspend fun insertStudent(student: StudentEntity) = studentDao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: StudentEntity) = studentDao.deleteStudent(student)
    suspend fun toggleStarred(studentId: Int, isStarred: Boolean) =
        studentDao.updateStarredStatus(studentId, isStarred)
}