package com.example.surveyapp.ui.main.surveylist

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.data.businessmodel.QuestionDomainModel
import com.example.storage.session.SessionManagerImpl
import com.example.surveyapp.R
import com.example.surveyapp.databinding.ItemSurveyLayoutBinding
import com.example.surveyapp.extensions.onTextChanged
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SurveyListAdapter @AssistedInject constructor(@ActivityContext val context: Context,
                                                    @Assisted val questions: List<QuestionDomainModel>,
                                                    @Assisted val answerButtonClickAction: (id: String, answer: String) -> Unit) : RecyclerView.Adapter<SurveyListAdapter.ViewPagerHolder>() {

    @Inject
    lateinit var sessionManager: SessionManagerImpl

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder =
        ViewPagerHolder(
            ItemSurveyLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        val questionModel = questions[position]

        with(holder) {
            questionText.text = questionModel.question
            val answerMemory = sessionManager.retrieveAnswer(questionModel.id ?: "")
            if (!answerMemory.isNullOrEmpty()) {
                answerEditText.setText(answerMemory)
                submitAnswerButton.text = context.getText(R.string.already_submitted)
            }
            else {
                answerEditText.setText(questionModel.answer)
                submitAnswerButton.text = context.getText(R.string.submit)
            }
            answerEditText.onTextChanged {
                submitAnswerButton.isEnabled = it?.length != 0
            }

            submitAnswerButton.isEnabled = questionModel.isButtonEnabled && !answerMemory.isNullOrEmpty()

            submitAnswerButton.setOnClickListener {
                answerButtonClickAction(questionModel.id ?: "", answerEditText.text.toString())
            }
        }
    }

    override fun getItemCount(): Int = questions.size

    class ViewPagerHolder(itemSurveyLayoutBinding: ItemSurveyLayoutBinding) : RecyclerView.ViewHolder(itemSurveyLayoutBinding.root) {
        val questionText: AppCompatTextView = itemSurveyLayoutBinding.questionText
        val answerText: AppCompatTextView = itemSurveyLayoutBinding.submitedAnswerText
        val answerEditText: EditText = itemSurveyLayoutBinding.editAnswerText
        val submitAnswerButton: AppCompatButton = itemSurveyLayoutBinding.submitAnswerButton
    }

    fun updateButtonState(isEnabled: Boolean, answer: String, position: Int) {
        questions[position].isButtonEnabled = isEnabled
        questions[position].answer = answer
        notifyItemChanged(position)
    }

    @AssistedFactory
    interface SurveyListAdapterFactory {
        fun createSurveyListAdapter(
            questions: List<QuestionDomainModel>,
            submitButtonClickAction: (String, String) -> Unit
        ): SurveyListAdapter
    }
}
