package com.tomtruyen.kotlindroidkit.core.handlers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tomtruyen.kotlindroidkit.core.R

class CrashActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var errorDialogVisible by remember {
                mutableStateOf(false)
            }

            Scaffold {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.crash_title),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Button(
                        onClick = {
                            errorDialogVisible = true
                        }
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
    }
}