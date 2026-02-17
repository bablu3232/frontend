package com.simats.drugssearch.network

import com.google.gson.annotations.SerializedName

data class OcrResponse(
    @SerializedName("report_category") val reportCategory: String,
    @SerializedName("parameters") val parameters: Map<String, OcrParameterDetail>
)

data class OcrParameterDetail(
    @SerializedName("value") val value: Double,
    @SerializedName("unit") val unit: String,
    @SerializedName("status") val status: String,
    @SerializedName("category") val category: String
)
