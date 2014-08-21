<?php
$user="ph109";
$password="abcph109354";
$database="ph109";
$server ="mysql-server-1.macs.hw.ac.uk";
$dberror1 = "could not connect";

$conn = mysql_connect($server, $user, $password) or die ($dberror1);
$selectdb = mysql_select_db($database, $conn);

$query = "INSERT INTO AnimalCharacter (Animal, AnimalAvatar, Sleep, Fitness) VALUES 
	('". $_POST["Animal"]."', '". $_POST["AnimalAvatar"]."', '". $_POST["Sleep"]."', '". $_POST["Fitness"]."')";
mysql_query($query);


 mysql_close();   


?>