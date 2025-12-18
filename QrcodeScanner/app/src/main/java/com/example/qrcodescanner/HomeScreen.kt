package com.example.qrcodescanner

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    LazyColumn(modifier = Modifier.padding(pd).fillMaxSize()){
        itemsIndexed (menu ?: listOf(Scanner(-1 , ".", "0","0"))){index , item ->
            if(item.id != -1) {
                HomePreview(item.id, item.content, item.date, item.time) {
                    decrease(item.id)
                }
            }
        }
    }
}


@Composable
fun HomePreview(id: Int, content: String, date: String, time: String, longClick: ()->Unit){
    var lineCount by remember { mutableIntStateOf(0) }
    var givenLines by remember { mutableIntStateOf(4) }
    val context  =LocalContext.current
    val isMore by remember {
        derivedStateOf { lineCount > givenLines }
    }
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
                },
                maxLines = givenLines,
                overflow = TextOverflow.Clip,
                modifier = Modifier.fillMaxWidth()
            )
            if(isMore) {
                TextButton(onClick = {
                    givenLines = lineCount
                }, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "drop down"
                        )
                        Text(
                            text = "Read More",
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

