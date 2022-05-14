package com.hcl.javajdbcjpa.jpa;

import java.util.List;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;



public class UserJpa {

		public static void main(String[] args) {

			EntityManagerFactory emf = null;
			
			EntityManager entityManager = null;
		
			EntityTransaction transaction = null;

			try {
				emf = Persistence.createEntityManagerFactory("jbd-pu");
				
				entityManager = emf.createEntityManager();
				
				transaction = entityManager.getTransaction();
			
				
				try (Scanner in = new Scanner(System.in);) {
					
					boolean menu = true;
					
					while (menu) {
						System.out.println("Type which selection you would like to do: Insert, Update, Delete, Read, ReadAll, or Quit?");
						String option = in.nextLine();
						
						switch(option.toLowerCase()) {
						
						case "insert":
							transaction.begin();
							Student me = addStudent(in);
							entityManager.persist(me);
							transaction.commit();
							break;
							
						case "update":	
							transaction.begin();
							int idU = inputID(in);
							Student stu = entityManager.find(Student.class, idU);
							stu = updateUser(in, stu);
							transaction.commit();
							break;
						case "delete":
							transaction.begin();
							int idD = inputID(in);
							Query del = entityManager.createQuery("delete from Stu s where s.ID=?0");
							del.setParameter(0, idD);
							del.executeUpdate();
							transaction.commit();
							break;
						case "read":
							long idR = inputID(in);
							Query read = entityManager.createQuery("select s from Stu s where s.studentId=?0");
							read.setParameter(0, idR);
							List<Student> resultList = read.getResultList();
							System.out.println("num of users:" + resultList.size());
							for (Student next : resultList) {
								System.out.println("next user: " + next);
							}
							break;
						case "readall":
							Query readAll = entityManager.createQuery("select s from Stu s");

							List<Student> resultListAll = readAll.getResultList();
							System.out.println("num of students:" + resultListAll.size());
							for (Student next : resultListAll) {
								System.out.println("next student: " + next);
							}
							break;
						case "quit":
							
							break;
						default:
							System.out.println("Invalid option");
							break;
						}
						menu =	menuOperator(in, menu, "Would you like to do anything else? ");
					}
				}
			} catch (Exception e) {
				System.out.println(e);
				if (transaction != null) {
					transaction.rollback();
				}
			} finally {
				if (entityManager != null) {
					entityManager.close();
				}
				if (emf != null) {
					emf.close();
				}
			}
		}
		
		private static int inputID(Scanner input) {
			System.out.println("Enter the ID");
			int id = input.nextInt();
			return id;
		}

		public static Student addStudent(Scanner input) {
			
			System.out.println("Enter first name");
			String fn = input.nextLine();
			System.out.println("Enter last name");
			String ln = input.nextLine();
			System.out.println("Enter phone number");
			String num = input.nextLine();
			
			
			
			Student stu = new Student();
			
			stu.setFirstName(fn);
			stu.setLastName(ln);
			stu.setContactNo(num);
			
			
			return stu; 
			
		}
		
		public static Student updateUser(Scanner input, Student stu) {
			
			System.out.println("Enter first name");
			String fn = input.nextLine();
			System.out.println("Enter last name");
			String ln = input.nextLine();
			System.out.println("Enter phone number");
			String num = input.nextLine();
			
			
			
			
			stu.setFirstName(fn);
			stu.setLastName(ln);
			stu.setContactNo(num);
			
			
			return stu;  
			
		}
		
		private static boolean menuOperator(Scanner input, boolean go, String message) {
			
			System.out.println(message);
			boolean gogo = true;
			while (gogo) {
				String repeat = input.nextLine();
				if (repeat.toUpperCase().equals("Y")) {
					gogo = false;
				} else if (repeat.toUpperCase().equals("N")) {
					go = false;
					gogo = false;
				} else {
					System.out.println("invalid input, try again. input Y or N: ");
				}
			}
			return go;
		}

	}
