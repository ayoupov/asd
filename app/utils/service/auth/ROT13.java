package utils.service.auth;

/**
 * Created with IntelliJ IDEA.
 * User: ayoupov
 * Date: 29.10.2015
 * Time: 16:48
 */
public class ROT13
{
    public static String encode(String text)
    {
//        String text = "abcdefghijklmnopqrstuvwxyz0123456789!$%^&*()äöü";

        String res = "";
        if (text == null)
            return res;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            ch--;
            if (ch % 32 < 13)
                ch += 13;
            else if (ch % 32 < 26)
                ch -= 13;
            else if (ch % 32 < 29)
                ch += 3;
            else
                ch -= 3;
            ch++;
            res += ch;
        }
        return res;
    }
}
