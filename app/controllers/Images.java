package controllers;

import models.Image;
import models.internal.UserManager;
import models.user.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import static play.data.Form.form;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 18.07.2015
 * Time: 15:47
 */

public class Images extends Controller
{
//    public static Result get(long id)
//    {
//        Image image = ImageManager.getById(id);
//
//        if (image != null) {
//
//            /*** here happens the magic ***/
//            return ok(image.data).as("image");
//            /************************** ***/
//
//        } else {
//            flash("error", "Picture not found.");
//            return redirect(routes.Application.index());
//        }
//    }

    public static Result upload()
    {
        Form<UploadImageForm> form = form(UploadImageForm.class).bindFromRequest();
        User user = UserManager.getAutoUser();

        if (form.hasErrors()) {
            return badRequest(
                    form.errorsAsJson());

        } else {
            long id = new Image(
                    form.get().image.getFilename(),
                    form.get().image.getFile(),
                    user
            ).getId();

            flash("success", "File uploaded.");
            return redirect(routes.Application.index());
        }
    }

    public static class UploadImageForm
    {
        public Http.MultipartFormData.FilePart image;

        public String validate()
        {
            Http.MultipartFormData data = request().body().asMultipartFormData();
            image = data.getFile("image");

            if (image == null) {
                return "File is missing.";
            }

            return null;
        }
    }
}
