<?php
$user="ph109";
$password="abcph109354";
$database="ph109";
$server ="mysql-server-1.macs.hw.ac.uk";
$dberror1 = "could not connect";

$conn = mysql_connect($server, $user, $password) or die ($dberror1);
$selectdb = mysql_select_db($database, $conn);

$query = "UPDATE HumanAvatar SET  PositionX = '". $_POST["PositionX"]."', PositionY = '". $_POST["PositionY"]."', 
Direction = '". $_POST["Direction"]."' WHERE Username = '". $_POST["Username"]."'";
mysql_query($query);


 mysql_close();   


?>