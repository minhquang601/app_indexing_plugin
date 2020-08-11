package io.plugin.app_indexing_b2b_plugin

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.annotation.NonNull
import com.google.firebase.appindexing.FirebaseAppIndex
import com.google.firebase.appindexing.Indexable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.lang.Exception
import java.lang.reflect.Type


class AppIndexingB2bPlugin(registrar: Registrar) : MethodCallHandler {
    private val logTag = "AppIndexingB2BLog"
    private val KEY_APP_INDEXING = "KEY_APP_INDEXING_B2B"
    val gson = Gson()
    val type: Type = object : TypeToken<MutableList<ProductEntity?>?>() {}.type

    var firebaseAppIndex: FirebaseAppIndex = FirebaseAppIndex.getInstance();
    val BASE_URL = Uri.parse("https://merchant.vinid.net/ordering/")
    private lateinit var channel: MethodChannel
    private var pref: SharedPreferences = registrar.activeContext().getSharedPreferences("PREF_APP_INDEXING", MODE_PRIVATE)

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "app_indexing_b2b_plugin")
            channel.setMethodCallHandler(AppIndexingB2bPlugin(registrar))
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "updateIndexing" -> updateIndexing(call, result)
            else -> result.notImplemented()
        }
    }

    private fun updateIndexing(call: MethodCall, result: Result) {
        var productName = call.argument("name") as? String
        var query = call.argument("query") as? String

        val appUri = BASE_URL.buildUpon()
                .appendPath("search?query=$query")
                .appendPath("&utm_campaign=appindex&utm_medium=appindex&utm_source=appindex")
                .build()
                .toString()

        val productEntity = ProductEntity(
                name = productName ?: "",
                url = appUri,
                keyWord = query ?: "")

        saveListProduct(productEntity = productEntity, call = call, result = result)

    }

    private fun saveListProduct(productEntity: ProductEntity, call: MethodCall, result: Result) {
        try {
            val listStrProduct = pref.getString(KEY_APP_INDEXING, "")
            var listObj = gson.fromJson<MutableList<ProductEntity>>(listStrProduct, type)

            if (listObj != null) {
                if (!listObj.contains(productEntity)) {
                    listObj.add(productEntity)
                    listObj.reverse()

                    val listIndexable: MutableList<Indexable> = mutableListOf()

                    listObj.forEach { productEntity ->
                        listIndexable.add(
                                Indexable.Builder()
                                        .setName(productEntity.name)
                                        .setUrl(productEntity.url)
                                        .setKeywords(productEntity.keyWord)
//                                        .setDescription(productEntity.description)
                                        .build()
                        )
                    }

                    val task = FirebaseAppIndex.getInstance().update(*listIndexable.toTypedArray())

                    task.addOnSuccessListener {
                        result.success(true)
                    }

                    task.addOnFailureListener { _ ->
                        result.success(false)
                    }

                    pref.edit().putString(KEY_APP_INDEXING, gson.toJson(listObj)).apply()
                }
            } else {
                listObj = mutableListOf()
                listObj.add(productEntity)

                val articleToIndex = Indexable.Builder()
                        .setName(productEntity.name)
                        .setUrl(productEntity.url)
                        .setKeywords(productEntity.keyWord)
//                        .setDescription(productEntity.description)
                        .build()
                val task = FirebaseAppIndex.getInstance().update(articleToIndex)

                task.addOnSuccessListener {
                    result.success(true)
                }

                task.addOnFailureListener { _ ->
                    result.success(false)
                }

                pref.edit().putString(KEY_APP_INDEXING, gson.toJson(listObj)).apply()
            }

        } catch (e: Exception) {
            Log.d("AppIndexing B2B", e.localizedMessage)
        }

    }


}
