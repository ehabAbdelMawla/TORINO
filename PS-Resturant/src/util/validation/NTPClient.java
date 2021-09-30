package util.validation;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

public final class NTPClient {

    // Singleton Instance
    private static NTPClient instance;
    private final String[] HOSTS = {
        "time.google.com",
        "time1.google.com",
        "time2.google.com",
        "time3.google.com",
        "time4.google.com",
        "time.facebook.com",
        "time1.facebook.com",
        "time2.facebook.com",
        "time3.facebook.com",
        "time4.facebook.com",
        "time5.facebook.com",
        "pool.ntp.org"};
    private int index = 0;
    private Date serverDate;

    public static NTPClient getInstance() {
        if (instance == null) {
            instance = new NTPClient();
        }
        return instance;
    }

    private NTPClient() {
    }

    private void processResponse(TimeInfo info) {
        NtpV3Packet message = info.getMessage();
        TimeStamp rcvNtpTime = message.getReceiveTimeStamp();
        System.out.println("Receive Timestamp: [" + rcvNtpTime + "\t" + rcvNtpTime.toDateString() + "]");
        setServerDate(rcvNtpTime.getDate());
    }

    public Date getServerDate() {
        return serverDate;
    }

    public void setServerDate(Date serverDate) {
        this.serverDate = serverDate;
    }

    public void requestServerDate() throws IOException {
        System.out.println("----------------- Start: requestServerDate --------------");
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(10000);
        if (!client.isOpen()) {
            client.open();
        }

        InetAddress hostAddr = InetAddress.getByName(HOSTS[index]);
        index = ++index % HOSTS.length;
        System.out.println("HOST: [" + hostAddr.getHostName() + "/" + hostAddr.getHostAddress() + "]");
        TimeInfo info = client.getTime(hostAddr);
        processResponse(info);
        client.close();
        System.out.println("------------------ End: requestServerDate ---------------");
    }
}
