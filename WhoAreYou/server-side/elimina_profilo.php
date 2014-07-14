<?php


   include "JSON.php";

   // DATI CONNESSIONE DATABASE
   $host = "ftp.whoareyou.altervista.org";
   $login = "whoareyou";
   $pass = "";
   $dbname = "my_whoareyou";

   $link = new mysqli($host,$login,$pass,$dbname);

   if (mysqli_connect_errno()) {
      printf("Connect failed: %s\n", mysqli_connect_error());
      exit();
   }


  $id=$_POST['identif'];


   function errorExit() {

      mysqli_close($link);
      $status = "ERROR";
      $jsonArr = array('status' => $status);
      echo json_encode($jsonArr);
      exit();

   }

   $result=mysqli_query($link, "DELETE FROM Messaggio_Bacheca  WHERE Account_identificativo='".$id."'");

   if( $result != TRUE ) 
      errorExit();


   $result0=mysqli_query($link, "DELETE FROM Messaggio_Privato WHERE mittente='".$id."' OR destinatario='".$id."'"); 

   if( $result0 != TRUE ) 
      errorExit();


   $result1=mysqli_query($link, "DELETE FROM Registrazione_Posizione  WHERE Account_identificativo='".$id."'");

   if( $result1 != TRUE ) 
      errorExit();


   $result2=mysqli_query($link, "DELETE FROM Amicizia WHERE id_richiedente='".$id."' OR id_ricevente='".$id."'");

   if( $result2 != TRUE ) 
      errorExit();


   $result3=mysqli_query($link, "DELETE FROM Account WHERE identificativo='".$id."'");

   if( $result3 != TRUE ) 
      errorExit();


   mysqli_close($link);


   $status = "OK";
   $jsonArr = array('status' => $status);

   echo json_encode($jsonArr);

?>
