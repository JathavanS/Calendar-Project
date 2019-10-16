import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

/**
 * Day stores information about a specific day such as the date of month and has an ArrayList filled with Reminders
 *
 * Jathavan Sellathurai
 */

public class Day implements Serializable{
    private int DateOfMonth;

    private Calendar time;
    private ArrayList<Reminder> reminders;

    public Day(int d,Date t)
    {
        DateOfMonth = d;
        reminders = new ArrayList<Reminder>();
        time = Calendar.getInstance();
        time.setTime(t);
    }
    public int getDayNumber()
    {
        return DateOfMonth;
    }
    //adds a reminder into the remninders arraylist and automatically sorts the arraylist
    public void addReminder(String m, Date t)
    {
        Reminder newReminder = new Reminder(m,t,this);
        reminders.add(newReminder);
        sortReminders();
    }
    // returns the day of Week(integer)
    public int getDayofWeek()
    {
        return time.get(time.DAY_OF_WEEK);
    }
    // returns a single reminder
    public Reminder getOneReminder(int i)
    {
        return reminders.get(i);
    }
    // returns the number of reminder this day holds
    public int getReminderSize()
    {
        return reminders.size();
    }
    // sorts the reminders arraylist by time using a sorting algorithm
    public void sortReminders()
    {
        for(int i = 0; i < reminders.size();i++)
        {
            Reminder lowReminder = reminders.get(i);
            int lowHour = lowReminder.getHour();
            int lowMinute = lowReminder.getMinute();
            int lowPos = i;
            for(int j = i + 1; j < reminders.size(); j++)
            {
                Reminder currentReminder = reminders.get(j);
                int currentHour = currentReminder.getHour();
                int currentMinute = currentReminder.getMinute();
                if(currentHour < lowHour)
                {
                    lowReminder = currentReminder;
                    lowHour = currentHour;
                    lowMinute = currentMinute;
                    lowPos = j;
                }
                else if(currentHour == lowHour)
                {
                    if(currentMinute < lowMinute)
                    {
                        lowReminder = currentReminder;
                        lowHour = currentHour;
                        lowMinute = currentMinute;
                        lowPos = j;
                    }
                }
            }
            Reminder switchReminder = reminders.get(i);
            reminders.set(lowPos,switchReminder);
            reminders.set(i,lowReminder);
        }
    }

}