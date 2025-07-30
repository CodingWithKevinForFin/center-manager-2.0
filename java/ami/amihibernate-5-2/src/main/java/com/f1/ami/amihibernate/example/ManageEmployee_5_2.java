package com.f1.ami.amihibernate.example;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ManageEmployee_5_2 {
	private static SessionFactory factory;

	public static void main(String[] args) {

		//		File configFile = new File("src/main/resources/example/mysql.hibernate.cfg.xml");
		File configFile = new File("src/main/resources/example/hibernate.cfg.xml");
		try {
			// 1) XML
			//			factory = new Configuration().configure(configFile).buildSessionFactory();
			// 2) Annotated
			factory = new Configuration().configure(configFile).addAnnotatedClass(EmployeeTask.class).addAnnotatedClass(Employee.class).buildSessionFactory();

		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

		ManageEmployee_5_2 ME = new ManageEmployee_5_2();

		/* Add few employee records in database */
		Number empID1 = ME.addEmployee("Zara", "Ali", 1000);
		Number empID2 = ME.addEmployee("Daisy", "Das", 5000);
		Number empID3 = ME.addEmployee("John", "Paul", 10000);

		/* List down all the employees */
		ME.listEmployees();

		/* Update employee's records */
		// 		ME.updateEmployee(empID1, 5000);
		//		ME.updateEmployee(1, 5000);

		/* Delete an employee from the database */
		//		ME.deleteEmployee(empID2);
		//		ME.deleteEmployee(2);

		/* List down new list of the employees */
		ME.listEmployees();

	}

	/* Method to CREATE an employee in the database */
	public Number addEmployee(String fname, String lname, int salary) {
		Session session = factory.openSession();
		Transaction tx = null;
		Number employeeID = null;

		try {
			tx = session.beginTransaction();
			Employee employee = new Employee(fname, lname, salary);
			employeeID = (Long) session.save(employee);
			tx.commit();
		} catch (HibernateException e) {
			//			if (tx != null)
			//				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return employeeID;
	}

	/* Method to  READ all the employees */
	public void listEmployees() {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			List employees = session.createQuery("FROM Employee").list();
			for (Iterator iterator = employees.iterator(); iterator.hasNext();) {
				Employee employee = (Employee) iterator.next();
				System.out.print("First Name: " + employee.getFirstName());
				System.out.print("  Last Name: " + employee.getLastName());
				System.out.println("  Salary: " + employee.getSalary());
			}
			tx.commit();
		} catch (HibernateException e) {
			//			if (tx != null)
			//				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	/* Method to UPDATE salary for an employee */
	public void updateEmployee(Number EmployeeID, int salary) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			Employee employee = (Employee) session.get(Employee.class, EmployeeID);
			employee.setSalary(salary);
			session.update(employee);
			tx.commit();
		} catch (HibernateException e) {
			//			if (tx != null)
			//				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	/* Method to DELETE an employee from the records */
	public void deleteEmployee(Number EmployeeID) {
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			Employee employee = (Employee) session.get(Employee.class, EmployeeID);
			session.delete(employee);
			tx.commit();
		} catch (HibernateException e) {
			//			if (tx != null)
			//				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
}