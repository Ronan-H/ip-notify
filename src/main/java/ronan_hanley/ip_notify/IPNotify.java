package ronan_hanley.ip_notify;

import com.beust.jcommander.JCommander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class IPNotify {
    private boolean running;

    public void go(Args jcArgs) {
        EmailBot emailBot = new EmailBot(jcArgs.username, jcArgs.password, jcArgs.recipient);

        URL checkSiteURL = null;
        String currentIP = null;
        boolean gotIP;
        boolean ipMatches;

        try {
            checkSiteURL = new URL(jcArgs.ipCheckSite);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        running = true;
        while (running) {
            System.out.printf("Checking current external IP address on %s...%n", jcArgs.ipCheckSite);

            gotIP = false;
            do {
                try {
                    currentIP = getCurrentIP(checkSiteURL);
                    gotIP = true;
                } catch (IOException e) {
                    System.out.printf("Error in retrieving current IP. Retrying in %d seconds...%n", jcArgs.retryTimeout);

                    try {
                        Thread.sleep(jcArgs.retryTimeout * 1000);
                    } catch (InterruptedException e2) {
                        e.printStackTrace();
                    }
                }
            } while (!gotIP);

            ipMatches = currentIP.equals(jcArgs.expectedIp);
            System.out.printf("Expected IP: %s - Actual IP: %s - Matches? %b%n", jcArgs.expectedIp, currentIP, ipMatches);

            if (ipMatches) {
                System.out.printf("IP has not changed. Sleeping for %d minutes before trying again...%n", jcArgs.sleepTime);

                try {
                    Thread.sleep(jcArgs.sleepTime * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("~~~ WARNING: IP has changed! ~~~");
                System.out.println("Sending notification email...");
                emailBot.sendIPChangeNotification(jcArgs.expectedIp, currentIP, jcArgs.retryTimeout);

                System.out.printf("Sleeping for %d seconds before exiting...%n", jcArgs.retryTimeout);

                try {
                    Thread.sleep(jcArgs.retryTimeout * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                System.out.println("Exiting...");
                System.exit(0);
            }
        }
    }

    private String getCurrentIP(URL checkSiteURL) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(checkSiteURL.openStream()));
        String ip = in.readLine();
        in.close();
        return ip;
    }

    public static void main(String[] rawArgs) {
        Args jcArgs = new Args();

        JCommander jc = JCommander.newBuilder()
                .addObject(jcArgs)
                .build();

        jc.parse(rawArgs);
        jc.setProgramName("IP Notify");

        new IPNotify().go(jcArgs);
    }
}
