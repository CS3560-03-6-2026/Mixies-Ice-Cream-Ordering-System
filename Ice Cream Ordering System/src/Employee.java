public class Employee {
    private final int employeeID;
    private final String employeeName;
    private final employeeRoles employeeRole;

    public Employee(int employeeID, String employeeName, employeeRoles employeeRole) {
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

    public employeeRoles getEmployeeRole() {
        return employeeRole;
    }

    @Override
    public String toString() {
        return employeeName + " (" + employeeRole + ")";
    }
}