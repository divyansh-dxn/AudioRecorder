package com.dxn.recorder

import java.util.*
import java.util.concurrent.TimeUnit

class TimeAgo {
    companion object {
        fun getTimeAgo(duration:Long) : String{
            val now = Date()
            val sec = TimeUnit.MILLISECONDS.toSeconds(now.time-duration)
            val min = TimeUnit.MILLISECONDS.toMinutes(now.time-duration)
            val hr = TimeUnit.MILLISECONDS.toHours(now.time-duration)
            val days = TimeUnit.MILLISECONDS.toDays(now.time-duration)

            return if(sec<60) {
                "just now"
            } else if(min==1L) {
               "a minute ago"
            } else if(min>1&&min<60) {
               "${min} minutes ago"
            } else if(hr==1L) {
               "an hour ago"
            }else if(hr>1&&hr<24) {
               "${hr} hours ago"
            }else if(days==1L) {
               "a day ago"
            }  else {
                "${days} days ago"
            }
        }
    }
}