package ronan_hanley.ip_notify;

import com.beust.jcommander.JCommander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class IPNotify {
    private boolean running;

    public void go(Args jcArgs) throws MalformedURLException, InterruptedException {
        EmailBot emailBot = new EmailBot(jcArgs.username, jcArgs.password, jcArgs.recipient);

        URL checkSiteURL;
        String currentIP = null;
        boolean gotIP;
        boolean ipMatches;

        checkSiteURL = new URL(jcArgs.ipCheckSite);

        running = true;
        while (running) {
            System.out.printf("Checking current external IP address on %s...%n", jcArgs.ipCheckSite);

            gotIP = false;
            while (!gotIP) {
                try {
                    currentIP = getCurrentIP(checkSiteURL);
                    gotIP = true;
                } catch (IOException e) {
                    System.out.printf("Error in retrieving current IP. Retrying in %d seconds...%n", jcArgs.retryTimeout);
                    Thread.sleep(jcArgs.retryTimeout * 1000);
                }
            }

            ipMatches = currentIP.equals(jcArgs.expectedIp);
            System.out.printf("Expected IP: %s - Actual IP: %s - Matches? %s%n",
                    jcArgs.expectedIp, currentIP, ipMatches ? "YES" : "NO");

            if (ipMatches) {
                System.out.printf("IP has not changed. Sleeping for %d minutes before trying again...%n", jcArgs.sleepTime);
                Thread.sleep(jcArgs.sleepTime * 60 * 1000);
            } else {
                System.out.println("~~~ WARNING: IP has changed! ~~~");
                System.out.println("Attempting to send notification email...");
                emailBot.sendIPChangeNotification(jcArgs.expectedIp, currentIP, jcArgs.retryTimeout);

                System.out.printf("Sleeping for %d seconds before exiting...%n", jcArgs.retryTimeout);

                Thread.sleep(jcArgs.retryTimeout * 1000);

                running = false;
            }
        }
    }

    private static String getCurrentIP(URL checkSiteURL) throws IOException {
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

        try {
            new IPNotify().go(jcArgs);
        } catch (MalformedURLException e) {
            System.err.println("Specified IP check site URL is malformed. Stack trace:");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Exiting...");
    }
}
