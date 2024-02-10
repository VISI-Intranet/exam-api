package Routing

import akka.http.javadsl.server.PathMatchers.segment
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.ExamRepository
import model.Exam

class ExamRoutes(implicit val examRepository: ExamRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("exam") {
      concat(
        pathEnd {
          concat(
            get {
              complete(examRepository.getAllExams())
            },
            post {
              entity(as[Exam]) { exam =>
                complete(examRepository.addExam(exam))
              }
            }
          )
        },
        path(Segment) { examId =>
          concat(
            get {
              complete(examRepository.getExamById(examId))
            },
            delete {
              complete(examRepository.deleteExam(examId))
            },
            put {
              entity(as[Exam]) { updatedExam =>
                complete(examRepository.updateExam(examId, updatedExam))
              }
            }
          )
        }
      )
    }
}