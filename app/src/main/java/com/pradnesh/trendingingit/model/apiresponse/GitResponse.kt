package com.pradnesh.trendingingit.model.apiresponse

import com.google.gson.annotations.SerializedName

data class GitResponse(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("incomplete_results") val incompleteResults: Boolean,
    @SerializedName("items") val items: List<Item>?
)
