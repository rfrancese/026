<?php

   // DATI CONNESSIONE DATABASE
   $host = "ftp.whoareyou.altervista.org";
   $login = "whoareyou";
   $pass = "";
   $dbname = "my_whoareyou";



   $identificativo = $_POST["identificativo"]; 




   $link = mysqli_connect($host, $login, $pass, $dbname);

   /* check connection */
   if (mysqli_connect_errno()) {
      printf("Connect failed: %s\n", mysqli_connect_error());
      exit();
   }  



   /* 

   ***** LOGOUT *****

   -> controllo che posizione_condivisa sia settata a false, altrimenti setto il valore

   */


   function errorExit() {

      mysqli_close($link);
      $status = "ERROR";
      $jsonArr = array('status' => $status);
      echo json_encode($jsonArr);
      exit();

   } 

   //controllo che posizione_condivisa sia settata a false, altrimenti setto il valore

   $valoreFalse = 0; // $posizione_condivisa = false, zero in mysql

   $query = "SELECT posizione_condivisa FROM Account WHERE identificativo='".$identificativo."'";

   if( $risultato = mysqli_query($link, $query, MYSQLI_USE_RESULT) ) {

      $rigaAccount = mysqli_fetch_row($risultato);

      if ( $rigaAccount[0] != $valoreFalse ) {

         mysqli_free_result($risultato);

         $query1 = "UPDATE Account SET posizione_condivisa='".$valoreFalse."' WHERE identificativo='".$identificativo."'";

         if( mysqli_query($link, $query1) != TRUE ) {
            errorExit();
         }
      } 

   } else {
      errorExit();
   }


   mysqli_close($link);
   
   
   
   $status = "OK" ;

   $jsonArr = array('status' => $status);

   echo json_encode($jsonArr);

?>
