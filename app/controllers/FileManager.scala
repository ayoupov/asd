package controllers

import java.io.File
import java.nio.file.attribute.PosixFilePermissions

import models.Image
import play.api.mvc.{Action, Controller}
import utils.{HibernateUtils, ServerProperties}
import utils.media.images.Thumber

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
      val webWhere = ServerProperties.getValue("asd.upload.relative.path") + mcid
      new File(where).mkdirs()
      request.body.files.foreach {
        picture =>
          val filename = picture.filename
          val contentType = picture.contentType
          val path = s"/$where/$filename"
          val webPath = s"/$webWhere/$filename"
          val outFile: File = new File(path)
          picture.ref.moveTo(outFile, replace = true)
          val setReadableSuccess = outFile.setReadable(true,false)
          val desc = s"uploaded to $outFile with setReadable success [$setReadableSuccess]"
          println(desc)
          Thumber.rethumb(outFile)
          val image = new Image(desc, webPath)
          HibernateUtils.beginTransaction()
          HibernateUtils.save(image)
          HibernateUtils.commitTransaction()
      }
      Redirect(routes.Admin.index()).flashing(
        "success" -> (request.body.files.map { f => f.filename }.mkString(",") + " uploaded successfully")
      )

  }
}