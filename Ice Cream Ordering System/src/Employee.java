public class Employee {
    private final int employeeID;
    private final String employeeName;
    private final String employeeRole;

    public Employee(int employeeID, String employeeName, String employeeRole) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.employeeRole = employeeRole;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    @Override
    public String toString() {
        return employeeName + " (" + employeeRole + ")";
    }
}