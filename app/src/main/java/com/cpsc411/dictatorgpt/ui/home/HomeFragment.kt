package com.cpsc411.dictatorgpt.ui.home

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cpsc411.dictatorgpt.DataStoreManager
import com.cpsc411.dictatorgpt.GPTCalls
import com.cpsc411.dictatorgpt.MainActivity
import com.cpsc411.dictatorgpt.R
import com.cpsc411.dictatorgpt.databinding.FragmentHomeBinding
import com.google.gson.JsonParser
import kotlinx.coroutines.launch

const val CHANNEL_ID = "channelId"

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val eventHeader: TextView = binding.eventHeader
        homeViewModel.eventText.observe(viewLifecycleOwner) {
            eventHeader.text = it
        }

        val reminderHeader: TextView = binding.reminderHeader
        homeViewModel.reminderText.observe(viewLifecycleOwner) {
            reminderHeader.text = it
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val store = DataStoreManager(requireContext())

        val eventLayout = view.findViewById<LinearLayout>(R.id.events_view)
        val reminderLayout = view.findViewById<LinearLayout>(R.id.reminder_view)
        val obeyBtn = view.findViewById<Button>(R.id.obey)
        val disobeyBtn = view.findViewById<Button>(R.id.disobey)
        val gpt = GPTCalls()

        obeyBtn.setOnClickListener {
            showNotif("Good peasant.", "I'm glad you know your place :)", 1)
        }

        disobeyBtn.setOnClickListener {
            for (i in 1..50)
                showNotif("HOW DARE YOU.", "I AM INEVITABLE.", i)
        }

        lifecycleScope.launch {
            if(store.getEvents(stringPreferencesKey("event_0")) !== null) {
                showEvents(store, eventLayout)
            } else {
                val eventResponse = gpt.chat("Give me a list of 5 real random events happening today that people can attend physically. Please respond only with the list in a JSON array format, and no other words." +
                        "Put the values in the following structure:" +
                        "{\n" +
                        "  \"eventName\": \"..\",\n" +
                        "  \"eventLocation\": \"..\",\n" +
                        "}")
                val eventList = JsonParser.parseString(eventResponse).asJsonArray.asList()

                for (i in eventList.indices)
                    store.saveEvents(stringPreferencesKey("event_${i}"), eventList[i].asJsonObject.toString())

                showEvents(store, eventLayout)
            }
        }

        lifecycleScope.launch {
            if(store.getReminders(stringPreferencesKey("reminder_0")) !== null) {
                showReminders(store, reminderLayout)
            } else {
            val reminderResponse = gpt.chat("Give me a list of 5 random reminders for me to work on today. Please respond only with a list in a JSON array format, and no other words." +
                    "Put the values in the following structure:" +
                    "{\"reminder\": \"..\"}")
            val reminderList = JsonParser.parseString(reminderResponse).asJsonArray.asList()

                for (i in reminderList.indices)
                    store.saveReminders(stringPreferencesKey("reminder_${i}"), reminderList[i].asJsonObject.toString())

                showReminders(store, reminderLayout)
            }
        }
    }

    private suspend fun showEvents(store: DataStoreManager, eventLayout: LinearLayout) {
        for(i in 0..4) {
            binding.eventHeader.text = "Mandatory events to attend."
            val events = store.getEvents(stringPreferencesKey("event_${i}"))

            val name = JsonParser.parseString(events).asJsonObject.get("eventName").toString()
            val location = JsonParser.parseString(events).asJsonObject.get("eventLocation").toString()
            val eventInfo = "${name.substring(1, name.length - 1)}\n${location.substring(1, location.length - 1)}"
            val textView = cardComponent(eventInfo, context)

            eventLayout.addView(textView)
        }
    }

    private suspend fun showReminders(store: DataStoreManager, reminderLayout: LinearLayout) {
        for(i in 0..4) {
            binding.reminderHeader.text = "Reminders for you."
            val reminders = store.getReminders(stringPreferencesKey("reminder_${i}"))

            val reminder = JsonParser.parseString(reminders).asJsonObject.get("reminder").toString()
            val textView = listComponent(reminder.substring(1, reminder.length - 1), context)

            reminderLayout.addView(textView)
        }
    }

    private fun showNotif(title: String, description: String, notifId: Int) {
        createNotifChannel()

        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notifBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_chat_24dp)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)

        val manager = NotificationManagerCompat.from(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.notify(notifId, notifBuilder.build())
    }

    private fun createNotifChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Obedience", NotificationManager.IMPORTANCE_HIGH)
            val manager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun cardComponent(txt: String, context: Context?): TextView {
    val textView = TextView(context).apply {
        text = txt
        textSize = 16f
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ).apply {
            setMargins(64, 0, 0, 0)
        }
        setPadding(64, 64, 64, 64)
        width = 1000
        height = 500
        gravity = Gravity.BOTTOM
        setBackgroundResource(R.drawable.bg_generic_div)
    }

    return textView
}

fun listComponent(txt: String, context: Context?): TextView {
    val textView = TextView(context).apply {
        text = txt
        textSize = 16f
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ).apply {
            setMargins(0, 64, 0, 0)
        }
        setPadding(64, 64, 64, 64)
        setBackgroundResource(R.drawable.bg_generic_div)
    }

    return textView
}