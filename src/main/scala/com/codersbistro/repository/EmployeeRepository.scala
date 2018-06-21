package com.codersbistro.repository

import scala.concurrent.Future

object EmployeeRepository {

  case class Employee(id: String,
                      firstName: String,
                      lastName: String,
                      active: Boolean,
                      address: Address)

  case class Address(line1: String,
                     line2: String,
                     city: String,
                     state: String,
                     zipCode: String)

  val EmployeeDB = Seq(
    Employee("100", "Lala", "Lee", true, Address("2234", "Cambridge Street", "Torronto", "Torronto", "2414132")),
    Employee("101", "Nancy", "Argan", true, Address("35", "Waterloo Park", "Niaora", "Nigeria", "546465")),
    Employee("102", "Manilla", "Neptune", true, Address("22", "Bakers Street", "Gurgaon", "Haryana", "21341324")),
    Employee("103", "Neeru", "Andrew", true, Address("3463", "St. Peters Road", "Rocky Hill", "Connecticut", "456546")),
    Employee("104", "Michael", "Nicholas", true, Address("56756", "Aurbhindo Marg", "Minnesota", "Minnetonka", "45242")),
    Employee("105", "Sam", "Montroe", true, Address("12312", "Ethens Street", "Delhi", "Delhi", "235353")),
    Employee("106", "Mila", "Hanson", true, Address("432", "Bridge Road", "San jose", "San Fransico", "3434534")),
    Employee("106", "Manila", "Winston", false, Address("432", "Bridge Road", "San jose", "San Fransico", "3434534"))
  )

  class EmployeeNotFoundException extends Throwable("No employee found in the database.")

  class DubiousEmployeeRecordsException extends Throwable("Dubious Employee records found given the Employee ID.")

}

trait EmployeeRepository {

  import EmployeeRepository._
  import akka.pattern.after
  import scala.concurrent.duration._
  import com.codersbistro.repository.RepositoryContext._

  /**
    * Fetch the employee records with a mocked delay to synthesize transaction delays.
    */
  def fetchDBWithDelay(): Future[Seq[Employee]] = {
    val randomDuration = (Math.random() * 5 + 3).toInt.seconds
    after(randomDuration, scheduler) {
      Future {
        EmployeeDB
      }
    }
  }

  /**
    * Get the employee details given the Employee Id.
    *
    * @param id
    * @return
    */
  def getEmployeeById(id: String) = fetchDBWithDelay().map { db =>
    val data = db.filter(_.id == id)
    if (data.isEmpty)
      throw new EmployeeNotFoundException
    else if (data.length > 1)
      throw new DubiousEmployeeRecordsException
    else
      data(0)
  }

  /**
    * Query the employee repository with the given query condition.
    *
    * @param id
    * @param firstName
    * @param lastName
    * @return
    */
  def queryEmployee(id: String, firstName: String, lastName: String): Future[Seq[Employee]] = {
    fetchDBWithDelay().map { db =>
      db.filter { elem =>
        elem.id == id && elem.firstName == firstName && elem.lastName == lastName
      }
    }
  }
}
