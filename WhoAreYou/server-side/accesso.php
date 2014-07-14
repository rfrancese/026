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
   $psw=$_POST['pass'];


   $result=mysqli_query($link, "SELECT * FROM Account  WHERE identificativo='".$id."' AND password='".$psw."'");

   if(mysqli_num_rows($result)!=0){

      while($e=mysqli_fetch_assoc($result)){

         $output[0]=$e;

      }
      print(json_encode($output));

   }
   else{

      $arr[0]= array('errore'=>"noresult");
      $arr[1]= array('errore1'=>"noresult");

      print(json_encode($arr));
   }


   mysqli_close($link);
?>
