package com.thegoodyard.waiyanhein.thegoodyard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class SaveItemReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Broadcast: message", Toast.LENGTH_SHORT).show()
    }
}