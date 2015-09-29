package controllers

import java.io.File

import models.Image
import models.internal.UserManager
import models.user.User
import play.api.mvc.{Action, Controller}
import utils.media.images.Thumber
import utils.{HibernateUtils, ServerProperties}

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 24.08.2015
 * Time: 6:38
 */
object FileManager extends Controller {
  def upload = Action(parse.multipartFormData) {
    request =>

      HibernateUtils.beginTransaction()
      val localUser: User = UserManager.getLocalUser(request.session)
      val userHash = if (localUser == null) User.anonymousHash() else localUser.getHash
      HibernateUtils.commitTransaction()

      val where = ServerProperties.getValue("asd.upload.path") + userHash
      val webWhere = ServerProperties.getValue("asd.upload.relative.path") + userHash
      new File(where).mkdirs()
      request.body.files.foreach {
        picture =>
          val filename = picture.filename
          val contentType = picture.contentType
          val path = s"$where/$filename"
          val webPath = s"$webWhere/$filename"
          val outFile: File = new File(path)
          picture.ref.moveTo(outFile, replace = true)
          val setReadableSuccess = outFile.setReadable(true, false)
          val desc = s"uploaded to $outFile with setReadable success [$setReadableSuccess]"
          if (!ServerProperties.isInProduction) println(desc)
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