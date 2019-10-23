/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turnConTest.com.turn;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author nhuytan
 */
// Java program for implementation of Bubble Sort 
public class BubbleSort {

    public void bubbleSortTime(ArrayList<Employee> employee) {
        int n = employee.size();
       // System.out.println("BEFORE bubbleSortTime:");
        //printEmployeeName(employee);
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (employee.get(j).getCheckInTime().isAfter(employee.get(j + 1).getCheckInTime())) {
                   // System.out.print("<<IS_AFTER>>");
                    Collections.swap(employee, j, j + 1);
                }
            }
           // System.out.println("bubbleSortTime-SORT ROUND " + (i + 1));
        }
       // System.out.println("AFTER bubbleSortTime:");
       // printEmployeeName(employee);
    }

    public void bubbleSortTotal(ArrayList<Employee> employee) {
        int n = employee.size();
       // System.out.println("BEFORE bubbleSortTotal:");
       // printEmployeeName(employee);
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (employee.get(j).getTotal() > employee.get(j + 1).getTotal()) {
                   // System.out.print("<<Larger>>");
                    Collections.swap(employee, j, j + 1);
                } else if (employee.get(j).getTotal() == employee.get(j + 1).getTotal()) {
                    if (employee.get(j).getCheckInTime().isAfter(employee.get(j + 1).getCheckInTime())) {
                     //   System.out.print("<<Is_After>>");
                        Collections.swap(employee, j, j + 1);
                    }
                }
            }
           // System.out.println("bubbleSortTotal-SORT ROUND " + (i + 1));
        }
       // System.out.println("AFTER bubbleSortTotal:");
        //printEmployeeName(employee);
    }

    public void bubbleSortTotalTurn(ArrayList<Employee> employee) {
        int n = employee.size();
        //System.out.println("BEFORE bubbleSortTotalTurn:");
       // printEmployeeName(employee);
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (employee.get(j).getTotalTurn() > employee.get(j + 1).getTotalTurn()) {
                   // System.out.print("<<Larger>>");
                    Collections.swap(employee, j, j + 1);
                } else if (employee.get(j).getTotalTurn() == employee.get(j + 1).getTotalTurn()) {
                    if (employee.get(j).getCheckInTime().isAfter(employee.get(j + 1).getCheckInTime())) {
                        Collections.swap(employee, j, j + 1);
                    }
                }
            }
           // System.out.println("bubbleSortTotalTurn-SORT ROUND " + (i + 1));
           // printEmployeeList(employee);
        }
       // System.out.println("AFTER bubbleSortTotalTurn:");
       // printEmployeeName(employee);
    }

    public static void printEmployeeList(ArrayList<Employee> e) {
        for (int i = 0; i < e.size(); i++) {
            System.out.println("Employee " + e.get(i).getEmpName() + " ----Total Turn " + e.get(i).getTotalTurn());
        }
    }

    public static void printEmployeeName(ArrayList<Employee> e) {
        for (int i = 0; i < e.size(); i++) {
            System.out.println(e.get(i).getEmpName() + "\t" + e.get(i).getTotalTurn() + "\t"
                    + e.get(i).getCheckInTime().toString());
        }
    }
}
