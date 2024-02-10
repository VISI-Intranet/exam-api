import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.mongodb.scala.MongoClient
import repository._
import Routing._
import model._

import scala.concurrent.{ExecutionContextExecutor, Future}
import java.util.Date
import akka.http.scaladsl.Http
object Main extends App {

  implicit val system: ActorSystem = ActorSystem("MyAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // Подключение к базе данных
  val client = MongoClient()
  implicit val db = client.getDatabase("Exam")

  implicit val examRepository = new ExamRepository()



  val examRoutes = new ExamRoutes()


  // Старт сервера
  private val bindingFuture = Http().bindAndHandle(examRoutes.route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  // Остановка сервера при завершении приложения
  sys.addShutdownHook {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}