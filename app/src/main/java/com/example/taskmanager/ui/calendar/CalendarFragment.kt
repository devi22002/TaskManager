package com.example.taskmanager.ui.calendar

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        updateCalendar()

        binding.btnPrev.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        binding.btnNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        return binding.root
    }

    private fun updateCalendar() {
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id"))
        binding.tvMonthYear.text = monthFormat.format(calendar.time)

        binding.gridCalendar.removeAllViews()
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val firstDay = cal.get(Calendar.DAY_OF_WEEK) - 1
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        addDayLabels()

        for (i in 0 until firstDay) addEmptyCell()

        for (day in 1..maxDay) addDateCell(day)
    }

    private fun addDayLabels() {
        val days = listOf("MIN", "SEN", "SEL", "RAB", "KAM", "JUM", "SAB")
        for (d in days) {
            val tv = createTextView(d, isBold = true)
            binding.gridCalendar.addView(tv)
        }
    }

    private fun addEmptyCell() {
        val tv = createTextView("")
        binding.gridCalendar.addView(tv)
    }

    private fun addDateCell(day: Int) {
        val tv = createTextView(day.toString())

        val today = Calendar.getInstance()
        if (day == today.get(Calendar.DAY_OF_MONTH) &&
            calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
        ) {
            val circle = GradientDrawable()
            circle.cornerRadius = 100f
            circle.setColor(ContextCompat.getColor(requireContext(), R.color.calendar_selected_bg))
            tv.background = circle
            tv.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            tv.typeface = Typeface.DEFAULT_BOLD
        }

        tv.setOnClickListener { /* later: open bottom sheet or list */ }

        binding.gridCalendar.addView(tv)
    }

    private fun createTextView(text: String, isBold: Boolean = false): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 15f
            setPadding(6, 12, 6, 12)
            gravity = android.view.Gravity.CENTER
            if (isBold) typeface = Typeface.DEFAULT_BOLD
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
