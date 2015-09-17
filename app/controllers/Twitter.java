package controllers;

import play.libs.F.Function;
import play.libs.F.Option;
import play.libs.F.Promise;
import play.libs.oauth.OAuth;
import play.libs.oauth.OAuth.ConsumerKey;
import play.libs.oauth.OAuth.OAuthCalculator;
import play.libs.oauth.OAuth.RequestToken;
import play.libs.oauth.OAuth.ServiceInfo;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.common.base.Strings;
import utils.ServerProperties;

//import javax.inject.Inject;

public class Twitter extends Controller {
    static final ConsumerKey KEY = new ConsumerKey(ServerProperties.getValue("tw.consumer.key"), ServerProperties.getValue("tw.consumer.secret"));

    private static final ServiceInfo SERVICE_INFO = new ServiceInfo("https://api.twitter.com/oauth/request_token",
            "https://api.twitter.com/oauth/access_token",
            "https://api.twitter.com/oauth/authorize",
            KEY);

    private static final OAuth TWITTER = new OAuth(SERVICE_INFO);

    private static WSClient ws = WS.client();

//    @Inject
    public Twitter(WSClient ws) {
        this.ws = ws;
    }

    public static Promise<Result> verifyCredentials() {
        Option<RequestToken> sessionTokenPair = getSessionTokenPair();
        if (sessionTokenPair.isDefined()) {
            return ws.url("https://api.twitter.com/1.1/account/verify_credentials")
                    .sign(new OAuthCalculator(Twitter.KEY, sessionTokenPair.get()))
                    .get()
                    .map(new Function<WSResponse, Result>(){
                        @Override
                        public Result apply(WSResponse result) throws Throwable {
                            return ok(result.asJson());
                        }
                    });
        }
        return Promise.pure(redirect(routes.Twitter.auth()));
    }

    public static Result auth() {
        String verifier = request().getQueryString("oauth_verifier");
        if (Strings.isNullOrEmpty(verifier)) {
            String url = routes.Twitter.auth().absoluteURL(request());
            RequestToken requestToken = TWITTER.retrieveRequestToken(url);
            saveSessionTokenPair(requestToken);
            return redirect(TWITTER.redirectUrl(requestToken.token));
        } else {
            RequestToken requestToken = getSessionTokenPair().get();
            RequestToken accessToken = TWITTER.retrieveAccessToken(requestToken, verifier);
            saveSessionTokenPair(accessToken);
            return redirect(routes.Twitter.verifyCredentials());
        }
    }

    private static void saveSessionTokenPair(RequestToken requestToken) {
        session("token", requestToken.token);
        session("secret", requestToken.secret);
    }

    private static Option<RequestToken> getSessionTokenPair() {
        if (session().containsKey("token")) {
            return Option.Some(new RequestToken(session("token"), session("secret")));
        }
        return Option.None();
    }

}
