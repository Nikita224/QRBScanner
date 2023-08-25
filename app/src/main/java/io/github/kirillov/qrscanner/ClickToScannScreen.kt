package io.github.kirillov.qrscanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kirillov.qrscanner.ui.theme.QRScannerTheme

@Composable
fun ClickToScanScreen(onScanClick: (scanCount: Int) -> Unit = {}) {
    Column {
        for (i in 1..3) {
            ScanQRCard(i, onScanClick)
        }
    }
}

@Composable
fun ScanQRCardStart(scanCount: Int, onScanClick: (scanCount: Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onScanClick(scanCount)
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.padding(16.dp),
                imageVector = Icons.Rounded.QrCodeScanner,
                contentDescription = "QR Code Icon"
            )
            Text(
                text = "Сканировать штрих код",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(top = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun ScanQRaCardPreview() {
    QRScannerTheme {
        ScanQRCard(1) {}
    }
}