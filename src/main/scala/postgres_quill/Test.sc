

/**
  * Note that you'll need "Plain" worksheet mode in IDEA, "Make project before run" for each time
  * when you change something outside of worksheet and finally
  * disable "Run worksheet in compiler process" in Settings/Languages & Frameworks/Scala/Worksheet/
  *
  * to make worksheet work
  *
  * Sometimes switching to REPL and back helps too...
  */
import postgres_quill.DatabaseEnvironment._

import cats.syntax.traverse._
import cats.instances.list._
import cats.syntax.flatMap._

import doobie.implicits._
import doobie.postgres.implicits._
import doobie.quill.DoobieContext

// doobie-postgres context for quill

val dc = new DoobieContext.Postgres(io.getquill.Literal) // naming scheme like Person -> person

import dc._ // methods for doobie-postgres

// case for test table
// in Quill table and case must have one name: https://getquill.io/#contexts-sql-contexts-naming-strategy
case class Person(id: Int, name: String, age: Int)

def createTable = {

    val drop =
      sql"""
     DROP TABLE IF EXISTS person
    """.update.run

    val create =
      sql"""
      CREATE TABLE person (
        id   SERIAL,
        name VARCHAR NOT NULL,
        age  SMALLINT
      )
    """.update.run

    (drop >> create).transact(xa)
  }

def insertTestData = {

    // id in table is serial so we will not use ids from this list
    val persons: List[Person] = List(
      Person(1, "Jesh", 26),
      Person(1, "Sarah", 21),
      Person(1, "Mikhael", 54),
      Person(1, "Lena", 12),
      Person(1, "Abdul", 34),
      Person(1, "Stepan", 88),
      Person(1, "Roman", 10),
      Person(1, "Sasha", 15)
    )

    val inserts: doobie.ConnectionIO[List[Long]] = {
      for {
        p <- persons
      } yield run(
        query[Person].insert(_.name -> lift(p.name), _.age -> lift(p.age))
      )
    }.sequence

    inserts.transact(xa)
  }

/** Disable it after first table creation to just test quill and everything... */
//createTable.unsafeRunSync
//insertTestData.unsafeRunSync

// take oldest person

run {
    query[Person].sortBy(_.age)(Ord.descNullsLast).take(1)
  }.transact(xa).unsafeRunSync.headOption // Some(Person(6,Stepan,88))

// update by some filter

run {
    query[Person].filter(_.name == "Lena").update(_.age -> 16)
  }.transact(xa).unsafeRunSync




