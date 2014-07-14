<?php

   // DATI CONNESSIONE DATABASE
   $host = "ftp.whoareyou.altervista.org";
   $login = "whoareyou";
   $pass = "";
   $dbname = "my_whoareyou";



   $identificativoMittente = $_POST["identificativoMittente"];  
   $identificativoDestinatario = $_POST["identificativoDestinatario"]; 


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

   ***** CONVERSAZIONE PRIVATA *****

   -> controllo che posizione_condivisa sia settata a true, altrimenti errore

   -> controllo che le coordinate della posizione corrente siano gia' presenti nel DB, altrimenti errore

   -> inserisco il messaggio privato nel DB

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

   $query = "SELECT posizione_condivisa FROM Account WHERE identificativo='".$identificativoMittente."'";

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



   // inserisco il messaggio privato nel DB

   $statoMessaggioNonLetto = "0"; // 0 -> non letto; 1 -> letto
   
   $statoEliminazioneNonEliminato =  "0"; // statoEliminazione di un messaggio privato:  0 -> non eliminato; 1 -> eliminato dal mittente ; 2 -> eliminato dal destinatario ; 3 -> eliminato da entrambi


   $data_ora = date("Y-m-d H:i:s"); // formato data e ora => 0000-00-00 00:00:00 

   $query2 = "INSERT INTO Messaggio_Privato VALUES('".$identificativoMittente."','".$identificativoDestinatario."','".$data_ora."','".$oggetto."','".$latitudine."','".$longitudine."','".$statoMessaggioNonLetto."','".$statoEliminazioneNonEliminato."')";  

   if( mysqli_query($link, $query2) != TRUE ) {
     // echo "5";
     errorExit();
   }




   mysqli_close($link);


   $status = "OK";
   $jsonArr = array('status' => $status);

   echo json_encode($jsonArr);

?>
