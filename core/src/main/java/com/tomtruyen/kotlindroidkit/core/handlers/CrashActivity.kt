package com.tomtruyen.kotlindroidkit.core.handlers

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tomtruyen.kotlindroidkit.core.R

class CrashActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CrashScreen(intent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashScreen(intent: Intent) {
    var errorDialogVisible by remember {
        mutableStateOf(false)
    }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_android_error),
                contentDescription = null,
                tint = colorResource(id = R.color.android_green),
                modifier = Modifier.fillMaxWidth(0.5f)
            )

            Text(
                text = stringResource(id = R.string.crash_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(
                    top = 48.dp,
                    bottom = 16.dp
                )
            )

            Button(
                onClick = {
                    errorDialogVisible = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.android_green)
                )
            ) {
                Text(text = stringResource(id = R.string.crash_button))
            }
        }

        if(errorDialogVisible) {
            AlertDialog(
                modifier = Modifier.fillMaxSize(),
                onDismissRequest = { errorDialogVisible = false },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(GlobalExceptionHandler.getThrowableFromIntent(intent)?.stackTraceToString() ?: "Unknown error")
                }
            }
        }
    }
}

@Preview
@Composable
fun CrashScreenPreview() {
    CrashScreen(Intent())
}