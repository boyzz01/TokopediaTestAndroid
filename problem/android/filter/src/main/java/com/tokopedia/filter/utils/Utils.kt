package com.tokopedia.filter.utils

import android.content.Context
import android.content.res.Resources
import java.io.IOException


fun getJsonData(context: Context, fileName: String): String? {


    val jsonString: String
    try {
        jsonString = context.resources.openRawResource(context.resources.getIdentifier(fileName,
                "raw", context.packageName)).bufferedReader().use {it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    } catch (e : Resources.NotFoundException ) {
        e.printStackTrace()
        return null
    }
    return jsonString
}