package utils.service;

import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;
import com.vividsolutions.jts.geom.Point;
import models.address.Address;
import models.internal.ContentManager;
import utils.map.GeocodeUtils;

import java.util.List;

import static utils.HibernateUtils.*;

/**
 * User: ayoupov
 */
public class DaemonThread extends Thread
{

    private long sleeptime;
    private static ThreadLocal<DaemonThread> dt = new ThreadLocal<DaemonThread>();

    private enum ThreadState
    {
        BUSY, IDLE
    }

    private ThreadState state;

    public DaemonThread(long sleepTime,
                        String threadName)
    {
        setDaemon(true);
        setName(threadName);
        this.sleeptime = sleepTime;
        dt.set(this);
        state = ThreadState.IDLE;
    }

    public void run()
    {
        System.out.println("daemon thread started...");
        while (true) {
            try {
                Thread.sleep(sleeptime);
                if (this.isAlive() && !this.isInterrupted() && state != ThreadState.BUSY) {
                    beginTransaction();
                    geocodeAddresses();
                    commitTransaction();
                }
            } catch (InterruptedException x) {
                x.printStackTrace();
                // todo: smart transaction handling?
                break;
            } finally {
                state = ThreadState.IDLE;
            }
        }
    }

    private void geocodeAddresses()
    {
        state = ThreadState.BUSY;
        int howMany = 100;
        int counter = 0;
        List<Address> addresses = ContentManager.getEmptyAddresses(howMany);
        for (Address address : addresses) {
            GeocodeResponse gresp = GeocodeUtils.geocode((Point) address.getGeometry());
            if (gresp == null || !GeocoderStatus.OK.equals(gresp.getStatus())) {
                System.out.println("Daemon.geocode: got " +
                        ((gresp == null) ? " null response" : gresp.getStatus()));
                if (gresp == null || GeocoderStatus.OVER_QUERY_LIMIT.equals(gresp.getStatus()))
                    break;
            }
            List<GeocoderResult> results = gresp.getResults();
            address.setUnfolded(results.get(0).getFormattedAddress());
            saveOrUpdate(address);
            counter++;
        }
        System.out.println("Daemon.geocode: geocoded  " + counter + " churches");
        state = ThreadState.IDLE;
    }

    public void terminate()
    {
        try {
            this.interrupt();
        } catch (Exception e) {

        }

    }

    public static DaemonThread getThis()
    {
        if (dt != null) {
            return dt.get();
        }
        return null;
    }
}