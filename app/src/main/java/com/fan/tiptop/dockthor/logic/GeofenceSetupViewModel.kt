package com.fan.tiptop.dockthor.logic

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeMetaAlarmBean
import com.fan.tiptop.citiapi.data.CitibikeStationAlarm
import com.fan.tiptop.citiapi.data.CitibikeStationAlarmData
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class GeofenceSetupViewModel(
    val station: CitiStationStatus,
    private val metaAlarmBean: CitibikeMetaAlarmBean
) :
    ViewModel() {
    private val TAG = "GeofenceSetupViewModel"
    private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH)

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
    private var prevUpdateMessage: String = ""

    init {
        fromMetaAlarmBean(metaAlarmBean)
    }

    fun onEnabledSwitchClick() {
        isSwitchCheckedLD.value = isSwitchCheckedLD.value?.not()
        if (isSwitchCheckedLD.value == false) {
            _kernel.removeAlarmForStation(station.stationId)
        }
        updateAlarms()
    }

    private fun updateAlarms(displayMessage: Boolean = true) {
        updateMetaAlarmBean()
        val nextAlarmInput: CitibikeStationAlarm? = metaAlarmBean.nextAlarm
        if (nextAlarmInput != null && isSwitchCheckedLD.value == true) {
            _kernel.setupNextAlarmForStation(metaAlarmBean)
            val updateMessage = getUpdateMessage(metaAlarmBean)
            if (updateMessage != prevUpdateMessage) {
                // weak way to not spam alarms
                if (displayMessage) {
                    prevUpdateMessage = updateMessage
                    messageToDisplayLD.value = updateMessage
                }
            }
        }
    }

    private fun getUpdateMessage(alarmBean: CitibikeMetaAlarmBean): String {
        val duration =
            Duration.ofMillis(alarmBean.alarmData.wakeUpTimeInMillis - System.currentTimeMillis())
        val days = duration.toDays().toInt()
        val hours = (duration - Duration.ofDays(days.toLong())).toHours().toInt()
        val minutes =
            (duration - Duration.ofDays(days.toLong()) - Duration.ofHours(hours.toLong())).toMinutes()
                .toInt()
        var daysText = ""
        var hoursText = ""
        var comma = ""
        if (days != 0) {
            daysText = "${days} day(s)"
            comma = ","
        }
        hoursText = "${comma}${hours} hour(s)"
        val minutesText = " and ${minutes} minute(s)"
        return "Geofence set to ${daysText}${hoursText}${minutesText} from now"
    }

    private fun fromMetaAlarmBean(alarmBean: CitibikeMetaAlarmBean) {
        if (alarmBean.alarms.isEmpty())
            return
        val startTime = LocalTime.of(alarmBean.alarmData.hourOfDay, alarmBean.alarmData.minuteOfDay)
        startTimeLD.value = startTime?.format(dtf)
        endTimeLD.value = startTime?.plusSeconds(alarmBean.alarmData.delayInSec)?.format(dtf)
        for (alarm in alarmBean.alarms) {
            when (alarm.dayOfWeek) {
                Calendar.MONDAY -> {
                    isMondayPickedLD.value = true
                    updateDayOfWeeks(isMondayPickedLD, Calendar.MONDAY)
                }
                Calendar.TUESDAY -> {
                    isTuesdayPickedLD.value = true
                    updateDayOfWeeks(isTuesdayPickedLD, Calendar.TUESDAY)
                }
                Calendar.WEDNESDAY -> {
                    isWednesdayPickedLD.value = true
                    updateDayOfWeeks(isWednesdayPickedLD, Calendar.WEDNESDAY)
                }
                Calendar.THURSDAY -> {
                    isThursdayPickedLD.value = true
                    updateDayOfWeeks(isThursdayPickedLD, Calendar.THURSDAY)
                }
                Calendar.FRIDAY -> {
                    isFridayPickedLD.value = true
                    updateDayOfWeeks(isFridayPickedLD, Calendar.FRIDAY)
                }
                Calendar.SATURDAY -> {
                    isSaturdayPickedLD.value = true
                    updateDayOfWeeks(isSaturdayPickedLD, Calendar.SATURDAY)
                }
                Calendar.SUNDAY -> {
                    isSundayPickedLD.value = true
                    updateDayOfWeeks(isSundayPickedLD, Calendar.SUNDAY)
                }
            }
        }
        if (alarmBean.alarmData.activated) {
            isSwitchCheckedLD.value = true
        }
        updateAlarms(displayMessage = false)
    }

    private fun updateMetaAlarmBean() {
        val startTime: LocalTime? = startTimeLD.value?.let { LocalTime.parse(it, dtf) }
        val endTime: LocalTime? = endTimeLD.value?.let { LocalTime.parse(it, dtf) }
        if (startTime == null || endTime == null) {
            Log.e(TAG, "Unable to parse ${startTimeLD.value} or ${endTimeLD.value}")
            return
        }
        var secondDelay: Int
        if (endTime > startTime) {
            secondDelay = endTime.toSecondOfDay() - startTime.toSecondOfDay()
        } else {
            secondDelay = LocalTime.parse("11:59:59").toSecondOfDay() - startTime.toSecondOfDay()
            secondDelay += endTime.toSecondOfDay()
        }

        val result = mutableListOf<Pair<Long, CitibikeStationAlarm>>()
        val map: Map<Int, CitibikeStationAlarm> =
            metaAlarmBean.alarms.associateBy { x -> x.dayOfWeek }
        for (pickedDayOfWeek in pickDayOfWeeks) {
            val alarmCalendar: Calendar = getCalendarForDay(startTime, pickedDayOfWeek)
            val wakeUpTimeInMillis = alarmCalendar.timeInMillis
            val alarm = map[pickedDayOfWeek] ?: CitibikeStationAlarm(
                stationId = station.stationId,
                dayOfWeek = pickedDayOfWeek,
            )
            result.add(
                Pair(wakeUpTimeInMillis, alarm)
            )
        }

        val sortedBy = result.sortedBy { x -> x.first }
        val sortedAlarms = sortedBy.map { x -> x.second }
        val alarmData = CitibikeStationAlarmData(
            station.stationId,
            activated = isSwitchCheckedLD.value ?: false,
            startTime.hour,
            startTime.minute,
            secondDelay.toLong(),
            progressLD.value ?: 5,
            sortedBy.firstOrNull()?.first ?: 0
        )
        metaAlarmBean.alarms=sortedAlarms
        metaAlarmBean.alarmData=alarmData
    }

    private fun getCalendarForDay(
        startTime: LocalTime,
        pickedDayOfWeek: Int
    ): Calendar {
        val nowCalendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        val alarmCalendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, startTime.hour)
            set(Calendar.MINUTE, startTime.minute)
            set(Calendar.SECOND, 0)
        }
        alarmCalendar.apply { set(Calendar.DAY_OF_WEEK, pickedDayOfWeek) }

        if (alarmCalendar < nowCalendar) {
            alarmCalendar.apply {
                set(
                    Calendar.DAY_OF_YEAR,
                    alarmCalendar.get(Calendar.DAY_OF_YEAR) + 7
                )
            }
        }
        return alarmCalendar
    }

    private fun updateDayOfWeeksWithUpdate(isDayPickedLD: MutableLiveData<Boolean>, day: Int) {
        updateDayOfWeeks(isDayPickedLD, day)
        if (isSwitchCheckedLD.value == true) {
            updateAlarms()
        }
    }

    private fun updateDayOfWeeks(isDayPickedLD: MutableLiveData<Boolean>, day: Int) {
        if (isDayPickedLD.value == true) {
            pickDayOfWeeks.add(day)
        } else {
            pickDayOfWeeks.remove(day)
        }
    }

    fun updateStartTime(hour: Int, minute: Int) {
        val startTime = LocalTime.of(hour, minute)
        startTimeLD.value = startTime.format(dtf)
        val endTime: LocalTime? = endTimeLD.value?.let { LocalTime.parse(it, dtf) }
        if (endTime == null || endTime < startTime) {
            if (hour + 1 == 24) {
                endTimeLD.value = LocalTime.of(23, 59)?.format(dtf)
            } else {
                endTimeLD.value = LocalTime.of(hour + 1, minute)?.format(dtf)
            }
        }
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
        updateDayOfWeeksWithUpdate(isMondayPickedLD, Calendar.MONDAY)
    }

    fun onTuesdayButtonClick() {
        isTuesdayPickedLD.value = isTuesdayPickedLD.value?.not()
        updateDayOfWeeksWithUpdate(isTuesdayPickedLD, Calendar.TUESDAY)
    }

    fun onWednesdayButtonClick() {
        isWednesdayPickedLD.value = isWednesdayPickedLD.value?.not()
        updateDayOfWeeksWithUpdate(isWednesdayPickedLD, Calendar.WEDNESDAY)
    }

    fun onThursdayButtonClick() {
        isThursdayPickedLD.value = isThursdayPickedLD.value?.not()
        updateDayOfWeeksWithUpdate(isThursdayPickedLD, Calendar.THURSDAY)
    }

    fun onFridayButtonClick() {
        isFridayPickedLD.value = isFridayPickedLD.value?.not()
        updateDayOfWeeksWithUpdate(isFridayPickedLD, Calendar.FRIDAY)
    }

    fun onSaturdayButtonClick() {
        isSaturdayPickedLD.value = isSaturdayPickedLD.value?.not()
        updateDayOfWeeksWithUpdate(isSaturdayPickedLD, Calendar.SATURDAY)
    }

    fun onSundayButtonClick() {
        isSundayPickedLD.value = isSundayPickedLD.value?.not()
        updateDayOfWeeksWithUpdate(isSundayPickedLD, Calendar.SUNDAY)
    }

}
