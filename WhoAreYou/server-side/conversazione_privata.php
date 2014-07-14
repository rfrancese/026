<?php


   // DATI CONNESSIONE DATABASE
   $host = "ftp.whoareyou.altervista.org";
   $login = "whoareyou";
   $pass = "";
   $dbname = "my_whoareyou";



   $identificativoUtente = $_POST["identificativoUtente"];

   $identificativoAmico = $_POST["identificativoAmico"]; 


   $latitudine = $_POST["latitudine"];  
   $longitudine = $_POST["longitudine"]; 



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

   -> cerco nel DB i messaggi privati, letti e non letti, relativi alla conversazione
   
   -> per ogni messaggio privato non letto setto il campo statoMessaggio a 1, cioè letto, se il destinatario è uguale a identificativoUtente

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

   $query = "SELECT posizione_condivisa FROM Account WHERE identificativo='".$identificativoUtente."'";

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



   // cerco nel DB i messaggi privati, letti e non letti, relativi alla conversazione

   $statoMessaggioLetto = "1"; // 0 -> non letto; 1 -> letto

   $statoMessaggioNonLetto = "0"; 


   $nonEliminato = "0"; // statoEliminazione di un messaggio privato:  0 -> non eliminato; 1 -> eliminato dal mittente ; 2 -> eliminato dal destinatario ; 3 -> eliminato da entrambi

   $eliminatoDalMittente = "1";

   $eliminatoDalDestinatario = "2";


   $arrayConversazioneLetta = array();

   $arrayConversazioneNonLetta = array();


   
   $query2 = "SELECT mittente, data_ora, oggetto, Coord_latitudine, Coord_longitudine, statoMessaggio FROM Messaggio_Privato WHERE ( mittente='".$identificativoUtente."' AND destinatario='".$identificativoAmico."' AND ( statoEliminazione='".$nonEliminato."' OR statoEliminazione='".$eliminatoDalDestinatario."') ) OR ( mittente='".$identificativoAmico."' AND destinatario='".$identificativoUtente."' AND ( statoEliminazione='".$nonEliminato."' OR statoEliminazione='".$eliminatoDalMittente."') )";   // ORDER BY data_ora DESC

   if( $risultato2 = mysqli_query($link, $query2, MYSQLI_USE_RESULT) ) {

      while ($row = mysqli_fetch_array($risultato2)) {

         $resultKilometers = distanzaCoordinate( $latitudine, $longitudine, $row['Coord_latitudine'], $row['Coord_longitudine'] ); 

         if ( $row['mittente'] == $identificativoUtente ) { 

			//se ho inviato io il messaggio privato non letto, non devo settarlo come messaggio letto!

            $tipoMessaggioPrivato = "INVIATO";                                                                      

            $arrayMessaggioPrivatoLetto = array('tipoMessaggioPrivato' => $tipoMessaggioPrivato, 'messaggio' => $row['oggetto'], 'data_ora' => $row['data_ora'], 'distanza' => $resultKilometers, 'statoMessaggio' => $row['statoMessaggio']);

            array_push($arrayConversazioneLetta, $arrayMessaggioPrivatoLetto);


         } else if ( $row['mittente'] == $identificativoAmico ) { 

            if ( $row['statoMessaggio'] == $statoMessaggioNonLetto ) { // il messaggio e' letto se il destinatario è uguale a identificativoUtente                                                                            

               $arrayMessaggioPrivatoNonLetto = array($identificativoAmico, $identificativoUtente, $row['data_ora']);

               array_push($arrayConversazioneNonLetta, $arrayMessaggioPrivatoNonLetto);

            }

            $tipoMessaggioPrivato = "RICEVUTO";

            $arrayMessaggioPrivatoLetto = array('tipoMessaggioPrivato' => $tipoMessaggioPrivato, 'messaggio' => $row['oggetto'], 'data_ora' => $row['data_ora'], 'distanza' => $resultKilometers, 'statoMessaggio' => $row['statoMessaggio']);

            array_push($arrayConversazioneLetta, $arrayMessaggioPrivatoLetto);


         }  else {  

            errorExit();
         }
      }

      mysqli_free_result($risultato2);

   }  else {
      
      errorExit();
   }


   // per ogni messaggio privato non letto setto il campo statoMessaggio a 1, cioè letto, se il destinatario è uguale a identificativoUtente

   for ($i=0; $i < count($arrayConversazioneNonLetta); $i++) {

      $mittente = $arrayConversazioneNonLetta[$i][0]; 
      $destinatario = $arrayConversazioneNonLetta[$i][1];
      $data_ora = $arrayConversazioneNonLetta[$i][2];

      $query3 = "UPDATE Messaggio_Privato SET statoMessaggio='".$statoMessaggioLetto."' WHERE mittente='".$mittente."' AND destinatario='".$destinatario."'  AND data_ora='".$data_ora."' AND statoMessaggio='".$statoMessaggioNonLetto."'";

      if( mysqli_query($link, $query3) != TRUE ) {
         errorExit();
      }
   }         



   mysqli_close($link);


   $status = "OK";
   $jsonArr = array('status' => $status, 'result' => $arrayConversazioneLetta);

   echo json_encode($jsonArr);

?>
