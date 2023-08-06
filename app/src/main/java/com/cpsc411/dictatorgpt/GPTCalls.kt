package com.cpsc411.dictatorgpt

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

class GPTCalls {
    private val gpt = OpenAI(OPEN_AI_KEY)

    @OptIn(BetaOpenAI::class)
    suspend fun chat(inStr: String): String {
        Log.i("OpenAI", "Making chatGPT request")
        val dictatorStr = "Respond to this as a dictator talking to a peasant: $inStr"
        val chatRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = dictatorStr
                )
            )
        )
        val response: ChatCompletion = gpt.chatCompletion(chatRequest)
        Log.i("OpenAI", "Receiving chatGPT response")
        return response.choices[0].message?.content ?: "(null response)"
    }
}