package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private var incomingData by mutableStateOf<String?>("Tidak ada data deep link yang diterima")
  private var parsedUri by mutableStateOf<Uri?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    handleIntent(intent)

    setContent {
      MyApplicationTheme {
        DeepLinkScreen(
          incomingData = incomingData ?: "Tidak ada data deep link yang diterima",
          parsedUri = parsedUri,
          onTestDeepLink = { url ->
            val testIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
              // Ensure intent targets this application or launches as custom scheme
              setPackage(packageName)
            }
            startActivity(testIntent)
          },
          onClearData = {
            incomingData = "Tidak ada data deep link yang diterima"
            parsedUri = null
          }
        )
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleIntent(intent)
  }

  private fun handleIntent(intent: Intent?) {
    val data = intent?.data
    if (data != null) {
      incomingData = data.toString()
      parsedUri = data
    } else {
      incomingData = "Tidak ada data deep link yang diterima"
      parsedUri = null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepLinkScreen(
  incomingData: String,
  parsedUri: Uri?,
  onTestDeepLink: (String) -> Unit = {},
  onClearData: () -> Unit = {}
) {
  val isDataReceived = incomingData != "Tidak ada data deep link yang diterima"

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Link,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Deep Link Demo",
              fontWeight = FontWeight.Bold,
              modifier = Modifier.testTag("app_title")
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Status badge card
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Aplikasi ini siap menerima Deep Link",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.testTag("status_text")
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Skema terdaftar: demoapp:// dan mywallet://",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
          }
        }
      }

      // Main Data Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDataReceived) {
            MaterialTheme.colorScheme.surfaceVariant
          } else {
            MaterialTheme.colorScheme.surface
          }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Data yang diterima:",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            if (isDataReceived) {
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary)
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "Aktif",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onPrimary,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isDataReceived) {
              MaterialTheme.colorScheme.surface
            } else {
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            },
            modifier = Modifier
              .fillMaxWidth()
              .border(
                width = 1.dp,
                color = if (isDataReceived) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
              )
          ) {
            Text(
              text = incomingData,
              fontFamily = FontFamily.Monospace,
              style = MaterialTheme.typography.bodyMedium,
              color = if (isDataReceived) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              },
              modifier = Modifier
                .padding(14.dp)
                .testTag("received_data_text")
            )
          }

          // Show parsed details if deep link received
          if (parsedUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "Detail Komponen URL:",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            UrlComponentRow(label = "Scheme", value = parsedUri.scheme ?: "-")
            UrlComponentRow(label = "Host", value = parsedUri.host ?: "-")
            if (!parsedUri.path.isNullOrEmpty()) {
              UrlComponentRow(label = "Path", value = parsedUri.path ?: "-")
            }
            if (!parsedUri.query.isNullOrEmpty()) {
              UrlComponentRow(label = "Query", value = parsedUri.query ?: "-")
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
              onClick = onClearData,
              modifier = Modifier
                .align(Alignment.End)
                .testTag("btn_clear_data")
            ) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Reset Data")
            }
          }
        }
      }

      // Quick Test Simulation Section
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Uji Coba Skema Deep Link",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Klik tombol di bawah untuk menguji penanganan intent URL secara langsung:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = { onTestDeepLink("demoapp://promo?code=DISKON50&source=banner") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_test_demoapp"),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            )
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Uji: demoapp://promo?code=DISKON50")
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = { onTestDeepLink("mywallet://pay?recipient=Budi&amount=75000") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_test_mywallet"),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.secondary
            )
          ) {
            Icon(
              imageVector = Icons.Default.AccountBalanceWallet,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Uji: mywallet://pay?recipient=Budi")
          }
        }
      }
    }
  }
}

@Composable
private fun UrlComponentRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontWeight = FontWeight.Medium
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

@Preview(showBackground = true)
@Composable
fun DeepLinkPreview() {
  MyApplicationTheme {
    DeepLinkScreen(
      incomingData = "demoapp://promo?code=DISKON50",
      parsedUri = Uri.parse("demoapp://promo?code=DISKON50")
    )
  }
}
