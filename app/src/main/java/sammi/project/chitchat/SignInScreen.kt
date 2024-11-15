package sammi.project.chitchat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignInScreen(navController: NavHostController) {
    val viewModel: SignInViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = uiState.value) {
        when(uiState.value){
            is SignInState.Success -> {
                navController.navigate("home")
            }
            is SignInState.Error -> {
                Toast.makeText(context,"Sign In failed", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
    Scaffold (modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Image(painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White))
            Text(text ="ChitChat",
                fontSize = 50.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF0b70b8))
            Spacer(modifier = Modifier.padding(4.dp))
            Text(text ="A MESSAGING APP",
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal,
                color = Color.Black)
            Spacer(modifier = Modifier.padding(10.dp))
            OutlinedTextField(value = email,
                onValueChange = {email = it},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                label = {Text(text = "Email")})
            OutlinedTextField(value = password,
                onValueChange = {password = it},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                label = {Text(text = "Password")},
                visualTransformation = PasswordVisualTransformation())
            Spacer(modifier = Modifier.padding(10.dp))
            if(uiState.value == SignInState.Loading){
                CircularProgressIndicator()
            }else {
                Button(
                    onClick = { viewModel.signIn(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp),
                    enabled = email.isNotEmpty() && password.isNotEmpty() &&
                            (uiState.value == SignInState.Nothing || uiState.value == SignInState.Error)
                ) {
                    Text("Sign In")
                }
                TextButton(onClick = { navController.navigate("signup") }) {
                    Text(
                        text = "Don't have an account? Sign Up",
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun PreviewSignInScreen(){
    SignInScreen(navController = rememberNavController())
}