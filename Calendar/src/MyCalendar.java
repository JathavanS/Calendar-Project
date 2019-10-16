import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;


import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.event.EventHandler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;


public class MyCalendar extends Application{

    // these arrays make it easy to create the calendar
    String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    String[] DaysOfWeek = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    // the label that will show the reminders at the side of the screen
    final Label reminderLabel = new Label("");

    final Group root = new Group();
    // the Arraylist that will hold all  months
    final ArrayList<Month> CalendarMonths = new ArrayList<Month>();
    // this arraylist will help take in information from the .dat file and put it into the Calendar Months arraylist
    ArrayList<Month> getCalendar;
    // this observableList will help create the ReminderlistView
    final ObservableList<Reminder> reminderList = FXCollections.observableArrayList();
    @Override
    public void start(Stage primaryStage) throws Exception {
        try
        {
            // tries to get the data from the Calendar.dat file
            try
            {
                FileInputStream fileIn = new FileInputStream("CalendarYears.dat");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                getCalendar = (ArrayList<Month>)(in.readObject());

                for(int i = 0; i < getCalendar.size();i++)
                {
                    CalendarMonths.add(getCalendar.get(i));
                }
                in.close();
                fileIn.close();
            }
            // if the CalendarYears.dat file does not exist it will create a calendar from scratch
            catch(IOException e)
            {
                Calendar calendar = Calendar.getInstance();
                int starting_year = 2019;
                int num_of_days;
                for(int curr_year = 0; curr_year <= 11; curr_year++) {
                    // creates the months
                    if(curr_year + starting_year % 4 == 0)
                    {
                        num_of_days = 366;
                    }
                    else
                    {
                        num_of_days = 365;
                    }
                    for (int i = 0; i < 12; i++) {
                        calendar.set(curr_year + starting_year, i, 1);
                        Date d = calendar.getTime();
                        @SuppressWarnings("deprecation")
                        Month currentMonth = new Month( curr_year + starting_year/*months[d.getMonth()] ,d.getMonth() + 1*/);
                        CalendarMonths.add(currentMonth);
                    }
                    // creates the days for each month


                    for (int i = 1; i <= num_of_days; i++) {
                        calendar.set(curr_year+ starting_year, 0, i);
                        Month currentMonth = CalendarMonths.get(calendar.get(calendar.MONTH) + curr_year*12);
                        Date current = calendar.getTime();
                        Day currentDay = new Day(calendar.get(calendar.DAY_OF_MONTH), current);
                        currentMonth.addDay(currentDay);
                    }
                }
                // saves the new calendar into the CalendarYears.dat file
                save();
            }
            catch(ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            primaryStage.setTitle("Calendar Jathavan");
            //final Group root = new Group();
            Scene scene = new Scene(root, 1100, 750);
            scene.setFill(Color.rgb(100,100,100) );
            // sets the information for the reminder Label
            Font reminderFont = Font.font("Ariel", 14);
            reminderLabel.setTranslateX(900);
            reminderLabel.setTranslateY(400);
            reminderLabel.setTextFill(Color.BLUE);
            reminderLabel.setFont(reminderFont);
            reminderLabel.setWrapText(true);


            root.getChildren().add(reminderLabel);

            //Sets the information for the ReminderListView
            final ListView<Reminder> reminderListView = new ListView<>(reminderList);
            reminderListView.setTranslateX(900);
            reminderListView.setTranslateY(100);
            reminderListView.setMaxWidth(100);
            reminderListView.setMaxHeight(250);
            root.getChildren().add(reminderListView);

            reminderListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Reminder>() {

                @Override
                public void changed(ObservableValue<? extends Reminder> observable, Reminder oldValue, Reminder newValue) {
                    Reminder setReminderLabel = reminderListView.getSelectionModel().getSelectedItem();
                    if(setReminderLabel != null)
                    {
                        reminderLabel.setText(setReminderLabel.getTime() + "\n" + setReminderLabel.getSortedMessage());
                    }
                }
            });

            Calendar c1 = Calendar.getInstance();
            // this integerProperty will hold the number of the month that is being show
            final IntegerProperty showMonth = new SimpleIntegerProperty();
            showMonth.setValue(c1.get(2));
            Font font2 = Font.font("Ariel",FontWeight.BOLD ,15);
            final Label monthLabel = new Label(months[showMonth.get()] );
            monthLabel.setTranslateX(170);
            monthLabel.setTranslateY(700);
            monthLabel.setFont(font2);
            monthLabel.setTextFill(Color.BLUE);
            root.getChildren().add(monthLabel);
            changeMonth(showMonth.get(),monthLabel);

            // the nextButton will show the next month in the calendar
            final Button nextButton = new Button();
            final Button prevButton = new Button();
            nextButton.setLayoutX(300);
            nextButton.setLayoutY(700);
            nextButton.setText("next");
            nextButton.setOnAction(
                    new EventHandler<ActionEvent>()
                    {
                        public void handle(ActionEvent event)
                        {
                            showMonth.set(showMonth.get() + 1);
                            changeMonth(showMonth.get(),monthLabel);
                            // this will disable the button if the last month is being shown
                            if(showMonth.get() == 143)
                            {
                                nextButton.setDisable(true);
                            }
                            // this will enable the prevButton if the first month is not show
                            if(showMonth.get() > 0)
                            {
                                prevButton.setDisable(false);
                            }
                        }
                    });
            root.getChildren().add(nextButton);
            // the prevButton will show the previous month in the calendar
            prevButton.setLayoutX(80);
            prevButton.setLayoutY(700);
            prevButton.setText("previous");
            prevButton.setOnAction(
                    new EventHandler<ActionEvent>()
                    {
                        public void handle(ActionEvent event)
                        {
                            showMonth.set(showMonth.get() - 1);
                            changeMonth(showMonth.get(), monthLabel);
                            // this will disable the button if the first month is being shown
                            if(showMonth.get() == 0)
                            {
                                prevButton.setDisable(true);
                            }
                            // this will enable the nextButton if the last mont is not show
                            if(showMonth.get() < 144)
                            {
                                nextButton.setDisable(false);
                            }

                        }
                    });
            root.getChildren().add(prevButton);


            // This is the clock that is constantly showing the current time
            DigitalClock clock1 = new DigitalClock();
            clock1.setTranslateY(10);
            clock1.setTranslateX(100);
            Font font1 =  Font.font("Ariel",FontWeight.BOLD ,25);
            clock1.setFont(font1);
            clock1.setTextFill(Color.BLUE);
            // the sunday label on top of the Calendar
            Label Sunday = new Label("Sunday");
            Sunday.setTranslateY(55);
            Sunday.setTranslateX(85);
            Sunday.setTextFill(Color.BLUE);
            Sunday.setFont(font2);
            root.getChildren().add(Sunday);
            // the monday label on top of the Calendar
            Label Monday = new Label("Monday");
            Monday.setTranslateY(55);
            Monday.setTranslateX(205);
            Monday.setTextFill(Color.BLUE);
            Monday.setFont(font2);
            root.getChildren().add(Monday);
            // the tuesday label on top of the Calendar
            Label Tuesday = new Label("Tuesday");
            Tuesday.setTranslateY(55);
            Tuesday.setTranslateX(325);
            Tuesday.setTextFill(Color.BLUE);
            Tuesday.setFont(font2);
            root.getChildren().add(Tuesday);
            // the wednesday label on top of the Calendar
            Label Wednesday = new Label("Wednesday");
            Wednesday.setTranslateY(55);
            Wednesday.setTranslateX(430);
            Wednesday.setTextFill(Color.BLUE);
            Wednesday.setFont(font2);
            root.getChildren().add(Wednesday);
            // the thursday label on top of the Calendar
            Label Thursday = new Label("Thursday");
            Thursday.setTranslateY(55);
            Thursday.setTranslateX(555);
            Thursday.setTextFill(Color.BLUE);
            Thursday.setFont(font2);
            root.getChildren().add(Thursday);
            // the friday label on top of the Calendar
            Label Friday = new Label("Friday");
            Friday.setTranslateY(55);
            Friday.setTranslateX(685);
            Friday.setTextFill(Color.BLUE);
            Friday.setFont(font2);
            root.getChildren().add(Friday);
            // the saturday label on top of the Calendar
            Label Saturday = new Label("Saturday");
            Saturday.setTranslateY(55);
            Saturday.setTranslateX(790);
            Saturday.setTextFill(Color.BLUE);
            Saturday.setFont(font2);
            root.getChildren().add(Saturday);
            //The add button that will open an addReminder window when clicked
            Button addReminder = new Button("Add");
            addReminder.setTranslateX(600);
            addReminder.setTranslateY(700);
            addReminder.setOnAction(
                    new EventHandler<ActionEvent>()
                    {
                        public void handle(ActionEvent event)
                        {
                            AddWindow addWindow = new AddWindow();
                            addWindow.display();
                            // this will reload the current month so that the new reminder shows
                            changeMonth(showMonth.get(),monthLabel);
                        }
                    });
            root.getChildren().add(addReminder);
            root.getChildren().add(clock1);
            //when the user clicked the red x button to close the program it will first save all the information first
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
            {

                public void handle(WindowEvent event) {
                    save();
                }
            });


            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch(Exception e)
        {
            // try catch in case something happens it is easy to find the problem
            System.out.println( "try catch start   " + e  +  "   " +  e.getStackTrace()[0].getLineNumber());
        }
    }



