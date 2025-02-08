package com.example.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Button
import com.example.domain.model.JobItem
import com.example.hhmenu.R
import java.text.SimpleDateFormat
import java.util.Locale


class JobAdapter(
    private val onFavoriteClick: (String, Boolean) -> Unit, // Коллбэк для кнопки "Избранное"
    private val onItemClick: (JobItem) -> Unit // Коллбэк для клика по элементу списка
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    private var jobList = emptyList<JobItem>()

    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tvLookingNumber: TextView = itemView.findViewById(R.id.tvLookingNumber)
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvTown: TextView = itemView.findViewById(R.id.tvTown)
        val tvCompany: TextView = itemView.findViewById(R.id.tvCompany)
        val tvExperience: TextView = itemView.findViewById(R.id.tvExperience)
        val tvPublishedDate: TextView = itemView.findViewById(R.id.tvPublishedDate)
        val btnNext: android.widget.Button = itemView.findViewById(R.id.btnRespond)

        lateinit var currentJob: JobItem

        init {
            // Установка слушателя кликов на корневой элемент
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            // Вызов коллбэка при клике на элемент
            onItemClick(currentJob)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vacancy_view, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.currentJob = job // Сохраняем текущую вакансию

        // 1. Количество просмотров
        job.lookingNumber?.let { number ->
            holder.tvLookingNumber.text = "Сейчас просматривает ${formatLookingNumber(number)}"
            holder.tvLookingNumber.visibility = View.VISIBLE
        } ?: run {
            holder.tvLookingNumber.visibility = View.GONE
        }

        // 2. Иконка "Избранное" (кликабельная)
        holder.ivFavorite.setImageResource(if (job.isFavorite) R.drawable.ic_fav_filled else R.drawable.ic_fav_empty)
        holder.ivFavorite.setOnClickListener {
            onFavoriteClick(job.id, job.isFavorite)
        }

        // 3. Заголовок
        holder.tvTitle.text = job.title

        // 4. Город
        holder.tvTown.text = job.address.town

        // 5. Название компании
        holder.tvCompany.text = job.company

        // 6. Опыт работы
        holder.tvExperience.text = job.experience.previewText

        // 7. Дата публикации
        holder.tvPublishedDate.text = formatDate(job.publishedDate)

        holder.btnNext.setOnClickListener {  }
    }

    override fun getItemCount(): Int = jobList.size

    // Метод для обновления списка данных
    fun submitList(newList: List<JobItem>) {
        jobList = newList
        notifyDataSetChanged()
    }

    val currentList: List<JobItem>
        get() = jobList

    // Форматирование количества просмотров с учетом склонения
    private fun formatLookingNumber(number: Int): String {
        return when {
            number % 10 in 2..4 && !(number % 100 in 12..14) -> "$number человека"
            else -> "$number человек"
        }
    }

    // Форматирование даты публикации
    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM", Locale("ru"))
        val date = inputFormat.parse(dateString) ?: return "Дата не указана"

        return "Опубликовано " + outputFormat.format(date)
    }
}