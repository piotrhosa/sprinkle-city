<?php
$user="ph109";
$password="abcph109354";
$database="ph109";
$server ="mysql-server-1.macs.hw.ac.uk";
$dberror1 = "could not connect";

$conn = mysql_connect($server, $user, $password) or die ($dberror1);
$selectdb = mysql_select_db($database, $conn);

$query = "INSERT INTO HumanAvatar (Username, PositionX, PositionY, Direction) VALUES 
	('". $_POST["Username"]."', '". $_POST["PositionX"]."', '". $_POST["PositionY"]."', '". $_POST["Direction"]."')";
mysql_query($query);


 mysql_close();   


?>