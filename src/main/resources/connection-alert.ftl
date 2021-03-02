<html>
<h1>Dear admin,
</h1>
<p>
  We are writing to you because we have detected that your connection between WiseTime and Patrawin does not work as
  desired.
</p>
<p>
  Indeed, while WiseTime has been able to post time to a temporary table in the Patrawin database, the process
  inside Patrawin that is moving the entries from the temporary place over to the cases seems to fail. This means,
  that users will not be able to see posted times in their Patrawin cases although the cases show as successfully
  posted in WiseTime.
</p>
<p>
  Patrawin has not been consuming posted times since ${datetime}.
</p>
<p>
  Please make sure your running instances of Patrawin are restarted so that the Patrawin-Processes handling the
  internal data transfer will be re-initialised. In case this does not resolve the issue, and your users continue to
  miss time that was posted from their Patrawin cases, please contact us at support@wisetime.com so we can try to
  give you a hand.
</p>
</html>