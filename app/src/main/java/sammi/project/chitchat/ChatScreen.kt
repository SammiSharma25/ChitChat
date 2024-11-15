package sammi.project.chitchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, channelId: String) {
    val channelName =""
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = channelName, color = BlueMain) },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val viewModel: ChatViewModel = hiltViewModel()
                LaunchedEffect(key1 = true) {
                    viewModel.listenForMessage(channelId)
                }
                val messages = viewModel.message.collectAsState()
                ChatMessages(
                    messages = messages.value,
                    onSendMessage = { message ->
                        viewModel.sendMessage(channelId, message)
                    }
                )
            }
        }
    )
}


@Composable
fun ChatMessages(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    ){
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val msg = remember {
        mutableStateOf("")
    }
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn {
            items(messages){message ->
                ChatBubble(message = message)
            }
        }
        Row (modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(8.dp)
            .background(InputBackground, shape = RoundedCornerShape(16.dp)),
            verticalAlignment = Alignment.Bottom) {
            TextField(value = msg.value, onValueChange = {msg.value = it},
                modifier = Modifier.weight(1f),
                placeholder = {Text(text = "Type a message")},
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        hideKeyboardController?.hide()
                    }
                )
            )
            IconButton(onClick = {onSendMessage(msg.value)
                msg.value = ""}) {
               Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = BlueMain)
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val bubbleColor = if(isCurrentUser){
        OutgoingBubble
    }else{
      IncomingBubble
    }
    val textColor = if (isCurrentUser) LightText else DarkText
    Row (modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.Start else Arrangement.End
        ){
            Box(modifier = Modifier
                .padding(8.dp)
                .background(color = bubbleColor, shape = RoundedCornerShape(16.dp))){
                Column(modifier = Modifier.padding(8.dp)) {
                    if (!isCurrentUser) {
                        Text(
                            text = message.senderName,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                Text(text = message.message,
                    color = textColor,
                    modifier = Modifier.padding(8.dp))
            }
        }
}}
