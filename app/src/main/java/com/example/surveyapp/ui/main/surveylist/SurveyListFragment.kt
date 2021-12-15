package com.example.surveyapp.ui.main.surveylist

import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.viewpager2.widget.ViewPager2
import com.example.data.businessmodel.QuestionDomainModel
import com.example.surveyapp.R
import com.example.surveyapp.databinding.SurveyListLayoutBinding
import com.example.surveyapp.extensions.gone
import com.example.surveyapp.extensions.visible
import com.example.surveyapp.ui.main.surveylist.SurveyListAdapter.SurveyListAdapterFactory
import com.example.surveyapp.ui.main.surveylist.state.QuestionsListState
import com.example.surveyapp.ui.main.surveylist.state.RenderState
import com.example.surveyapp.ui.main.surveylist.state.SubmitAnswerState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SurveyListFragment : Fragment() {

    @Inject
    lateinit var surveyListAdapterFactory: SurveyListAdapterFactory

    private var _binding: SurveyListLayoutBinding? = null
    val binding get() = _binding!!

    private lateinit var surveyListAdapter: SurveyListAdapter
    private var selectedId = ""
    private var selectedAnswer = ""
    var totalNumberQuestions: Int? = 0

    private val viewModel by viewModels<SurveyListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        _binding = SurveyListLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        observeViewModel()
        clickListeners()
    }

    private fun initLayout() {
        (activity as AppCompatActivity).supportActionBar?.title = "Survey"

        binding.questionsSubmitted.text = getString(R.string.questions_submitted, ZERO)
        viewModel.getSubmittedAnswers()

        changeQuestionPageListener()
    }

    private fun observeViewModel() {

        // Observe questions call
        viewModel.getQuestionsListState().observe(viewLifecycleOwner, { state ->
            // We created a new method in order to handle UI tests more easily
            renderState(state)
        })

        // Observe submit answer call
        viewModel.submitAnswerState().observe(viewLifecycleOwner, { state ->
            // We created a new method in order to handle UI tests more easily
            renderState(state)
        })

        viewModel.getStoredAnswer().observe(viewLifecycleOwner, { isStored ->
            if (isStored) {
                // We want to update the number of answered questions
                viewModel.getSubmittedAnswers()
            }
        })

        viewModel.getSubmittedQuestionsNumber().observe(viewLifecycleOwner, { number ->
            binding.questionsSubmitted.text = getString(R.string.questions_submitted, number.toString())
        })
    }

    @VisibleForTesting
    fun renderState(state: RenderState) {
        when (state) {
            // Check QuestionsList state
            is QuestionsListState.Loading -> {
                showLoader(true)
                showQuestions(false)
            }
            is QuestionsListState.Success -> {
                totalNumberQuestions = state.questionsResponse?.size
                requireActivity().invalidateOptionsMenu()

                showLoader(false)
                if (state.questionsResponse.isNullOrEmpty()) {
                    showQuestions(false)
                } else {
                    initViewPagerQuestions(state.questionsResponse)
                    showQuestions(true)
                }
            }
            is QuestionsListState.Error -> {
                showLoader(false)
                showQuestions(true)
            }

            // Check SubmitAnswer state
            is SubmitAnswerState.Loading -> {
                showLoader(true)
                showQuestions(false)
            }
            is SubmitAnswerState.Success -> {
                initializeSurveyListAdapter()

                showLoader(false)
                showResultArea(show = true, isSuccess = true)
                showQuestions(true)

                //Store the answer to memory
                viewModel.storeAnswer(selectedId, selectedAnswer)

                val position = binding.questionsViewPager.currentItem
                surveyListAdapter.updateButtonState(
                    isEnabled = false,
                    answer = selectedAnswer,
                    position = position)
            }
            is SubmitAnswerState.Error -> {
                initializeSurveyListAdapter()

                showLoader(false)
                showResultArea(show = true, isSuccess = false)
                showQuestions(true)

                val position = binding.questionsViewPager.currentItem
                surveyListAdapter.updateButtonState(
                    isEnabled = false,
                    answer = selectedAnswer,
                    position = position)
            }
        }
    }

    /**
     * This method was added only for testing purposes
     */
    private fun initializeSurveyListAdapter() {
        if (!this::surveyListAdapter.isInitialized) {
            val mockedResponseList = arrayListOf(
                QuestionDomainModel("1", "What's your favourite color?", "",true),
                QuestionDomainModel("2", "What's your favourite food?", "",true)
            )

            initViewPagerQuestions(mockedResponseList)
        }
    }


    private fun clickListeners() {
        binding.submitResultRetry.setOnClickListener {
            viewModel.submitQuestion(selectedId, selectedAnswer)
        }
    }


    private fun initViewPagerQuestions(questionsResponse: List<QuestionDomainModel>) {

        val answerButtonClickAction = { id: String, answerText: String ->
            selectedId = id
            selectedAnswer = answerText
            viewModel.submitQuestion(id, answerText)
        }

        surveyListAdapter = surveyListAdapterFactory.createSurveyListAdapter(
            questions = questionsResponse,
            submitButtonClickAction = answerButtonClickAction)

        with(binding.questionsViewPager) {
            adapter = surveyListAdapter
            offscreenPageLimit = 1
        }

        changeToolbarTitle(binding.questionsViewPager.currentItem)
    }

    private fun changeQuestionPageListener() {
        binding.questionsViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                changeToolbarTitle(position)
                requireActivity().invalidateOptionsMenu()
            }
        })
    }

    private fun changeToolbarTitle(position: Int) {
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.questions_page_number, position.plus(1), totalNumberQuestions)
    }

    private fun showLoader(show: Boolean) {
        if (show) {
            showResultArea(show = false)
            binding.animationView.playAnimation()
            binding.animationView.visible()
        } else {
            binding.animationView.cancelAnimation()
            binding.animationView.gone()
        }
    }

    private fun showQuestions(show: Boolean) {
        if (show) {
            binding.questionsViewPager.visible()
        } else {
            binding.questionsViewPager.gone()
        }
    }

    private fun showResultArea(show: Boolean, isSuccess: Boolean = false) {
        if (show) {
            if (isSuccess) {
                binding.submitResultArea.visible()
                binding.submitResultArea.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_700))
                binding.submitResultText.text = getString(R.string.success)
                binding.submitResultRetry.gone()
            }
            else {
                binding.submitResultArea.visible()
                binding.submitResultArea.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.submitResultText.text = getString(R.string.failure)
                binding.submitResultRetry.visible()
            }
        } else {
            binding.submitResultArea.gone()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_items, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentPosition = binding.questionsViewPager.currentItem

        when (item.itemId) {
            R.id.previous_button -> {
                binding.questionsViewPager.setCurrentItem(currentPosition.minus(1), true)
            }
            R.id.next_button -> {
                binding.questionsViewPager.setCurrentItem(currentPosition.plus(1), true)
            }
        }

        if (binding.submitResultArea.isVisible) {
            showResultArea(false)
        }
        requireActivity().invalidateOptionsMenu()
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.previous_button).isEnabled = binding.questionsViewPager.currentItem > 0
        menu.findItem(R.id.next_button).isEnabled = binding.questionsViewPager.currentItem.plus(1) < totalNumberQuestions ?: 0
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ZERO = "0"
    }
}