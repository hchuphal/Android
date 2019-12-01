package com.example.soc_macmini_15.sqlitepractice.Model;

public class Employee {

    private long staffId;
    private String firstName;
    private String lastName;
    private String gender;
    private String taskScheduleDate;
    private String task;
    private String status;
    private String timeToDestination;
    private String destination;
    public Employee() {
    }

    public Employee(long staffId, String firstName, String lastName, String gender, String status, String taskScheduleDate, String task, String timeToDestination, String destination) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.taskScheduleDate = taskScheduleDate;
        this.status = status;
        this.task = task;
        this.timeToDestination = timeToDestination;
        this.destination = destination;
    }

    public long getEmpId() {
        return staffId;
    }

    public void setEmpId(long staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String gettta() {
        return timeToDestination;
    }

    public void settta(String timeToDestination) {
        this.timeToDestination = timeToDestination;
    }


    public String getdestination() {
        return destination;
    }

    public void setdestination(String destination) {
        this.destination = destination;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHireDate() {
        return taskScheduleDate;
    }

    public void setHireDate(String taskScheduleDate) {
        this.taskScheduleDate = taskScheduleDate;
    }

    public String getTask() {
        return task;
    }

    public String getStatus() {
        return status;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return "Staff id: " + getEmpId() + "\n" +
                "Name: " + getFirstName()+ " " + getLastName() +
                " \n"+"Gender: "+getGender()
                + "\n" + "Task Schedule: (9AM-5PM) " + getHireDate()
                +"\n" + "Assigned Task: " + getTask()
                + "\n" + "Availibility  Status: " + getStatus()
                +"\n" +"Time to Arrive: "+gettta()
                +"\n" +"Destination: "+getdestination();
    }

   /* EmployeeDBHandler.COLUMN_ID,
    EmployeeDBHandler.COLUMN_FIRST_NAME,
    EmployeeDBHandler.COLUMN_LAST_NAME,
    EmployeeDBHandler.COLUMN_GENDER,
    EmployeeDBHandler.COLUMN_HIRE_DATE,
    EmployeeDBHandler.COLUMN_DEPT,
    EmployeeDBHandler.COLUMN_STATUS,
    EmployeeDBHandler.COLUMN_TTA,
    EmployeeDBHandler.COLUMN_DESTINATION

                    cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8));
    */
    public String toJson() {
        return "[{"+ "\n"+
                 "id: " + getEmpId() + "\n"
                + "name: " + getFirstName()+ " " + getLastName() + " \n"
                + "gender: "+getGender() +"\n"
                + "task: " + getTask() + "\n"
                + "task_schedule: " + getHireDate() + "\n"
                + "availibility_status: " + getStatus()+ "\n"
                +"destination: "+getdestination() +"\n"
                +"time_to_arrive: "+gettta() +"\n"
                +"]";
    }

/* Sample example
[{
  "id": 1,
  "name": "Staff_Name1",
  "task_description": "Clean_Ride",
  "destination": "Ride",
  "time_to_arrive": 5,
  "task_date":"2019-04-10"
},
{
  "id": 2,
  "name": "Staff_Name2",
  "task_description": "Clean_Ride",
  "destination": "Ride",
  "time_to_arrive": 10,
  "task_date":"2019-04-10"
}
]
 */



}
