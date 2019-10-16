import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

/**
 * Reminder stores information about the calandar reminders and gives information such as the message and the time
 *
 * Jathavan Sellathurai
 */
public class Reminder implements Serializable {
    private String message;
    private Calendar time;
    private Day thisDay;

    public Reminder(String m,Date t , Day d)
    {
        message = m;
        time = Calendar.getInstance();
        time.setTime(t);
        thisDay = d;
    }
    // returns the day that the reminder is in
    public Day getDay()
    {
        return thisDay;
    }
    // returns the hour of this reminder
    public int getHour()
    {
        return time.get(11);
    }
    // returns the minute of this reminder
    public int getMinute()
    {
        return time.get(12);
    }
    // returns the time of this reminder in string form
    public String getTime()
    {
        String s = "";
        if(time.get(10) == 0 )
        {
            s+= "12";
        }
        if(time.get(10) < 10 && time.get(10) != 0)
        {
            s+= "0";
            s += time.get(10);
        }
        else if(time.get(10) != 0)
        {
            s += time.get(10);
        }
        s += ":";
        if(time.get(12) < 10)
        {
            s+= "0";
        }
        s += time.get(12);
        s+= " ";
        if(time.get(time.AM_PM) == 0)
        {
            s+= "AM";
        }
        else
        {
            s+= "PM";
        }
        return s;
    }
    // gets the message
    public String getMessage()
    {
        return message;
    }
    // this helps listViews get the message
    public String toString()
    {
        return message;
    }
    // returns the message in a sorted way. it inlclude the time and if the message has a space(" ") than it will be change to a "\n"
    // because it makes it easier to fit on the screen
    public String getSortedMessage()
    {
        String s = "";
        for(int i = 0; i < message.length();i++)
        {
            if(message.charAt(i) == ' ')
            {
                s+= "\n";
            }
            else
            {
                s+= message.charAt(i);
            }
        }
        return s;
    }

}