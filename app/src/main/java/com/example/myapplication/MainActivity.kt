package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.network.GroqApiService
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                ChatBotUI()
            }
        }
    }
}



@Composable
fun ChatBotUI() {
    val userInput = remember { mutableStateOf(TextFieldValue("")) }
    val chatMessages = remember { mutableStateListOf<String>("What can I help you with?") }
    val coroutineScope = rememberCoroutineScope()

    // Lazy list state for scrolling
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Chat messages inside LazyColumn
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(chatMessages) { message ->
                Text(text = message, modifier = Modifier.padding(vertical = 4.dp))
            }
        }

        // Automatically scroll to the bottom when a new message is added
        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }

        // User input box
        BasicTextField(
            value = userInput.value,
            onValueChange = { userInput.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(56.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    if (userInput.value.text.isEmpty()) {
                        Text(text = "Type your message...", color = androidx.compose.ui.graphics.Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        // Send button
        Button(
            onClick = {
                val message = userInput.value.text
                if (message.isNotEmpty()) {
                    chatMessages.add("You: $message")
                    userInput.value = TextFieldValue("")
                    coroutineScope.launch {
                        val response = GroqApiService.getResponse(message)
                        if (response != null) {
                            chatMessages.add("Bot: $response")
                        } else {
                            chatMessages.add("Bot: Failed to get response.")
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Send")
        }
    }
}
