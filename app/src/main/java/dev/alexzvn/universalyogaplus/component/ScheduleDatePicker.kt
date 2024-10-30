package dev.alexzvn.universalyogaplus.component
import android.util.Log
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.alexzvn.universalyogaplus.local.DayOfWeek
import dev.alexzvn.universalyogaplus.util.format
import dev.alexzvn.universalyogaplus.util.toLocal
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
class ScheduleSelectableDates (
    val since: Date = Date(),
    val dayOfWeek: DayOfWeek? = null,
    val ignoreDate: List<Date> = listOf()
) : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val selecting = Date(utcTimeMillis).toLocal()
        val since = since.toLocal()

        if (since.isAfter(selecting)) {
            return false
        }

        for (date in ignoreDate) {
            val local = date.toLocal()
            val a = local.format("dd/MM/yyyy")
            val b = selecting.format("dd/MM/yyyy")

            if (a == b) {
                return false
            }
        }

        if (dayOfWeek == null) {
            return true
        }

        return selecting.dayOfWeek.name == dayOfWeek.name
    }

    override fun isSelectableYear(year: Int): Boolean {
        val local = since.toLocal()

        return year >= local.year
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun ScheduleDatePickerPreview() {

    val state = rememberDatePickerState(
        selectableDates = ScheduleSelectableDates(
            dayOfWeek = DayOfWeek.FRIDAY
        )
    )

    DatePicker(state = state, showModeToggle = false)
}
