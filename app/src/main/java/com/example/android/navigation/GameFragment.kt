/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    data class Question(
            val text: String,
            val answers: List<String>)

    // The first answer is the correct one.  We randomize the answers before showing the text.
    // All questions must have four answers.  We'd want these to contain references to string
    // resources so we could internationalize. (or better yet, not define the questions in code...)
    private val questions: MutableList<Question> = mutableListOf(
            Question(text = "Rickets is a deficiency of which vitamin?",
                    answers = listOf("Vitamin D", "Vitamin C", "Vitamin B", "Vitamin A")),
            Question(text = "Which country was involved in the First World War?",
                    answers = listOf("all of these", "Belgium", "Italy", "UK")),
            Question(text = "Which country was not in the Triple Entente in 1914?",
                    answers = listOf("Italy", "None of these", "Great Britain", "France")),
            Question(text = "How many countries were involved in World War 1?",
                    answers = listOf("32", "18", "25", "30")),
            Question(text = "Which country has the most deaths in World War 1?",
                    answers = listOf("Russia", "Australia", "Japan", "Germany")),
            Question(text = "How many people died in World War 1?",
                    answers = listOf("16 M", "30 M", "60 M", "50 M")),
            Question(text = " In the 1930s there was a civil war in ______",
                    answers = listOf("Spain ", "Italy", "Germany", "Poland")),
            Question(text = "World War II began when Germany invaded?",
                    answers = listOf("Poland","Spain ", "Italy", "Germany" )),
            Question(text = " Klaus von Stauffenberg was ______",
                    answers = listOf("tried to kill Hitler ", " Hitler's most important general", "Germany's last chancellor", "organized the German submarine attacks\n" +
                            "in the Atlantic")),
            Question(text = "Germany' secret police was commanded by _____",
                    answers = listOf("Heinrich Himmler", "Adolf Hitler", "Joseph Goebbels", "Rudolf Hess")),
            Question(text = "Hitler gave up his plan of invading Great Britain\n" +
                    "because?",
                    answers = listOf("he could not defeat the British air force", "Britain was too far away from Germany", "the Germans did not have enough pilots", "the British were supported by the Americans")),
            Question(text = "After the end of the war Germany was divided into ____",
                    answers = listOf("four zones", "three countries", "two parts", "five districts")),
            Question(text = "When Hitler was a child he dreamed of becoming a _______",
                    answers = listOf("painter", "dictator", "writer", "soldier")),
            Question(text = "During the Holocaust the Nazis killed millions of what?",
                    answers = listOf("Jews", "Muslims", "Christians", "gypsies")),
            Question(text = "Currently how many members are in the IBRD?",
                    answers = listOf("189", "206", "198", "193")),
            Question(text = "Who is the current President of the World Bank Group?",
                    answers = listOf("David Malpass", " Robert Zoellick", "Christine Lagarde", "Jim Yong Kim")),
            Question(text = "Which of the following institutions is not part of the World Bank community?",
                    answers = listOf("WTO", " IBRD", "IDA", "IFC")),
            Question(text = "What is Coronavirus?",
                    answers = listOf("Both A and B are correct", "It belongs to the family of Nidovirus", "It is a large family of viruses.", "None of these"))

//            Question(text = "What is Android Jetpack?",
//                    answers = listOf("all of these", "tools", "documentation", "libraries")),
//            Question(text = "Base class for Layout?",
//                    answers = listOf("ViewGroup", "ViewSet", "ViewCollection", "ViewRoot")),
//            Question(text = "Layout for complex Screens?",
//                    answers = listOf("ConstraintLayout", "GridLayout", "LinearLayout", "FrameLayout")),
//            Question(text = "Pushing structured data into a Layout?",
//                    answers = listOf("Data Binding", "Data Pushing", "Set Text", "OnClick")),
//            Question(text = "Inflate layout in fragments?",
//                    answers = listOf("onCreateView", "onViewCreated", "onCreateLayout", "onInflateLayout")),
//            Question(text = "Build system for Android?",
//                    answers = listOf("Gradle", "Graddle", "Grodle", "Groyle")),
//            Question(text = "Android vector format?",
//                    answers = listOf("VectorDrawable", "AndroidVectorDrawable", "DrawableVector", "AndroidVector")),
//            Question(text = "Android Navigation Component?",
//                    answers = listOf("NavController", "NavCentral", "NavMaster", "NavSwitcher")),
//            Question(text = "Registers app with launcher?",
//                    answers = listOf("intent-filter", "app-registry", "launcher-registry", "app-launcher")),
//            Question(text = "Mark a layout for Data Binding?",
//                    answers = listOf("<layout>", "<binding>", "<data-binding>", "<dbinding>"))
    )

    lateinit var currentQuestion: Question
    lateinit var answers: MutableList<String>
    private var questionIndex = 0
    private val numQuestions = ((questions.size + 1) / 2).coerceAtMost(3)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentGameBinding>(
                inflater, R.layout.fragment_game, container, false)

        // Shuffles the questions and sets the question index to the first question.
        randomizeQuestions()

        // Bind this fragment class to the layout
        binding.game = this

        // Set the onClickListener for the submitButton
        binding.submitButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            val checkedId = binding.questionRadioGroup.checkedRadioButtonId
            // Do nothing if nothing is checked (id == -1)
            if (-1 != checkedId) {
                var answerIndex = 0
                when (checkedId) {
                    R.id.secondAnswerRadioButton -> answerIndex = 1
                    R.id.thirdAnswerRadioButton -> answerIndex = 2
                    R.id.fourthAnswerRadioButton -> answerIndex = 3
                }
                // The first answer in the original question is always the correct one, so if our
                // answer matches, we have the correct answer.
                if (answers[answerIndex] == currentQuestion.answers[0]) {
                    questionIndex++
                    // Advance to the next question
                    if (questionIndex < numQuestions) {
                        currentQuestion = questions[questionIndex]
                        setQuestion()
                        binding.invalidateAll()
                    } else {
                        // We've won!  Navigate to the gameWonFragment.
//                        view.findNavController()
//                                .navigate(R.id.action_gameFragment_to_gameWonFragment)
                        view.findNavController()
                                .navigate(GameFragmentDirections.
                                actionGameFragmentToGameWonFragment(numQuestions, questionIndex))

                    }
                } else {
                    // Game over! A wrong answer sends us to the gameOverFragment.
//                    view.findNavController().
//                    navigate(R.id.action_gameFragment_to_gameOverFragment)
                    view.findNavController()
                            .navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment())
                }
            }
        }
        return binding.root
    }

    // randomize the questions and set the first question
    private fun randomizeQuestions() {
        questions.shuffle()
        questionIndex = 0
        setQuestion()
    }

    // Sets the question and randomizes the answers.  This only changes the data, not the UI.
    // Calling invalidateAll on the FragmentGameBinding updates the data.
    private fun setQuestion() {
        currentQuestion = questions[questionIndex]
        // randomize the answers into a copy of the array
        answers = currentQuestion.answers.toMutableList()
        // and shuffle them
        answers.shuffle()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_android_trivia_question, questionIndex + 1, numQuestions)
    }
}
