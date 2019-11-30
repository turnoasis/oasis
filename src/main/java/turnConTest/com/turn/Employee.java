/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turnConTest.com.turn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author nhuytan
 * edit 1
 */
public class Employee {

    public String EmployeeID;
    public String EmpName;
    LocalDateTime  CheckInTime;
   // public ArrayList<String> turnList;
    double totalTurn, Total;
    int position;
    boolean active;
    boolean isWorking;
    int indexGroup;
    ArrayList<WorkHis> turnListD;
    LocalDateTime lstTime;
    public String pass;

    Employee() {
    }

    public int getIndexGroup() {
        return indexGroup;
    }

    /**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @param pass the pass to set
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @return the lstTime
	 */
	public LocalDateTime getLstTime() {
		return lstTime;
	}

	/**
	 * @param lstTime the lstTime to set
	 */
	public void setLstTime(LocalDateTime lstTime) {
		this.lstTime = lstTime;
	}

	public void setIndexGroup(int indexGroup) {
        this.indexGroup = indexGroup;
    }

    public boolean isIsWorking() {
        return isWorking;
    }

    public void setIsWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
  

    public String getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(String EmployeeID) {
        this.EmployeeID = EmployeeID;
    }

    public String getEmpName() {
        return EmpName;
    }

    public void setEmpName(String EmpName) {
        this.EmpName = EmpName;
    }

    public LocalDateTime  getCheckInTime() {
        return CheckInTime;
    }

    /**
     *
     * @param CheckInTime
     */
    public void setCheckInTime(LocalDateTime  CheckInTime) {
        this.CheckInTime = CheckInTime;
    }

    public Employee(String EmployeeID, String EmpName, LocalDateTime  CheckInTime, String pass) {
        this.EmployeeID = EmployeeID;
        this.EmpName = EmpName.trim();
        this.CheckInTime = CheckInTime;
        this.lstTime = null;
        this.Total=0;
        this.totalTurn=0;
        this.pass = pass;
      //  this.turnList = new ArrayList<>();
        this.active=true;
        this.isWorking = false;
        this.position= Integer.parseInt(EmployeeID);
        this.turnListD = new ArrayList<WorkHis>();
    }

  /*  public ArrayList<String> getTurnList() {
		return turnList;
	}

	public void setTurnList(ArrayList<String> turnList) {
		this.turnList = turnList;
	}*/

	public ArrayList<WorkHis> getTurnListD() {
		return turnListD;
	}

	public void setTurnListD(ArrayList<WorkHis> turnListD) {
		this.turnListD = turnListD;
	}

	public double getTotalTurn() {
        return totalTurn;
    }

    public void setTotalTurn(double TotalTurn) {
        this.totalTurn = TotalTurn;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double Total) {
        this.Total = Total;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
