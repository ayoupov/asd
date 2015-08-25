package controllers

import java.io.File

import play.api.mvc.{Action, Controller}
import utils.ServerProperties

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 24.08.2015
 * Time: 6:38
 */
object FileManager extends Controller {
  def upload = Action(parse.multipartFormData) {
    request =>
      val mcid = request.body.dataParts("mcid").head
      val where = ServerProperties.getValue("asd.upload.path") + mcid
      new File(where).mkdirs()
      request.body.files.foreach {
        picture =>
          val filename = picture.filename
          val contentType = picture.contentType
          picture.ref.moveTo(new File(s"/$where/$filename"))
      }
      Redirect(routes.Admin.index()).flashing(
        "success" -> (request.body.files.map{f => f.filename}.mkString(",") + " uploaded successfully")
      )

    //        .getOrElse {
//        Redirect(routes.Application.index()).flashing(
//          "error" -> "Missing file")
//      }
  }
}
