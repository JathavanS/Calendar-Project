import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

/**
 * Month stores information such as an Arraylist filled with Days
 *
 * Jathavan Sellathurai
 */

public class Month implements Serializable
{

    private ArrayList<Day> days;
    private int year;
    public Month(int year2)
    {
        days = new ArrayList<Day>();
        this.year = year2;
    }
    public int getYear()
    {
        return this.year;
    }
    //adds a day into the days arraylist
    public void addDay(Day d)
    {
        days.add(d);
    }
    //returns the day Size
    public int daysSize()
    {
        return days.size();
    }
    //returns a day given an index
    public Day getDay(int a)
    {
        return days.get(a);
    }

}