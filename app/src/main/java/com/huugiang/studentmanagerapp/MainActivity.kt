package com.huugiang.studentmanagerapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.huugiang.studentmanagerapp.database.entity.ClassEntity
import com.huugiang.studentmanagerapp.database.entity.StudentEntity
import com.huugiang.studentmanagerapp.databinding.ActivityMainBinding
import com.huugiang.studentmanagerapp.viewmodel.StudentViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: StudentViewModel by viewModels()
    private lateinit var adapter: StudentAdapter

    private var classList: List<ClassEntity> = emptyList()
    private var editingStudent: StudentEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeData()
        setupEvents()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            onStarClick = { student -> viewModel.toggleStarred(student) },
            onEditClick = { student -> startEditStudent(student) },
            onDeleteClick = { student -> confirmDeleteStudent(student) }
        )
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = adapter
    }

    private fun observeData() {
        viewModel.allClasses.observe(this) { classes ->
            classList = classes

            // Spinner chọn lớp khi thêm / sửa sinh viên
            val inputAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classes)
            inputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerClassInput.adapter = inputAdapter

            // Spinner lọc danh sách sinh viên theo lớp
            val filterItems = mutableListOf(ClassEntity(id = 0, className = "Tất cả các lớp"))
            filterItems.addAll(classes)
            val filterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterItems)
            filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerClassFilter.adapter = filterAdapter
        }

        viewModel.students.observe(this) { studentList ->
            adapter.submitList(studentList)
        }
    }

    private fun setupEvents() {
        binding.btnSave.setOnClickListener {
            val code = binding.edtStudentCode.text.toString().trim()
            val name = binding.edtFullName.text.toString().trim()
            val gpaStr = binding.edtGpa.text.toString().trim()
            val selectedClass = binding.spinnerClassInput.selectedItem as? ClassEntity

            if (code.isEmpty() || name.isEmpty() || gpaStr.isEmpty() || selectedClass == null) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gpa = gpaStr.toDoubleOrNull()
            if (gpa == null || gpa < 0.0 || gpa > 4.0) {
                Toast.makeText(this, "Điểm GPA phải nằm trong khoảng 0.0 - 4.0!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editingStudent == null) {
                // Thêm mới sinh viên
                val newStudent = StudentEntity(
                    classId = selectedClass.id,
                    studentCode = code,
                    fullName = name,
                    gpa = gpa
                )
                viewModel.insertStudent(newStudent)
                Toast.makeText(this, "Đã thêm sinh viên $name", Toast.LENGTH_SHORT).show()
            } else {
                // Cập nhật thông tin sinh viên
                val updated = editingStudent!!.copy(
                    classId = selectedClass.id,
                    studentCode = code,
                    fullName = name,
                    gpa = gpa,
                    updatedAt = System.currentTimeMillis()
                )
                viewModel.updateStudent(updated)
                editingStudent = null
                binding.btnSave.text = "Thêm Sinh Viên"
                Toast.makeText(this, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show()
            }
            clearForm()
        }

        binding.btnAddClass.setOnClickListener {
            val input = EditText(this).apply {
                hint = "VD: 24T4 - Khoa học dữ liệu"
            }
            AlertDialog.Builder(this)
                .setTitle("Thêm Lớp Học Mới")
                .setView(input)
                .setPositiveButton("Lưu") { _, _ ->
                    val className = input.text.toString().trim()
                    if (className.isNotEmpty()) {
                        viewModel.addClass(className)
                        Toast.makeText(this, "Đã thêm lớp $className", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        binding.spinnerClassFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selected = parent?.getItemAtPosition(pos) as? ClassEntity
                viewModel.filterByClass(selected?.id ?: 0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun startEditStudent(student: StudentEntity) {
        editingStudent = student
        binding.edtStudentCode.setText(student.studentCode)
        binding.edtFullName.setText(student.fullName)
        binding.edtGpa.setText(student.gpa.toString())

        val classIndex = classList.indexOfFirst { it.id == student.classId }
        if (classIndex >= 0) {
            binding.spinnerClassInput.setSelection(classIndex)
        }
        binding.btnSave.text = "Cập Nhật Sinh Viên"
    }

    private fun confirmDeleteStudent(student: StudentEntity) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa sinh viên ${student.fullName}?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteStudent(student)
                if (editingStudent?.id == student.id) {
                    editingStudent = null
                    binding.btnSave.text = "Thêm Sinh Viên"
                    clearForm()
                }
                Toast.makeText(this, "Đã xóa ${student.fullName}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun clearForm() {
        binding.edtStudentCode.text?.clear()
        binding.edtFullName.text?.clear()
        binding.edtGpa.text?.clear()
        binding.edtStudentCode.requestFocus()
    }
}