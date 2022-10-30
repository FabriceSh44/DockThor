package com.fan.tiptop.dockthor.logic

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeStationAlarm
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class GeofenceSetupViewModel(val station: CitiStationStatus, alarms: List<CitibikeStationAlarm>) :
    ViewModel() {
    private val TAG = "GeofenceSetupViewModel"
    val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH)

    val messageToDisplayLD = MutableLiveData("")
    private val pickDayOfWeeks: MutableSet<Int> = mutableSetOf()
    private var _kernel: DockThorKernel = DockThorKernel.getInstance()
    val startTimeClickedLD = MutableLiveData(false)
    val endTimeClickedLD = MutableLiveData(false)
    val isMondayPickedLD = MutableLiveData(false)
    val isTuesdayPickedLD = MutableLiveData(false)
    val isWednesdayPickedLD = MutableLiveData(false)
    val isThursdayPickedLD = MutableLiveData(false)
    val isFridayPickedLD = MutableLiveData(false)
    val isSaturdayPickedLD = MutableLiveData(false)
    val isSundayPickedLD = MutableLiveData(false)
    val isSwitchCheckedLD = MutableLiveData(false)
    val progressLD = MutableLiveData(5)
    val maxDockValueLD = MutableLiveData(10)
    val startTimeLD = MutableLiveData("8:00")
    val endTimeLD = MutableLiveData("9:45")
    val startTimeHour: Int
        get() {
            return LocalTime.parse(startTimeLD.value, dtf).hour
        }
    val startTimeMinute: Int
        get() {
            return LocalTime.parse(startTimeLD.value, dtf).minute
        }
    val endTimeHour: Int
        get() {
            return LocalTime.parse(endTimeLD.value, dtf).hour
        }
    val endTimeMinute: Int
        get() {
            return LocalTime.parse(endTimeLD.value, dtf).minute
        }

    init {
        fromListAlarmInput(alarms)
    }

    fun onEnabledSwitchClick() {
        isSwitchCheckedLD.value = isSwitchCheckedLD.value?.not()
        updateAlarms()
    }

    fun updateAlarms() {
        val alarmInputs: List<CitibikeStationAlarm> = this.toListAlarmInput()
        if (isSwitchCheckedLD.value == true) {
            messageToDisplayLD.value = "Alarm created" //TODO should pop only if next alarm changed
            _kernel.addAlarmForStation(station, alarmInputs)
        } else {
            messageToDisplayLD.value = "Alarm removed"
            _kernel.removeAlarmForStation(alarmInputs)
        }
    }

    private fun fromListAlarmInput(alarms: List<CitibikeStationAlarm>) {
        if (alarms.isEmpty())
            return
        isSwitchCheckedLD.value = true
        val firstAlarm = alarms.first()
        val startTime = LocalTime.of(firstAlarm.hourOfDay, firstAlarm.minuteOfDay)
        startTimeLD.value = startTime?.format(dtf)
        endTimeLD.value = startTime?.plusSeconds(firstAlarm.delayInSec)?.format(dtf)
        for (alarm in alarms) {
            when (alarm.dayOfWeek) {
                Calendar.MONDAY -> {
                    isMondayPickedLD.value = true
                    updateDayOfWeeks(isMondayPickedLD,Calendar.MONDAY)
                }
                Calendar.TUESDAY -> {
                    isTuesdayPickedLD.value = true
                    updateDayOfWeeks(isTuesdayPickedLD,Calendar.TUESDAY)
                }
                Calendar.WEDNESDAY -> {
                    isWednesdayPickedLD.value = true
                    updateDayOfWeeks(isWednesdayPickedLD,Calendar.WEDNESDAY)
                }
                Calendar.THURSDAY -> {
                    isThursdayPickedLD.value = true
                    updateDayOfWeeks(isThursdayPickedLD,Calendar.THURSDAY)
                }
                Calendar.FRIDAY -> {
                    isFridayPickedLD.value = true
                    updateDayOfWeeks(isFridayPickedLD,Calendar.FRIDAY)
                }
                Calendar.SATURDAY -> {
                    isSaturdayPickedLD.value = true
                    updateDayOfWeeks(isSaturdayPickedLD,Calendar.SATURDAY)
                }
                Calendar.SUNDAY -> {
                    isSundayPickedLD.value = true
                    updateDayOfWeeks(isSundayPickedLD,Calendar.SUNDAY)
                }
            }
        }
    }

    private fun toListAlarmInput(): List<CitibikeStationAlarm> {
        val result = mutableListOf<CitibikeStationAlarm>()
        val startTime: LocalTime? = startTimeLD.value?.let { LocalTime.parse(it, dtf) }
        val endTime: LocalTime? = endTimeLD.value?.let { LocalTime.parse(it, dtf) }
        if (startTime == null || endTime == null) {
            Log.e(TAG, "Unable to parse ${startTimeLD.value} or ${endTimeLD.value}")
            return mutableListOf()
        }
        var secondDelay: Int
        if (endTime > startTime) {
            secondDelay = endTime.toSecondOfDay() - startTime.toSecondOfDay()
        } else {
            secondDelay = LocalTime.parse("11:59:59").toSecondOfDay() - startTime.toSecondOfDay()
            secondDelay += endTime.toSecondOfDay()
        }

        for (pickedDayOfWeek in pickDayOfWeeks) {
            result.add(
                CitibikeStationAlarm(
                    stationId = station.stationId,
                    dayOfWeek = pickedDayOfWeek,
                    hourOfDay = startTime.hour,
                    minuteOfDay = startTime.minute,
                    delayInSec = secondDelay.toLong(),
                    dockThreshold = progressLD.value ?: 5
                )
            )
        }
        return result
    }

    fun updateDayOfWeeks(isDayPickedLD: MutableLiveData<Boolean>, day: Int) {
        if (isDayPickedLD.value == true) {
            pickDayOfWeeks.add(day)
        } else {
            pickDayOfWeeks.remove(day)
        }
        if (isSwitchCheckedLD.value == true) {
            updateAlarms()
        }
    }

    fun updateStartTime(hour: Int, minute: Int) {
        startTimeLD.value = LocalTime.of(hour, minute)?.format(dtf)
        if (isSwitchCheckedLD.value == true) {
            updateAlarms()
        }
    }

    fun updateEndTime(hour: Int, minute: Int) {
        endTimeLD.value = LocalTime.of(hour, minute)?.format(dtf)
        if (isSwitchCheckedLD.value == true) {
            updateAlarms()
        }
    }

    fun onStartTimeClicked() {
        startTimeClickedLD.value = true
    }

    fun onEndTimeClicked() {
        endTimeClickedLD.value = true
    }

    fun onMondayButtonClick() {
        isMondayPickedLD.value = isMondayPickedLD.value?.not()
        updateDayOfWeeks(isMondayPickedLD, Calendar.MONDAY)
    }

    fun onTuesdayButtonClick() {
        isTuesdayPickedLD.value = isTuesdayPickedLD.value?.not()
        updateDayOfWeeks(isTuesdayPickedLD, Calendar.TUESDAY)
    }

    fun onWednesdayButtonClick() {
        isWednesdayPickedLD.value = isWednesdayPickedLD.value?.not()
        updateDayOfWeeks(isWednesdayPickedLD, Calendar.WEDNESDAY)
    }

    fun onThursdayButtonClick() {
        isThursdayPickedLD.value = isThursdayPickedLD.value?.not()
        updateDayOfWeeks(isThursdayPickedLD, Calendar.THURSDAY)
    }

    fun onFridayButtonClick() {
        isFridayPickedLD.value = isFridayPickedLD.value?.not()
        updateDayOfWeeks(isFridayPickedLD, Calendar.FRIDAY)
    }

    fun onSaturdayButtonClick() {
        isSaturdayPickedLD.value = isSaturdayPickedLD.value?.not()
        updateDayOfWeeks(isSaturdayPickedLD, Calendar.SATURDAY)
    }

    fun onSundayButtonClick() {
        isSundayPickedLD.value = isSundayPickedLD.value?.not()
        updateDayOfWeeks(isSundayPickedLD, Calendar.SUNDAY)
    }

}
