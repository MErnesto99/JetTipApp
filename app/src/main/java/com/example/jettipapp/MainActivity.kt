package com.example.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.utils.calculateTotalBill
import com.example.jettipapp.utils.calculateTotalTip
import com.example.jettipapp.widgets.RoundIconId

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                Column() {

                    MainContent()

                }

            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            content()
        }

    }
}

@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(130.dp)
            .clip(CircleShape.copy(all = CornerSize(12.dp))), color = Color(0Xffe6d1f7)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent() {

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    var splitByState = remember {
        mutableStateOf(1)
    }

    var totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    val range = IntRange(start = 1, endInclusive = 100)

    BillForm(
        range=range,
        splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState
    ) { }
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {

    //totalBillState is connected to the value state inside of TextField
    val totalBillState =
        remember { mutableStateOf("") } // it takes this as mutableState because of "="
//    val totalBillState by remember{ mutableStateOf("") } // it takes this as String
    val validState =
        remember(totalBillState.value) {// This state is used to check if there is something inside the TextField
            totalBillState.value.trim().isNotEmpty()
        }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()


    TopHeader(totalPerPerson = totalPerPersonState.value)

    Surface(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column() {
            InputField(valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions

                    onValChange(totalBillState.value)

                    keyboardController?.hide()


                })//validate text and hides the keyboard if there something on the TextField

            if (validState) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = modifier
                            .padding(10.dp),
//                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Split"
                        )
//                        Spacer(modifier = Modifier.width(120.dp))
                    }

                    Row(
                        modifier = modifier.padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RoundIconId(
                            image = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                    if (splitByState.value > 1) splitByState.value - 1 else 1

                                totalPerPersonState.value = calculateTotalBill(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage,
                                    splitBy = splitByState.value
                                )
                            },
                            tint = Color.Black.copy(alpha = 0.8f)
                        ) { image, tint ->
                            Icon(imageVector = image, tint = tint, contentDescription = "")
                        }

                        Text(text = "$splitByState", modifier = modifier.padding(10.dp))

                        RoundIconId(
                            image = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value += 1
                                    totalPerPersonState.value = calculateTotalBill(
                                        totalBill = totalBillState.value.toDouble(),
                                        tipPercentage = tipPercentage,
                                        splitBy = splitByState.value
                                    )
                                }


                            },
                            tint = Color.Black.copy(alpha = 0.8f)
                        ) { image, tint ->
                            Icon(imageVector = image, tint = tint, contentDescription = "")
                        }


                    }
                }


                Row(
                    modifier = modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Tip",
                        modifier = modifier
                            .padding(3.dp),
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = modifier.width(200.dp))
                    Text(
                        text = "$${tipAmountState.value}", modifier = Modifier.padding(3.dp),
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.SemiBold
                    )

                }

                Column(
                    modifier = modifier
                        .padding(0.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$tipPercentage%",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = modifier.height(10.dp))

                    Slider(value = sliderPositionState.value, onValueChange = { newVal ->
                        sliderPositionState.value = newVal

                        tipAmountState.value = calculateTotalTip(
                            totalBill = totalBillState.value.toDouble(),
                            percentage = tipPercentage
                        )

                        totalPerPersonState.value = calculateTotalBill(
                            totalBill = totalBillState.value.toDouble(),
                            tipPercentage = tipPercentage,
                            splitBy = splitByState.value
                        )
                    }, modifier = modifier
                        .padding(start = 16.dp, end = 16.dp),
                        steps = 5, onValueChangeFinished = {
                            //Todo, once slider stops moving we can do
                            // something with the value
                        })
                }

            } else {
                Box() {}

            }

        }


    }
}


//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipAppTheme {}
}