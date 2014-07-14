<?php


   // DATI CONNESSIONE DATABASE
   $host = "ftp.whoareyou.altervista.org";
   $login = "whoareyou";
   $pass = "";
   $dbname = "my_whoareyou";



   $identificativo = $_POST["identificativo"];  



   $latitudine = $_POST["latitudine"];  
   $longitudine = $_POST["longitudine"]; 



   $link = mysqli_connect($host, $login, $pass, $dbname);

   /* check connection */
   if (mysqli_connect_errno()) {
      printf("Connect failed: %s\n", mysqli_connect_error());
      exit();
   }  



   /* 

   ***** RICHIESTE D'AMICIZIA *****

   -> controllo che posizione_condivisa sia settata a true, altrimenti errore

   -> controllo che le coordinate della posizione corrente siano gia' presenti nel DB, altrimenti errore

   -> cerco nel DB le richieste d'amicizia in sospeso

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



   // cerco nel DB le richieste d'amicizia in sospeso

   $inSospeso = 2; // statoAmicizia : 0 => amicizia eliminata ;  1 => amici ;  2 => amicizia in pending 

   $arrayRichiesteAmicizia = array();

   $query2 = "SELECT A.identificativo, A.immagine, A.username FROM Amicizia F JOIN Account A ON F.id_richiedente=A.identificativo WHERE statoAmicizia ='".$inSospeso."' AND F.id_ricevente='".$identificativo."'";      

   if( $risultato2 = mysqli_query($link, $query2, MYSQLI_USE_RESULT) ) {

      while ($row = mysqli_fetch_array($risultato2)) {

         $arrayRichiesta = array('identificativo' => $row['identificativo'], 'immagine' => $row['immagine'], 'username' => $row['username']);

         array_push($arrayRichiesteAmicizia, $arrayRichiesta);
      }

      mysqli_free_result($risultato2);

   }  else { 

      errorExit();
   }




   mysqli_close($link);


   $status = "OK";
   $jsonArr = array('status' => $status, 'result' => $arrayRichiesteAmicizia);

   echo json_encode($jsonArr);

?>
