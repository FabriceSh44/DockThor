package com.fan.tiptop.dockthor.logic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeStationAlarm
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class GeofenceSetupViewModel(val station: CitiStationStatus,alarms:List<CitibikeStationAlarm>) : ViewModel() {

    val messageToDisplayLD = MutableLiveData("")
    private val pickDayOfWeeks: MutableSet<Int> = mutableSetOf()
    private var _kernel: DockThorKernel = DockThorKernel.getInstance()
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
    init {
        fromListAlarmInput(alarms)
    }

    fun onEnabledSwitchClick() {
        val alarmInputs: List<CitibikeStationAlarm> = this.toListAlarmInput()
        isSwitchCheckedLD.value = isSwitchCheckedLD.value?.not()
        if (isSwitchCheckedLD.value == true) {
            messageToDisplayLD.value = "Alarm created"
            _kernel.addAlarmForStation(station, alarmInputs)
        } else {
            messageToDisplayLD.value = "Alarm removed"
            _kernel.removeAlarmForStation(alarmInputs)
        }
    }

    private fun fromListAlarmInput(alarms:List<CitibikeStationAlarm>) {
        if (alarms.isEmpty())
            return
        isSwitchCheckedLD.value=true
        val firstAlarm = alarms.first()
        startTimeLD.value= "${firstAlarm.hourOfDay}:${firstAlarm.minuteOfDay}"
        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("H:m", Locale.ENGLISH)
        val startTime: LocalTime? = startTimeLD.value?.let { LocalTime.parse(it, dtf) }
        endTimeLD.value=startTime?.plusSeconds(firstAlarm.delayInSec)?.format(dtf)
        for (alarm in alarms)
        {
            when(alarm.dayOfWeek){
                Calendar.MONDAY->{
                    isMondayPickedLD.value=true
                }
                Calendar.TUESDAY->{isTuesdayPickedLD.value=true}
                Calendar.WEDNESDAY->{isWednesdayPickedLD.value=true}
                Calendar.THURSDAY->{isThursdayPickedLD.value=true}
                Calendar.FRIDAY->{isFridayPickedLD.value=true}
                Calendar.SATURDAY->{isSaturdayPickedLD.value=true}
                Calendar.SUNDAY->{isSundayPickedLD.value=true}
            }
        }
//        val result = mutableListOf<CitibikeStationAlarm>()
//        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("H:m", Locale.ENGLISH)
//        val startTime: LocalTime? = startTimeLD.value?.let { LocalTime.parse(it, dtf) }
//        val endTime: LocalTime? = endTimeLD.value?.let { LocalTime.parse(it, dtf) }
//        if (startTime == null || endTime == null) {
//            return mutableListOf()
//        }
//        var secondDelay: Int
//        if (endTime > startTime) {
//            secondDelay = endTime.toSecondOfDay() - startTime.toSecondOfDay()
//        } else {
//            secondDelay = LocalTime.parse("11:59:59").toSecondOfDay() - startTime.toSecondOfDay()
//            secondDelay += endTime.toSecondOfDay()
//        }
//
//        for (pickedDayOfWeek in pickDayOfWeeks) {
//            result.add(
//                CitibikeStationAlarm(
//                    stationId = station.stationId,
//                    dayOfWeek = pickedDayOfWeek,
//                    hourOfDay = startTime.hour,
//                    minuteOfDay = startTime.minute,
//                    delayInSec = secondDelay.toLong()
//                )
//            )
//        }
    }
    private fun toListAlarmInput(): List<CitibikeStationAlarm> {
        val result = mutableListOf<CitibikeStationAlarm>()
        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("H:m", Locale.ENGLISH)
        val startTime: LocalTime? = startTimeLD.value?.let { LocalTime.parse(it, dtf) }
        val endTime: LocalTime? = endTimeLD.value?.let { LocalTime.parse(it, dtf) }
        if (startTime == null || endTime == null) {
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
                    delayInSec = secondDelay.toLong()
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
    }

    fun onStartTimeSubmitted() {}
    fun onEndTimeSubmitted() {}
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
