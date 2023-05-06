package com.example.jettipapp.utils

fun calculateTotalTip(totalBill:Double,percentage:Int): Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty())
        (totalBill*percentage) / 100 else 0.0
}

fun calculateTotalBill(totalBill: Double,
              splitBy:Int,
              tipPercentage:Int): Double{

    val total= calculateTotalTip(totalBill,tipPercentage)

    return (totalBill+total)/splitBy
}
//
//fun total(total:Double): Double{
//
//    var result = calculateTotalBill()
//}