package com.example.hhmenu.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.ui.adapter.JobAdapter
import com.example.domain.model.Address
import com.example.domain.model.Experience
import com.example.domain.model.JobItem
import com.example.domain.model.Offer
import com.example.domain.model.ResultWrapper
import com.example.hhmenu.R
import com.example.hhmenu.databinding.FragmentSearchBinding
import com.example.hhmenu.presentation.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var jobAdapter: JobAdapter
    private lateinit var offerAdapter: OfferAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Создание адаптера
        jobAdapter = JobAdapter(
            onFavoriteClick = { jobId, isFavorite ->
                viewModel.updateFavorite(jobId, isFavorite)
            },
            onItemClick = { job ->
                handleJobItemClick(job)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = jobAdapter


        // Настройка RecyclerView для предложений
        offerAdapter = OfferAdapter(onOfferClick = { offer ->
            handleOfferClick(offer)
        })

        binding.offersRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.offersRecyclerView.adapter = offerAdapter


        // Подписка на состояние данных для предложений
        subscribeToOffersState()

        // Подписка на состояние данных вакансий
        subscribeToUiState()

        // Подписка на состояние ошибок
        subscribeToErrorState()

        // Настройка кнопки "Еще"
        setupShowMoreButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Очищаем ссылку на binding
        _binding = null
    }

    // Метод для подписки на состояние данных вакансий
    private fun subscribeToUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { result ->
                    when (result) {
                        is ResultWrapper.Loading -> {
                            // Показать индикатор загрузки
                            showLoadingIndicator(true)
                        }
                        is ResultWrapper.Success -> {
                            // Обновить список вакансий
                            val vacancy = viewModel.getCountVacancy()
                            jobAdapter.submitList(result.data)
                            updateShowMoreButtonVisibility(vacancy, vacancy > 3, viewModel.showAllVacancies)
                            updateFavoriteCount(result.data.size)
                            showLoadingIndicator(false)
                        }
                        is ResultWrapper.Error -> {
                            // Показать сообщение об ошибке
                            showError(result.exception.message ?: "Неизвестная ошибка")
                            showLoadingIndicator(false)
                        }
                    }
                }
            }
        }
    }

    // Метод для подписки на состояние данных для предложений
    private fun subscribeToOffersState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.offersState.collectLatest { result ->
                    when (result) {
                        is ResultWrapper.Success -> {
                            offerAdapter.submitList(result.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    // Метод для настройки кнопки "Еще"
    private fun setupShowMoreButton() {
        binding.btnCountVacancy.setOnClickListener {

            // Получаем общее количество вакансий
            val totalVacancies = viewModel.getCountVacancy()

            // Проверяем, чтобы вакансий было больше 3
            if (totalVacancies > 3) {
                viewModel.showAllVacancies() // Показываем все вакансии
                binding.btnCountVacancy.visibility = View.GONE
                binding.tvVacancyCount.isVisible = true
                binding.imgAccordance.isVisible = true
                binding.tvAccordance.isVisible = true
            }
        }
    }

    // Метод для обновления видимости кнопки "Еще"
    private fun updateShowMoreButtonVisibility(totalVacancies: Int, isVisible: Boolean, btnIsVisible: Boolean) {
        if (totalVacancies <= 3 && !isVisible) {
            binding.btnCountVacancy.visibility = View.GONE
        } else if (totalVacancies > 3 && isVisible && !btnIsVisible) {
            binding.btnCountVacancy.visibility = View.VISIBLE
            val remainingVacancies = totalVacancies - 3
            binding.btnCountVacancy.text = viewModel.formatShowMoreButton(remainingVacancies)
        }
        else{
            binding.btnCountVacancy.visibility = View.GONE
        }
    }

    // Метод для подписки на состояние ошибок (_errorState)
    private fun subscribeToErrorState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorState.collect { errorMessage ->
                    errorMessage?.let {
                        // Показать ошибку через Toast или Snackbar
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        viewModel.resetErrorState() // Сброс состояния ошибки после показа
                    }
                }
            }
        }
    }

    // Метод для обработки кликов на предложения
    private fun handleOfferClick(offer: Offer) {
        openOfferDetails(offer)
    }

    // Метод для открытия деталей предложения
    private fun openOfferDetails(offer: Offer) {
        // Открываем ссылку или переход на другой экран
        if (!offer.link.isNullOrBlank()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(offer.link))
            startActivity(intent)
        }
    }

    // Метод для обработки кликов на элементы списка
    private fun handleJobItemClick(job: JobItem) {
        openJobDetails(job)
    }

    // Метод для открытия детальной информации о вакансии
    private fun openJobDetails(job: JobItem) {
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        navController.navigate(R.id.plugFragment)

    }

    // Метод для показа индикатора загрузки
    private fun showLoadingIndicator(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.errorTextView.visibility = View.GONE
        binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    // Метод для показа ошибки
    private fun showError(message: String) {
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
    }

    // Метод для обновления счетчика избранных вакансий
    private fun updateFavoriteCount(count: Int) {
        binding.tvVacancyCount.text = "${formatVacancyCount(count)}"
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