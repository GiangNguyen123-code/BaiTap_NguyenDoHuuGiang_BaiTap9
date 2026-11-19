package com.huugiang.studentmanagerapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huugiang.studentmanagerapp.database.entity.StudentEntity
import com.huugiang.studentmanagerapp.databinding.ItemStudentBinding

class StudentAdapter(
    private val onStarClick: (StudentEntity) -> Unit,
    private val onEditClick: (StudentEntity) -> Unit,
    private val onDeleteClick: (StudentEntity) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var list: List<StudentEntity> = emptyList()

    fun submitList(newList: List<StudentEntity>) {
        list = newList
        notifyDataSetChanged()
    }

    inner class StudentViewHolder(val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = list[position]
        with(holder.binding) {
            tvStudentName.text = student.fullName
            tvStudentInfo.text = "MSV: ${student.studentCode}  •  GPA: ${student.gpa}"

            if (student.gpa >= 3.2) {
                tvStudentInfo.setBackgroundColor(Color.parseColor("#DCFCE7"))
                tvStudentInfo.setTextColor(Color.parseColor("#166534"))
            } else {
                tvStudentInfo.setBackgroundColor(Color.parseColor("#EEF2FF"))
                tvStudentInfo.setTextColor(Color.parseColor("#3730A3"))
            }

            btnStar.setImageResource(
                if (student.isStarred) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )

            btnStar.setOnClickListener { onStarClick(student) }
            btnEdit.setOnClickListener { onEditClick(student) }
            btnDelete.setOnClickListener { onDeleteClick(student) }
        }
    }

    override fun getItemCount(): Int = list.size
}