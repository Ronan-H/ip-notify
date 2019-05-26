IP Notify
Sends a notification email when an IP address change has been detected.

Usage: java -jar ip-notify.jar [options]
  Options (asterisk fields are required):
  * --expected-ip, -e
      Expected/current IP address
  * --user, -u
      Address of the SMTP email to use to send the notification emails from
  * --pass, -p
      Password for the SMTP email to use to send the notification emails from
  * --recipient, -rec
      Recipient address of the notification emails
    --check-site, -c
      Site to use to check the current IP address
      Default: http://www.icanhazip.com/
    --sleep-time, -s
      Time to sleep after checking the IP address before trying again 
      (minutes) 
      Default: 10
    --retry-timeout, -rt
      Time to sleep failing to send the notification email before trying again
      Default: 5
