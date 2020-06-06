package postgres_quill

import cats.effect.{Blocker, ContextShift, IO, Timer}
import doobie.free.connection.ConnectionIO
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie.implicits._

import scala.concurrent.ExecutionContext.global
import scala.concurrent.ExecutionContextExecutor

object DatabaseEnvironment {

  implicit val ec: ExecutionContextExecutor = global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val tm: Timer[IO] = IO.timer(ec)

  private val user = "admin"
  private val password = "admin"
  private val host = "localhost"
  private val db = "default"

  class PostgresTransactor(host: String, db: String, user: String, password: String)(implicit cs: ContextShift[IO]) {

    val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      s"jdbc:postgresql://$host/$db",
      user,
      password,
      Blocker.liftExecutionContext(ExecutionContexts.synchronous)
    )
  }

  val xa: Transactor[IO] = new PostgresTransactor(host, db, user, password).xa

  def transactAndPrint[F](con: ConnectionIO[F]) =
    println(con.transact(xa).unsafeRunSync)
}
