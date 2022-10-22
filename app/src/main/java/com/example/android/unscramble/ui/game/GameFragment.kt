/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class GameFragment : Fragment() {
    /**
     * Kotlin'deki property delegasyonu, getter-setter sorumluluğunu farklı bir sınıfa devretmenize yardımcı olur.
     * Bu sınıf (delege sınıfı olarak adlandırılır), propertynin getter ve setter işlevlerini sağlar
     * ve değişikliklerini handle eder..
     * Bir delegated property, by keywordu ve bir delegate sınıfı instance i kullanılarak tanımlanır:
     * */
    private val viewModel: GameViewModel by viewModels()
    //viewModel nesnesinin sorumlulugunu viewModels() classina devrettik.
    //bu nesneye eristigimizde viewModels sinifi configuration changesleri handle edecek

    private lateinit var binding: GameFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding) {
            gameViewModel = viewModel
            maxNoOfWords = MAX_NO_OF_WORDS
            lifecycleOwner = viewLifecycleOwner
            submit.setOnClickListener { onSubmitWord() }
            skip.setOnClickListener { onSkipWord() }
        }


    }

    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()
        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)

            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }


    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)

        } else {
            showFinalScoreDialog()
        }
    }

    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)

    }


    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
    * Sets and resets the text field error status.
    */
    private fun setErrorTextField(error: Boolean) {
        with(binding) {
            if (error) {
                textField.isErrorEnabled = true
                textField.error = getString(R.string.try_again)
            } else {
                textField.isErrorEnabled = false
                textInputEditText.text = null
            }
        }

    }


    //show dialog for final screen
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }
}
