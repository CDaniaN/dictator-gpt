package com.cpsc411.dictatorgpt.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cpsc411.dictatorgpt.GPTCalls
import com.cpsc411.dictatorgpt.databinding.FragmentChatBinding
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chatViewModel =
            ViewModelProvider(this)[ChatViewModel::class.java]

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textChat
        chatViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Close keyboard and send prompt to GPT after pressing the enter button
        binding.chatbox.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                val response = binding.textChat

                if (binding.chatbox.text.isNotEmpty()) {
                    val loading = "Dictator is thinking..."
                    response.text = loading
                    val gpt = GPTCalls()

                    lifecycleScope.launch {
                        val statement = "Dictator says...\n\n${gpt.chat(binding.chatbox.text.toString())}"
                        response.text = statement
                    }
                }
            }
            false
        }

        binding.chatbox.setOnFocusChangeListener { _, focus ->
            if(!focus) {
                val input = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                input.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}