    public static void main(String[] args) {
        Application.launch(args);
    }
    // this method is used to save information into the CalendarYears
    public void save()
    {
        try
        {
            FileOutputStream fileOut = new FileOutputStream("CalendarYears.dat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(CalendarMonths);
            out.close();
            fileOut.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    // this method is called when the reminderListView needs to change the day that it is showing
    public void changeReminderList(Day thisDay)
    {
        // it will first empty the listView of all reminders from the previous selected day
        try{
            reminderList.removeAll(reminderList);
        }
        catch(Exception e)
        {
            System.out.println("change:  " + e);
        }
        // then it will add all the reminders into the listView
        for(int i = 0; i < thisDay.getReminderSize();i++)
        {
            reminderList.add(thisDay.getOneReminder(i));
        }
    }
    // this method is called to switch what month is being shown on screen
    public void changeMonth(int showMonth, Label monthLabel)
    {
        Month currentMonth = CalendarMonths.get(showMonth);
        int currentNumber = 0;
        monthLabel.setText(months[showMonth% 12] + " "+ CalendarMonths.get(showMonth).getYear());
        boolean maxDays = true;
        // the calendar will show 6 weeks and 7 days per week
        for(int i = 0; i < 6 ; i++)
        {
            for(int j = 0 ; j < 7 ; j++)
            {
                Day currentDay = currentMonth.getDay(currentNumber);
                dayBox box;
                // this will check if the first of the month is the same day_of_week of the day that is being made right now. for example
                // the first day of June starts on wednseday so the first four boxes should be blank and then the actual days of the month
                // will be made
                if(currentDay.getDayofWeek() - 1 == j && maxDays)
                {
                    box = new dayBox(j *120 + 50  ,i*100 + 80 ,120,100,currentDay);
                    if(currentNumber < currentMonth.daysSize() - 1)
                    {
                        currentNumber++;

                    }
                    // this checks to see if all the days of the month have been completed. if so the rest of the boxes will be blank
                    else
                    {
                        maxDays = false;
                    }
                }
                // creates a blank box if this box should not hold a day
                else
                {
                    box = new dayBox(j *120 + 50  ,i*100 + 80 ,120,100,null);

                }
                root.getChildren().add(box);
                // if the box does is blank than don't print out the day number since it doesn't have on
                if(box.checkDay() != null)
                {
                    root.getChildren().add(box.getDayLabel());
                }
                //
                ArrayList<ReminderButton> buttons = box.getButtons();
                for(int k = 0; k < buttons.size(); k++)
                {
                    root.getChildren().add(buttons.get(k));
                }

            }
        }
    }
    //a reminder button is a button with a reminder attached to it and when click will set the reminder label to the reminder's message
    class ReminderButton extends Button
    {
        Reminder reminder;
        public ReminderButton(double x, double y, Reminder r, boolean overFlow)
        {
            reminder = r;
            this.setLayoutX(x);
            this.setLayoutY(y);
            // if the day that the reminder is in has 5 or more days that the 4th one will show a "..." to show that there are more than 4 reminders
            if(overFlow)
            {
                this.setText("...");
            }
            else
            {
                this.setText( reminder.getMessage());
            }

            this.checkClick();
        }
        // checks to see if the button is clicked. if it is than set the reminderLabel to this reminder's message
        public void checkClick()
        {
            this.setOnAction(
                    new EventHandler<ActionEvent>()
                    {
                        public void handle(ActionEvent event)
                        {
                            try
                            {
                                reminderLabel.setText( reminder.getTime() + "\n" +reminder.getSortedMessage());
                                changeReminderList(reminder.getDay());
                            }catch(Exception e)
                            {
                                System.out.println("Button:   " +e);
                            }
                        }
                    });
        }
    }
    // this window will be used to add new reminders
    public class AddWindow {
        //the is called to see what option the user chose for the monthChoiceBox
        public int checkMonth(ChoiceBox<String> monthChoiceBox)
        {
            for(int i = 0; i < 10; i++)
            {
                if(monthChoiceBox.getValue().equals(months[i]))
                {
                    return i;
                }
            }
            return 11;

        }
        // changes how many days the dayChoiceBox shows.
        public void changeDayChoiceBox(ChoiceBox<String> monthChoiceBox,ChoiceBox<Integer> dayChoiceBox, int yearOfChoiceBox)
        {
            int month_of_year = checkMonth(monthChoiceBox);
            int month_in_list = month_of_year + (yearOfChoiceBox - 2019)*12;
            dayChoiceBox.getItems().removeAll(dayChoiceBox.getItems());
            for(int i = 0; i < CalendarMonths.get(month_in_list).daysSize() ;i++)
            {
                dayChoiceBox.getItems().add(i + 1);
            }

        }
        // displays the second window for creating reminders
        public void display()
        {
            int start_year = 2019;
            final Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Add new Reminder");
            final Group layout = new Group();
            Scene scene = new Scene(layout,300,300);
            Font addFont = Font.font("Ariel", 12);
            //label for the word Month
            Label yearLabel = new Label("Year:");
            yearLabel.setFont(addFont);
            yearLabel.setTextFill(Color.BLUE);
            ChoiceBox<Integer> yearChoiceBox = new ChoiceBox<Integer>();
            yearChoiceBox.setTranslateX(60);
            for(int i = 0; i <= 11; i++)
            {
                yearChoiceBox.getItems().add(i+start_year);
            }
            yearChoiceBox.setValue(yearChoiceBox.getItems().get(0));
            Label monthLabel = new Label("Month:");
            monthLabel.setFont(addFont);
            monthLabel.setTextFill(Color.BLUE);
            monthLabel.setTranslateY(25);
            //This choiceBox will have all the months of the year
            ChoiceBox<String> monthChoiceBox= new ChoiceBox<String>();
            monthChoiceBox.setTranslateX(60);
            monthChoiceBox.setTranslateY(25);
            for(int i = 0;i < 12;i++)
            {
                monthChoiceBox.getItems().add(months[i]);
            }
            monthChoiceBox.setValue(monthChoiceBox.getItems().get(0));
            //label for the word Day
            Label dayLabel = new Label("Day:");
            dayLabel.setTranslateY(50);
            dayLabel.setFont(addFont);
            dayLabel.setTextFill(Color.BLUE);
            // this choiceBox will allow the user to select the day
            ChoiceBox<Integer> dayChoiceBox = new ChoiceBox<Integer>();
            dayChoiceBox.setTranslateX(60);
            dayChoiceBox.setTranslateY(50);

            for(int i = 0; i < 31;i++)
            {
                dayChoiceBox.getItems().add(i + 1);
            }
            // Depending on what month is chosen in the monthChoice box the dayChoiceBox will will have a certain number of options
            // For example if the user selects january for the month the day choice box will give 31 different options for the day. If the user
            // selects February for the month the user will have 28 options for the days(or 29 days for leap years)
            dayChoiceBox.setValue(dayChoiceBox.getItems().get(0));
            // this listener will listen for when the monthchoiceBox Changes
            yearChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    changeDayChoiceBox(monthChoiceBox,dayChoiceBox,yearChoiceBox.getValue());
                }
            });
            monthChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    //changes the number of days in the dayChoiceBox
                    changeDayChoiceBox(monthChoiceBox,dayChoiceBox,yearChoiceBox.getValue());

                }
            });

            Label hourLabel = new Label("Hour:");
            hourLabel.setTranslateY(75);
            hourLabel.setFont(addFont);
            hourLabel.setTextFill(Color.BLUE);
            // this choice box will set the hour of the reminder
            ChoiceBox<Integer> hourChoiceBox = new ChoiceBox<Integer>();
            hourChoiceBox.setTranslateX(60);
            hourChoiceBox.setTranslateY(75);
            for(int i = 0;i < 12;i++)
            {
                hourChoiceBox.getItems().add(i+ 1);
            }
            hourChoiceBox.setValue(hourChoiceBox.getItems().get(0));
            //this choiceBox will set if hour is am or pm
            ChoiceBox<String> amChoiceBox = new ChoiceBox<String>();
            amChoiceBox.setTranslateX(106);
            amChoiceBox.setTranslateY(75);
            amChoiceBox.getItems().add("AM");
            amChoiceBox.setValue(amChoiceBox.getItems().get(0));
            amChoiceBox.getItems().add("PM");
            //label for the word minute
            Label minuteLabel = new Label("Minute:");
            minuteLabel.setFont(addFont);
            minuteLabel.setTextFill(Color.BLUE);

            ChoiceBox<Integer> minuteChoiceBox = new ChoiceBox<Integer>();
            minuteChoiceBox.setTranslateY(100);
            minuteChoiceBox.setTranslateX(60);
            for(int i = 0; i < 12; i++)
            {
                minuteChoiceBox.getItems().add(i*5);
            }
            minuteChoiceBox.setValue(minuteChoiceBox.getItems().get(0));
            minuteLabel.setTranslateY(100);
            Label messageLabel = new Label("Message:");
            messageLabel.setFont(addFont);
            messageLabel.setTextFill(Color.BLUE);
            // this textfield will set the message for the reminder
            TextField messageField = new TextField();
            messageField.setTranslateY(125);
            messageField.setTranslateX(60);
            messageLabel.setTranslateY(125);
            final StringProperty minuteString = new SimpleStringProperty();
            final StringProperty messageString = new SimpleStringProperty();

            messageString.bind(messageField.textProperty());
            // this label will pop up if the user does something wrong l
            final Label errorLabel = new Label();
            errorLabel.setTranslateX(50);
            errorLabel.setTranslateY(250);
            errorLabel.setTextFill(Color.RED);

            final IntegerProperty minuteInt = new SimpleIntegerProperty();
            // this button will create the reminder from the information that is filled in
            Button addButton = new Button("Add");
            addButton.setTranslateX(100);
            addButton.setTranslateY(200);
            addButton.setOnAction(
                    new EventHandler<ActionEvent>()
                    {
                        public void handle(ActionEvent event)
                        {
                            int hour = hourChoiceBox.getValue();
                            //if the user put 12 for the hour than it will change it to 0 since that is the time works
                            if(hourChoiceBox.getValue() == 12)
                            {
                                hour = 0;
                            }
                            // if the amchoicebox equals pm than add an additional 12 hours to make it pm
                            if(amChoiceBox.getValue().equals("PM"))
                            {
                                hour+= 12;
                            }
                            try
                            {
                                //this will create the reminder and close the window
                                Month currentMonth = CalendarMonths.get(checkMonth(monthChoiceBox) + (yearChoiceBox.getValue() - 2019)*12);
                                Day currentDay = currentMonth.getDay(dayChoiceBox.getValue() - 1);
                                Calendar addCalendar = Calendar.getInstance();
                                addCalendar.set(yearChoiceBox.getValue(), checkMonth(monthChoiceBox) ,dayChoiceBox.getValue() - 1 , hour ,minuteChoiceBox.getValue() );
                                Date addDate = addCalendar.getTime();
                                currentDay.addReminder(messageString.get(),addDate);
                                changeReminderList(currentDay);
                                window.close();

                            }
                            //if the user does not give and integer for the minutechoicebox it will set the errorLabel to tell the user their mistake
                            catch(Exception e)
                            {

                                errorLabel.setText("The Minute has to be an Integer");
                            }

                        }
                    });
            // adds all the choiceboxes and labels into the layout
            layout.getChildren().add(addButton);
            layout.getChildren().add(yearChoiceBox);
            layout.getChildren().add(monthChoiceBox);
            layout.getChildren().add(dayChoiceBox);
            layout.getChildren().add(hourChoiceBox);
            layout.getChildren().add(amChoiceBox);
            //layout.getChildren().add(minuteField);
            layout.getChildren().add(minuteChoiceBox);
            layout.getChildren().add(messageField);
            layout.getChildren().add(errorLabel);
            layout.getChildren().add(yearLabel);
            layout.getChildren().add(monthLabel);
            layout.getChildren().add(dayLabel);
            layout.getChildren().add(hourLabel);
            layout.getChildren().add(minuteLabel);
            layout.getChildren().add(messageLabel);
            window.setScene(scene);
            // this will stop the user from going back to the calendar unless the add a reminder or
            window.showAndWait();
        }

    }
    // a daybox will show a rectangle will the number of the day and any reminders that it has.
    //multiple dayboxes are created in order to create and enitre month for the calendar
    class dayBox extends Rectangle
    {
        Day boxDay;
        Label dayLabel;
        ArrayList<ReminderButton> buttons;
        public dayBox(double x, double y, double width, double height , Day d)
        {
            boxDay = d;
            buttons = new ArrayList<ReminderButton>();
            this.setFill(Color.LIGHTGREY);
            this.setStroke(Color.BLACK);
            this.setStrokeWidth(2);
            this.setX(x);
            this.setY(y);
            this.setWidth(width);
            this.setHeight(height);
            // if the box is not blank that show the day number and the
            if(boxDay != null)
            {
                dayLabel = new Label(boxDay.getDayNumber() + "");
                if(boxDay.getDayNumber() < 10)
                {
                    dayLabel.setTranslateX(x + 100);
                }
                else
                {
                    dayLabel.setTranslateX(x + 90);
                }
                dayLabel.setTranslateY(y);
                Font font = Font.font("Ariel", 18);
                dayLabel.setFont(font);
                dayLabel.setTextFill(Color.BLUE);
                Font font2 = Font.font("Ariel", 9);
                //the max variable helps to see if the number of is greater than 4 since that is the maximum amount of buttons a dayBox can hold
                int max = boxDay.getReminderSize();
                if(max > 4)
                {
                    max = 4;
                }
                for(int i = 0; i <  max;i++ )
                {
                    ReminderButton b;
                    // checks to see if the day has 5 or more reminders since the box can only show 4 reminders
                    if(i == 3 && boxDay.getReminderSize() > 4)
                    {
                        b = new ReminderButton(x + 10, y + 10 + (22*i),boxDay.getOneReminder(i),true);
                    }
                    else
                    {
                        b = new ReminderButton(x + 10, y + 10 + (22*i),boxDay.getOneReminder(i),false);
                    }
                    b.setMinWidth(75);
                    b.setMaxWidth(75);
                    b.setFont(font2);
                    buttons.add(b);
                }
            }
        }
        // reutrns the dayLable
        public Label getDayLabel()
        {
            return dayLabel;
        }
        //returns the boxDay
        public Day checkDay()
        {
            return boxDay;
        }
        //returns the buttons arraylist
        public ArrayList<ReminderButton> getButtons()
        {
            return buttons;
        }

    }
    // this clock will show the current time and date and will update every second
    class DigitalClock extends Label {
        public DigitalClock() {
            bindToTime();
        }

        // the digital clock updates once a second.
        private void bindToTime() {

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new EventHandler<ActionEvent>() {
                                @Override public void handle(ActionEvent actionEvent) {
                                    // organizes how the digital clock will show
                                    Calendar time = Calendar.getInstance();
                                    // the rest of this organizes what the label will show
                                    String hour = "" + time.get(10);
                                    String minute = "" + time.get(12);
                                    String second = "" + time.get(time.SECOND);
                                    String am = "" ;
                                    if(time.get(10) == 0)
                                    {
                                        hour = "12";
                                    }
                                    if(time.get(10) < 10 && time.get(10) != 0)
                                    {
                                        hour = "0" + hour;
                                    }
                                    if(time.get(12) < 10)
                                    {
                                        minute = "0" + minute;
                                    }
                                    if(time.get(time.SECOND) < 10)
                                    {
                                        second = "0" + second;
                                    }
                                    if(time.get(time.HOUR_OF_DAY) < 12)
                                    {
                                        am = "AM";
                                    }
                                    else
                                    {
                                        am = "PM";
                                    }
                                    setText( DaysOfWeek[time.get(time.DAY_OF_WEEK) - 1] + ", " + months[time.get(2)] + "  "   + time.get(time.DAY_OF_MONTH) + "  " + time.get(1) +
                                            "       "
                                            + hour + ":" + minute+ ":" + second  + " " + am);

                                }
                            }
                    ),
                    new KeyFrame(Duration.seconds(1))
            );
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }


    }


}