@file:OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)

package io.github.kirillov.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import io.github.kirillov.qrscanner.domain.BarCodeContract
import io.github.kirillov.qrscanner.ui.theme.QRScannerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.DriverManager
import java.util.logging.Logger

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val passedParameter = intent.getStringExtra(EXTRA_PARAMETER)
        setContent {

            val context = LocalContext.current
            val bottomSheetScaffoldState =
                rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

            val coroutineScope = rememberCoroutineScope()
            val barcodeLauncher = rememberLauncherForActivityResult(
                BarCodeContract()
            ) { result ->
                result.qrCodeContent?.let { scannedContent ->
                    //TODO: Handle Content
                    insertScannedContentIntoDatabase(scannedContent)

                }
            }
            val cameraPermissionState = rememberPermissionState(
                permission = Manifest.permission.CAMERA,
                onPermissionResult = { isGranted ->
                    if (isGranted) {
                        barcodeLauncher.launch(Unit)
                    } else {
                        coroutineScope.launch {
                            bottomSheetScaffoldState.show()
                        }
                    }
                }
            )

            QRScannerTheme {
                ModalBottomSheetLayout(
                    sheetState = bottomSheetScaffoldState,
                    sheetShape = RoundedCornerShape(32.dp, 32.dp),
                    sheetElevation = 8.dp,
                    sheetContent = {
                        PermissionBottomSheet(
                            title = "Camera Permission Required",
                            subtitle = "Permission for camera required for scanning Barcode",
                            buttonText = "Allow"
                        ) {
                            cameraPermissionState.launchPermissionRequest()
                            coroutineScope.launch {
                                bottomSheetScaffoldState.hide()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainScreen(
                        //onHistoryClick = {
//
                        // },
                        onScanClick = {
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                ) -> {
                                    barcodeLauncher.launch(Unit)
                                }
                                else -> {
                                    cameraPermissionState.launchPermissionRequest()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_PARAMETER = "extra_parameter"
    }
    private fun insertScannedContentIntoDatabase(scannedContent: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Class.forName("com.mysql.jdbc.Driver")
                val connection = DriverManager.getConnection(
                    "jdbc:mysql://31.31.198.23/u2081872_DateForAnalitics?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT",
                    "u2081872_qwe",
                    "1Filipinisbro"
                )

                val statement = connection.prepareStatement(
                    "INSERT INTO qwe (Name, Date, Time) VALUES (?, NOW(), CURTIME())"
                )
                statement.setString(1, scannedContent)
                statement.executeUpdate()

                launch(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Добавлен скан: $scannedContent",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
                //Log.e("DatabaseError", "Failed to insert into the database", e)

                launch(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Не удалось сохранить скан!\nПроверьте подключение к сети и попробуйте снова",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
