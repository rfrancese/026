<?php


   // DATI CONNESSIONE DATABASE
   $host = "ftp.whoareyou.altervista.org";
   $login = "whoareyou";
   $pass = "";
   $dbname = "my_whoareyou";



   $identificativo = $_POST["identificativo"];  



   $latitudine = $_POST["latitudine"]; 
   $longitudine = $_POST["longitudine"]; 

   $oggetto = $_POST["oggetto"];


   
   
   $link = mysqli_connect($host, $login, $pass, $dbname);

   /* check connection */
   if (mysqli_connect_errno()) {
      printf("Connect failed: %s\n", mysqli_connect_error());
      exit();
   }  



   /* 

   ***** BACHECA AVVISTAMENTI *****

   -> controllo che posizione_condivisa sia settata a true, altrimenti errore

   -> controllo che le coordinate della posizione corrente siano gia' presenti nel DB, altrimenti errore

   -> inserisco il messaggio d'avvistamento nel DB

   */

   function errorExit() {

      mysqli_close($link);
      $status = "ERROR";
      $jsonArr = array('status' => $status);
      echo json_encode($jsonArr);
      exit();

   } 



   // controllo che posizione_condivisa sia settata a true, altrimenti errore

   $valoreTrue = 1; // $posizione_condivisa = true, numero diverso da zero in mysql

   $query = "SELECT posizione_condivisa FROM Account WHERE identificativo='".$identificativo."'";

   if( $risultato = mysqli_query($link, $query, MYSQLI_USE_RESULT) ) {

      $rigaAccount = mysqli_fetch_row($risultato);

      if ( $rigaAccount[0] == $valoreTrue ) {

         mysqli_free_result($risultato);

      } else {

         errorExit();
      }
   } else {
      
      errorExit();
   }



   // controllo che le coordinate della posizione corrente siano gia' presenti nel DB, altrimenti errore

   $query1 = "SELECT latitudine, longitudine FROM Coordinate WHERE latitudine='".$latitudine."' AND longitudine='".$longitudine."'";

   if( $risultato1 = mysqli_query($link, $query1, MYSQLI_USE_RESULT) ) {

      while ( $rigaCoordinate = mysqli_fetch_row($risultato1) ) {
         if ( $rigaCoordinate[0] == $latitudine && $rigaCoordinate[1] == $longitudine ) {

            mysqli_free_result($risultato1);

         } else {  
           
            errorExit();
         }
      }

   }  else {
      
      errorExit();
   }



   //preparo la stringa oggetto da inserire nel DB
   $oggetto = mysqli_real_escape_string($link, $oggetto);
   
   // $oggetto = addcslashes($oggetto, '');



   // inserisco il messaggio d'avvistamento nel DB

   $data_ora = date("Y-m-d H:i:s"); // formato data e ora => 0000-00-00 00:00:00 

   $query2 = "INSERT INTO Messaggio_Bacheca VALUES('".$identificativo."','".$data_ora."','".$oggetto."','".$latitudine."','".$longitudine."')";  

   if( mysqli_query($link, $query2) != TRUE ) {
      errorExit();
   }




   mysqli_close($link);


   $status = "OK";
   $jsonArr = array('status' => $status);

   echo json_encode($jsonArr);

?>
