package com.example.libreria_pde

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.google.firebase.firestore.FirebaseFirestore


class NovelWidgetProvider : AppWidgetProvider() {

    private val db = FirebaseFirestore.getInstance()


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        for (appWidgetId in appWidgetIds) {
            upDateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun upDateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
        remoteViews.removeAllViews(R.id.widgetListView)

        db.collection("Libreria")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.getBoolean("favorite") == true) {
                        val title = document.getString("title")
                        val views = RemoteViews(context.packageName, R.layout.widget_item)
                        views.setTextViewText(R.id.txtTitulo, title)
                        remoteViews.addView(R.id.widgetListView, views)
                    }
                }
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            }
            .addOnFailureListener { exception ->
                Log.w("NovelWidgetProvider", "Error getting documents.", exception)
            }
    }

    //Las funciones de abajo son para futuras mejoras de la aplicación. Actualmente no se usan.
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            onClose(context)
        }
    }
    private fun onClose(context: Context) {
        val intent = Intent(context, NovelWidgetProvider::class.java)
        context.stopService(intent)
    }

}