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

   ***** BACHECA AVVISTAMENTI *****

   -> controllo che posizione_condivisa sia settata a true, altrimenti errore

   -> controllo che le coordinate della posizione corrente siano gia' presenti nel DB, altrimenti errore

   -> cerco nel DB le coordinate che si trovano nelle vicinanze della posizione corrente

   -> per ogni coordinate vicine stampo le informazioni dei messaggi d'avvistamento inviati in tale posizione

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


   // cerco nel DB le coordinate che si trovano nelle vicinanze della posizione corrente


   /*
   Funzione che calcola la distanza in km tra due punti, data la latitudine e la longitudine di tali punti.

   Definizioni:
   Le latitudini sud sono negative, le longitudini est sono positive

   Parametri passati alla funzione:
   lat_A, long_A = latitudine e longitudine del punto A (in gradi decimali)
   lat_B, long_B = latitudine e longitudine del punto B (in gradi decimali)

   */

   function distanzaCoordinate($lat_A, $long_A, $lat_B, $long_B) {

      $theta = $long_A - $long_B;
      $dist = sin(deg2rad($lat_A)) * sin(deg2rad($lat_B)) +  cos(deg2rad($lat_A)) * cos(deg2rad($lat_B)) * cos(deg2rad($theta));
      $dist = acos($dist);
      $dist = rad2deg($dist);
      $miles = $dist * 60 * 1.1515;
      $kilometers = $miles * 1.609344;

      
      return $kilometers;

   } 


   $arrayDistanza = array();

   $query2 = "SELECT * FROM Coordinate";

   if( $risultato2 = mysqli_query($link, $query2, MYSQLI_USE_RESULT) ) {

      while ($row = mysqli_fetch_array($risultato2)) {

         $resultKilometers = distanzaCoordinate( $latitudine, $longitudine, $row['latitudine'], $row['longitudine'] ); 

         if ( $resultKilometers <= 5) {  // se la distanza è minore di 5 km allora aggiungo le coordinate all'array

            $arrayCoordinate = array($row['latitudine'], $row['longitudine'], $resultKilometers);

            array_push($arrayDistanza, $arrayCoordinate);
         }

      }

      mysqli_free_result($risultato2);


   }  else {

      errorExit();
   }


   
   // per ogni coordinate vicine stampo le informazioni dei messaggi d'avvistamento inviati in tale posizione
   $arrayBachecaAvvistamenti = array();

   for ($i=0; $i < count($arrayDistanza); $i++) {

      $latDistanza = $arrayDistanza[$i][0]; 
      $longDistanza = $arrayDistanza[$i][1];
      $distanza = $arrayDistanza[$i][2];

      

      //messaggi d'avvistamento dell'ultima ora
      $anHourAgo = time() - ( 60 * 60 ); 
      $dateAnHourAgo = date("Y-m-d H:i:s", $anHourAgo ); // formato data e ora => 0000-00-00 00:00:00  

                 
      $query3 = "SELECT A.identificativo, A.immagine, A.username, MB.oggetto, MB.data_ora FROM Messaggio_Bacheca MB JOIN Account A ON MB.Account_identificativo=A.identificativo WHERE Coord_latitudine='".$latDistanza."' AND Coord_longitudine='".$longDistanza."' AND MB.data_ora >='".$dateAnHourAgo."' ORDER BY MB.data_ora DESC ";

      if( $risultato3 = mysqli_query($link, $query3, MYSQLI_USE_RESULT) ) {

         while ($row = mysqli_fetch_array($risultato3)) {

               $arrayMessaggioAvvistamento = array('identificativo' => $row['identificativo'], 'immagine' => $row['immagine'], 'username' => $row['username'], 'messaggio' => $row['oggetto'], 'data_ora' => $row['data_ora'], 'distanza' => $distanza);

               array_push($arrayBachecaAvvistamenti, $arrayMessaggioAvvistamento);
           
         }
         mysqli_free_result($risultato3);

         
      }  else { 
    
         errorExit();
      }

   }



   mysqli_close($link);


   $status = "OK";
   $jsonArr = array('status' => $status, 'result' => $arrayBachecaAvvistamenti);

   echo json_encode($jsonArr);

?>
