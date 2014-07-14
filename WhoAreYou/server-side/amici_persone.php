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

   ***** AMICI *****

   -> controllo che posizione_condivisa sia settata a true, altrimenti errore

   -> controllo che le coordinate della posizione corrente siano gia' presenti nel DB, altrimenti errore

   -> cerco nel DB gli amici dell'utente

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



   // cerco nel DB gli amici dell'utente

   $valoreTrue = 1; // statoAmicizia : 0 => amicizia eliminata ;  1 => amici ;  2 => amicizia in pending 

   $arrayUtenti = array();

   $query2 = "SELECT id_richiedente, id_ricevente FROM Amicizia WHERE statoAmicizia ='".$valoreTrue."' AND (id_richiedente='".$identificativo."' OR id_ricevente='".$identificativo."')";

   if( $risultato2 = mysqli_query($link, $query2, MYSQLI_USE_RESULT) ) {

      while ($row = mysqli_fetch_array($risultato2)) {

         if( $row['id_richiedente'] == $identificativo ) {

            array_push($arrayUtenti, $row['id_ricevente']);

         } else if( $row['id_ricevente'] == $identificativo ) {

            array_push($arrayUtenti, $row['id_richiedente']);

         }

      }

      mysqli_free_result($risultato2);

   }  else {

      errorExit();
   }


   // per ogni amico stampo le informazioni relative ad esso

   $arrayAmici = array();

   for ($i=0; $i < count($arrayUtenti); $i++) {

      $identificativoAmico = $arrayUtenti[$i]; 

      $query3 = "SELECT A.identificativo, A.immagine, A.username FROM Account A WHERE identificativo='".$identificativoAmico."' ";      

      if( $risultato3 = mysqli_query($link, $query3, MYSQLI_USE_RESULT) ) {

         while ($row = mysqli_fetch_array($risultato3)) {

            if( $row['identificativo'] == $identificativoAmico ) {

               $arrayInfo = array( 'identificativo' => $row['identificativo'], 'immagine' => $row['immagine'], 'username' => $row['username'] );

               array_push($arrayAmici, $arrayInfo);

            } else {

               errorExit();
            }

         }

         mysqli_free_result($risultato3);

      }  else { 

         errorExit();
      }

   }


   mysqli_close($link);


   $status = "OK";
   $jsonArr = array('status' => $status, 'result' => $arrayAmici);

   echo json_encode($jsonArr);

?>
