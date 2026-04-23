// Employee class represents an instance of employee in the system
// It stores ID, name, and role as either worker or manager.
public class Employee {
    private final int employeeID; // Key identifier
    private final String employeeName;
    private final employeeRoles employeeRole;

    public Employee(int employeeID, String employeeName, employeeRoles employeeRole) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.employeeRole = employeeRole;
    }

    //Getter Methods to return Employee fields
    public int getEmployeeID() {
        return employeeID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public employeeRoles getEmployeeRole() {
        return employeeRole;
    }

    // Override default toString() mthod
    @Override
    public String toString() {
        return employeeName + " (" + employeeRole + ")";
    }
}
