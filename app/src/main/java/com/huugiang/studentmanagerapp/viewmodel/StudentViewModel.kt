package com.huugiang.studentmanagerapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.huugiang.studentmanagerapp.database.AppDatabase
import com.huugiang.studentmanagerapp.database.entity.ClassEntity
import com.huugiang.studentmanagerapp.database.entity.StudentEntity
import com.huugiang.studentmanagerapp.repository.StudentRepository
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository
    val allClasses: LiveData<List<ClassEntity>>
    private val selectedClassId = MutableLiveData(0) // 0 = Hiển thị tất cả lớp
    val students: LiveData<List<StudentEntity>>

    init {
        val dao = AppDatabase(application).studentDao()
        repository = StudentRepository(dao)
        allClasses = repository.allClasses

        // Tự động thêm 3 lớp mẫu khi chạy app lần đầu
        viewModelScope.launch {
            if (repository.getClassCount() == 0) {
                repository.insertClass(ClassEntity(className = "24T1 - Công nghệ thông tin"))
                repository.insertClass(ClassEntity(className = "24T2 - Kỹ thuật phần mềm"))
                repository.insertClass(ClassEntity(className = "24T3 - Công nghệ số"))
            }
        }

        students = selectedClassId.switchMap { id ->
            if (id == 0) repository.allStudents else repository.getStudentsByClass(id)
        }
    }

    fun filterByClass(classId: Int) {
        selectedClassId.value = classId
    }

    fun addClass(className: String) = viewModelScope.launch {
        repository.insertClass(ClassEntity(className = className))
    }

    fun insertStudent(student: StudentEntity) = viewModelScope.launch {
        repository.insertStudent(student)
    }

    fun updateStudent(student: StudentEntity) = viewModelScope.launch {
        repository.updateStudent(student)
    }

    fun deleteStudent(student: StudentEntity) = viewModelScope.launch {
        repository.deleteStudent(student)
    }

    fun toggleStarred(student: StudentEntity) = viewModelScope.launch {
        repository.toggleStarred(student.id, !student.isStarred)
    }
}