<?php
$user="ph109";
$password="abcph109354";
$database="ph109";
$server ="mysql-server-1.macs.hw.ac.uk";
$dberror1 = "could not connect";

$conn = mysql_connect($server, $user, $password) or die ($dberror1);
$selectdb = mysql_select_db($database, $conn);


$query = "SELECT * FROM User WHERE Username = '". $_POST["Username"]."'";
$result = mysql_query($query);
 while($row=mysql_fetch_assoc($result))
  $output[]=$row;
 print(json_encode($output));
 mysql_close();   


?>

