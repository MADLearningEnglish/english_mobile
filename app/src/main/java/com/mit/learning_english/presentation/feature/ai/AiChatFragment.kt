package com.mit.learning_english.presentation.feature.ai

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentAiChatBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.feature.ai.AiChatMessageAdapter.SelectionAction
import com.mit.learning_english.presentation.feature.ai.model.ChatListItem
import com.mit.learning_english.presentation.feature.ai.model.toSessionSummaryArgs
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AiChatFragment : BaseFragment<FragmentAiChatBinding, AiChatViewModel>() {

    override val viewModel: AiChatViewModel by viewModels()
    private val args: AiChatFragmentArgs by navArgs()

    private var tts: TextToSpeech? = null

    private val recordAudioLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            viewModel.startRecording(requireContext())
        } else {
            Toast.makeText(requireContext(), R.string.ai_mic_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    private val messageAdapter by lazy {
        AiChatMessageAdapter(
            onScenarioDetails = { instruction ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.ai_scenario_details_title)
                    .setMessage(instruction)
                    .setPositiveButton(R.string.ai_ok, null)
                    .show()
            },
            onSpeakAssistant = { text ->
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ai_chat_tts")
            },
            onWhyCorrection = { explanation ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.ai_why_title)
                    .setMessage(explanation ?: getString(R.string.ai_ok))
                    .setPositiveButton(R.string.ai_ok, null)
                    .show()
            },
            onTextAction = { action, selectedText ->
                onChatTextAction(action, selectedText)
            },
        )
    }

    override fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAiChatBinding {
        return FragmentAiChatBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        childFragmentManager.setFragmentResultListener(
            SessionSummaryDialogFragment.REQUEST_SUMMARY_CLOSED,
            viewLifecycleOwner,
        ) { _, _ ->
            findNavController().popBackStack()
        }
        binding.recyclerChat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerChat.adapter = messageAdapter
        binding.textTitle.text = args.aiRole.ifBlank { args.title }
        val levelLabel = when (args.levelName.uppercase()) {
            "FREE" -> getString(R.string.ai_difficulty_free).uppercase(Locale.US)
            else -> args.levelName.uppercase(Locale.US)
        }
        val goal = args.goalType.takeIf { it.isNotBlank() }?.replace('_', ' ') ?: "GENERAL"
        val mode = args.coachingMode.takeIf { it.isNotBlank() }?.replace('_', ' ') ?: "COACH"
        binding.textLevel.text = getString(R.string.ai_level_line_format, levelLabel) + " • " + goal + " • " + mode
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnFinish.setOnClickListener { confirmEndSession() }
        binding.btnSettings.setOnClickListener {
            Toast.makeText(requireContext(), R.string.ai_settings, Toast.LENGTH_SHORT).show()
        }
        binding.btnSend.setOnClickListener {
            val t = binding.inputMessage.text?.toString().orEmpty()
            binding.inputMessage.text?.clear()
            viewModel.sendTextMessage(t)
        }
        binding.btnMic.setOnClickListener { onMicClicked() }
        binding.btnStopRecord.setOnClickListener { viewModel.cancelRecording() }
        binding.btnSendRecording.setOnClickListener { viewModel.stopRecordingAndSend() }

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    override fun bindView() {
        viewModel.initSession(args.sessionId, args.instruction)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectState(viewModel.uiState) { state ->
            messageAdapter.submitList(state.items) {
                if (state.items.isNotEmpty()) {
                    binding.recyclerChat.smoothScrollToPosition(state.items.size - 1)
                }
            }
            val hasConversation = state.items.any { it is ChatListItem.Assistant || it is ChatListItem.User }
            binding.emptyChatHint.isVisible = state.transcriptLoaded && !hasConversation
            val tip = state.bottomHint?.takeIf { it.isNotBlank() }
                ?: if (state.transcriptLoaded && hasConversation) {
                    getString(R.string.ai_chat_encouragement_default)
                } else {
                    null
                }
            binding.tipLine.text = tip.orEmpty()
            binding.tipLine.visibility = if (tip.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.rowTyping.visibility = if (state.isRecording) View.GONE else View.VISIBLE
            binding.rowRecording.visibility = if (state.isRecording) View.VISIBLE else View.GONE
            val sec = state.recordElapsedSec
            binding.textTimer.text = String.format(Locale.US, "%d:%02d", sec / 60, sec % 60)
        }
        collectEvent(viewModel.event) { ev ->
            when (ev) {
                is AiChatEvent.ShowSummary -> {
                    SessionSummaryDialogFragment.newInstance(ev.summary.toSessionSummaryArgs())
                        .show(childFragmentManager, "session_summary")
                }
                is AiChatEvent.ShowToast -> {
                    Toast.makeText(requireContext(), ev.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onChatTextAction(action: SelectionAction, selectedText: String) {
        val text = selectedText.trim()
        if (text.isBlank()) return
        when (action) {
            SelectionAction.TRANSLATE -> {
                val url = "https://translate.google.com/?sl=en&tl=vi&text=${Uri.encode(text)}&op=translate"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                runCatching { startActivity(intent) }
                    .onFailure {
                        Toast.makeText(requireContext(), R.string.ai_action_translate, Toast.LENGTH_SHORT).show()
                    }
            }
            SelectionAction.LISTEN -> {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ai_chat_selected_tts")
            }
            SelectionAction.COPY -> {
                val clipboard = requireContext().getSystemService(ClipboardManager::class.java)
                clipboard?.setPrimaryClip(ClipData.newPlainText("ai_chat_selection", text))
                Toast.makeText(requireContext(), R.string.ai_text_copied, Toast.LENGTH_SHORT).show()
            }
            SelectionAction.SAVE -> {
                viewModel.saveSelectedTextToVocabulary(text)
            }
        }
    }

    private fun onMicClicked() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.startRecording(requireContext())
            }
            else -> recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun confirmEndSession() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.ai_confirm_end_title)
            .setMessage(R.string.ai_confirm_end_message)
            .setNegativeButton(R.string.ai_cancel, null)
            .setPositiveButton(R.string.ai_end) { _, _ -> viewModel.endSession() }
            .show()
    }

    override fun onDestroyView() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroyView()
    }
}
