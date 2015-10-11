package utils.service;

import play.Plugin;
import play.Logger;
import utils.ServerProperties;

import static utils.DataUtils.safeLong;

public class DaemonPlugin extends Plugin
{

    private static final long TIME_TO_WAIT = 10 * 60 * 1000;
    private ThreadLocal<DaemonThread> tl = new ThreadLocal<DaemonThread>();

    @Override
    public void onStart()
    {
        super.onStart();
        init();
    }

    public void init()
    {
        // init Daemon
        try {
//            long sessionTimeout = safeLong(ServerProperties.getValue("daemon.session.timeout"));
            long sleepTimeout = safeLong(ServerProperties.getValue("daemon.sleep.timeout"), TIME_TO_WAIT);
//            long inviteTimeout = Long.valueOf(props.getProperty(PROP_PREFIX + "invite.timeout"));
//            long userRegisterTimeout =
//                    Long.valueOf(props.getProperty(PROP_PREFIX + "user.register.timeout", "" + 6 * 60 * 60 * 1000));
            if (sleepTimeout != 0) {
                DaemonThread daemon =
                        new DaemonThread(sleepTimeout, "asd_daemon");
                tl.set(daemon);
                daemon.start();
            } else {
                throw new Exception("bad parameters in ServerProperties");
            }
        } catch (Exception e) {
            Logger.error("Unable to start Daemon: " + e.getMessage());
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        DaemonThread t = tl.get();
        if (t != null)
            t.terminate();
        Logger.info("Daemon thread exited...");
    }

}
