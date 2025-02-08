package com.example.hhmenu.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.ui.adapter.JobAdapter
import com.example.domain.model.JobItem
import com.example.domain.model.ResultWrapper
import com.example.hhmenu.databinding.FragmentFavouriteBinding
import com.example.hhmenu.presentation.FavouriteViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouriteViewModel by viewModel()
    private lateinit var jobAdapter: JobAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Создание адаптера
        jobAdapter = JobAdapter(
            onFavoriteClick = { jobId, isFavorite ->
                viewModel.updateFavorite(jobId, isFavorite)
            },
            onItemClick = { job ->
                handleJobItemClick(job)
            }
        )
        binding.recyclerView.adapter = jobAdapter

        // Подписка на состояние данных
        subscribeToUiState()

        // Подписка на состояние ошибок
        subscribeToErrorState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Метод для подписки на состояние данных (_uiState)
    private fun subscribeToUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { result ->
                    when (result) {
                        is ResultWrapper.Loading -> {
                            showLoadingIndicator(true)
                        }
                        is ResultWrapper.Success -> {
                            if (result.data.isEmpty()) {
                                showEmptyMessage()
                            } else {
                                jobAdapter.submitList(result.data)
                                updateFavoriteCount(result.data.size)
                                showLoadingIndicator(false)
                            }
                        }
                        is ResultWrapper.Error -> {
                            showError(result.exception.message ?: "Неизвестная ошибка")
                            showLoadingIndicator(false)
                        }
                    }
                }
            }
        }
    }

    // Метод для подписки на состояние ошибок (_errorState)
    private fun subscribeToErrorState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorState.collect { errorMessage ->
                    errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        viewModel.resetErrorState()
                    }
                }
            }
        }
    }

    // Метод для обработки кликов на элементы списка
    private fun handleJobItemClick(job: JobItem) {
        openJobDetails(job)
    }

    // Метод для открытия деталей вакансии
    private fun openJobDetails(job: JobItem) {
        // Реализуйте логику открытия деталей (например, переход на другой фрагмент)
    }

    // Метод для показа индикатора загрузки
    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.errorTextView.visibility = View.GONE
        binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    // Метод для показа сообщения об ошибке
    private fun showError(message: String) {
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
    }

    // Метод для показа сообщения об отсутствии избранных вакансий
    private fun showEmptyMessage() {
        binding.errorTextView.text = "Нет избранных вакансий"
        binding.errorTextView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
    }

    // Метод для обновления счетчика избранных вакансий
    private fun updateFavoriteCount(count: Int) {
        binding.tvFavoriteCount.text = "${formatVacancyCount(count)}"
    }

    // Форматирование текста счетчика с учетом склонения
    private fun formatVacancyCount(number: Int): String {
        return when {
            number % 10 == 1 && number % 100 != 11 -> "$number вакансия"
            number % 10 in 2..4 && !(number % 100 in 12..14) -> "$number вакансии"
            else -> "$number вакансий"
        }
    }
}