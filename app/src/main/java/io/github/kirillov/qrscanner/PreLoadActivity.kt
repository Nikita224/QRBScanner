package io.github.kirillov.qrscanner

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kirillov.qrscanner.ui.theme.QRScannerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.DriverManager

class PreLoadActivity : ComponentActivity() {

    var taskNames by mutableStateOf(emptyList<String>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadAssemblyTasks()
        setContent {
            QRScannerTheme {
                if (taskNames.isNotEmpty()) {
                    PreMainScreen(taskNames.toTypedArray()) { scanCount ->
                        val intent = Intent(this@PreLoadActivity, MainActivity::class.java).apply {
                            putExtra("PARAM_KEY", scanCount)
                        }
                        startActivity(intent)
                    }
                } else {
                    // Show loading or placeholder content while data is loading
                }
            }
        }
    }

    private fun loadAssemblyTasks() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val resultList = mutableListOf<String>()
                Class.forName("com.mysql.jdbc.Driver")
                val connection = DriverManager.getConnection(
                    "jdbc:mysql://31.31.198.23/u2081872_DateForAnalitics?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT",
                    "u2081872_qwe",
                    "1Filipinisbro"
                )

                val statement = connection.prepareStatement(
                    "SELECT * FROM AssemblyTasks"
                )

                val resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val taskName = resultSet.getString("Name")
                    resultList.add(taskName)
                }

                resultSet.close()
                connection.close()

                taskNames = resultList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun PreMainScreen(taskNames: Array<String>, onClick: (scanCount: String) -> Unit = {}) {
    val currentState = rememberUpdatedState(taskNames)

    Column {
        for (i in currentState.value.indices) {
            Greeting(currentState.value[i], onClick)
        }
    }
}

@Composable
fun Greeting(scanCount: String, onClick: (scanCount: String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onClick(scanCount)
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = Icons.Rounded.Add,
                contentDescription = "QR Code Icon"
            )
            Text(
                text = "Отборочное задание \n№ $scanCount",
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )
        }
    }
}