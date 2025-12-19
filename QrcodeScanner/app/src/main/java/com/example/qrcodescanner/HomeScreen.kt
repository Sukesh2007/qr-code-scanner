package com.example.qrcodescanner

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData

@Composable
fun HomeScreen(
    pd: PaddingValues,
    menuLiveData: MutableLiveData<List<Scanner>>,
    database1: ScannerInventory,
    decrease: (id: Int) -> Unit
) {
     val menu by database1.getAllItems().observeAsState()
    Scaffold(
        topBar = {
            TopBar()
        },
        modifier = Modifier.padding(pd)
    ){it->
        LazyColumn(modifier = Modifier.padding(it).fillMaxSize()){
            itemsIndexed (menu ?: listOf(Scanner(-1 , ".", "0","0"))){index , item ->
                if(item.id != -1) {
                    HomePreview(item.id, item.content, item.date, item.time) {
                        decrease(item.id)
                    }
                }
            }
        }
    }

}

@Composable
fun TopBar(){
    Row(modifier = Modifier.fillMaxWidth().background(Color.Black).padding(vertical = 20.dp)){
        Text(
            text = "QR Code Scanner",
            color =Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 80.dp)
        )
    }
}


@Composable
fun HomePreview(id: Int, content: String, date: String, time: String, longClick: ()->Unit){
    var lineCount by remember { mutableIntStateOf(0) }
    val context  =LocalContext.current
    var isTruncated by remember { mutableStateOf(true) }
    var lineHeight by remember {mutableStateOf(140.dp)}
    val density = LocalDensity.current
    Card(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth().combinedClickable(
        onClick = {
            Toast.makeText(context, "Long click to delete the item", Toast.LENGTH_SHORT).show()
        }, onLongClick = {longClick()}
    ),
        colors = CardDefaults.cardColors(Color(0xF0C1C4FF)),
        border = BorderStroke(1.5.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)){
        Column(modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Scan $id",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 22.sp
            )
            Text(
                text = content ,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                onTextLayout = {it->
                    lineCount = it.lineCount
                    isTruncated = it.hasVisualOverflow
                    lineHeight = with(density){
                        it.size.height.toFloat().toDp()
                    }
                    Log.d("lines", "$id: $lineCount lines")
                },
                overflow = TextOverflow.Clip,
                modifier = if(isTruncated) Modifier.fillMaxWidth().height(140.dp) else Modifier.fillMaxWidth().wrapContentHeight()
            )
            if(lineHeight > 135.dp) {
                TextButton(onClick = {
                    isTruncated = !isTruncated
                }, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isTruncated) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                            contentDescription = "drop down"
                        )
                        Text(
                            text = if (isTruncated) "Read More" else "Read Less",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            Text(
                text = "$date\n$time",
                color = Color.Gray,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
}

