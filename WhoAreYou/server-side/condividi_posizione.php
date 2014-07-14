<?php


   // DATI CONNESSIONE DATABASE
   $host = "ftp.whoareyou.altervista.org";
   $login = "whoareyou";
   $pass = "";
   $dbname = "my_whoareyou";



   $identificativo = $_POST["identificativo"]; 

   $posizione_condivisa = $_POST["posizione_condivisa"];  // in SQL  zero = false , uno = true
   $latitudine = $_POST["latitudine"]; 
   $longitudine = $_POST["longitudine"];




   $link = mysqli_connect($host, $login, $pass, $dbname);

   /* check connection */
   if (mysqli_connect_errno()) {
      printf("Connect failed: %s\n", mysqli_connect_error());
      exit();
   }  



   /* 

   ***** CONDIVISIONE POSIZIONE *****

   -> controllo che posizione_condivisa sia settata a true, altrimenti setto il valore

   -> controllo che le coordinate siano gia' presenti nel DB, altrimenti le inserisco

   -> verifico se la posizione corrente e' gia' presente nel DB:
   => se si aggiorno la data e l'ora
   => altrimenti le inserisco

   __________________________________________


   ***** POSIZIONE NON CONDIVISA *****

   -> controllo che posizione_condivisa sia settata a false, altrimenti setto il valore


   */

   function errorExit() {

      mysqli_close($link);
      $status = "ERROR";
      $jsonArr = array('status' => $status);
      echo json_encode($jsonArr);
      exit();

   } 



   if ( $posizione_condivisa == "true" ) { // caso in cui si vuole condividere la posizione 


      $valoreTrue = 1; // $posizione_condivisa = true, numero diverso da zero in mysql



      // controllo che la posizione sia settata a true

      $query = "SELECT posizione_condivisa FROM Account WHERE identificativo='".$identificativo."'";

      if( $risultato = mysqli_query($link, $query, MYSQLI_USE_RESULT) ) {

         $rigaAccount = mysqli_fetch_row($risultato);

         if ( $rigaAccount[0] != $valoreTrue ) {

            mysqli_free_result($risultato);

            $query0 = "UPDATE Account SET posizione_condivisa='".$valoreTrue."' WHERE identificativo='".$identificativo."'";

            if( mysqli_query($link, $query0) != TRUE ) {
               errorExit();
            }

         } else 

            mysqli_free_result($risultato);

      } else {
         errorExit();
      }


      // controllo che le coordinate siano presenti nel DB

      $query1 = "SELECT latitudine, longitudine FROM Coordinate WHERE latitudine='".$latitudine."' AND longitudine='".$longitudine."'";

      if( $risultato1 = mysqli_query($link, $query1, MYSQLI_USE_RESULT) ) {

         $rigaCoordinate = mysqli_fetch_row($risultato1);

         if ( $rigaCoordinate[0] == $latitudine && $rigaCoordinate[1] == $longitudine ){

            mysqli_free_result($risultato1);

         } else {  

            mysqli_free_result($risultato1);

            $query2 = "INSERT INTO Coordinate VALUES('".$latitudine."','".$longitudine."')";

            if( mysqli_query($link, $query2) != TRUE ) {

               errorExit();
            }
         }
      }  else {  
         errorExit();
      }




      /* verifico se la posizione corrente e' gia' presente nel DB:
      => se si aggiorno la data e l'ora
      => altrimenti le inserisco                             */

      $data_ora = date("Y-m-d H:i:s"); // formato data e ora => 0000-00-00 00:00:00 

      $query3 = "SELECT Account_identificativo, Coord_latitudine, Coord_longitudine FROM Registrazione_Posizione WHERE Account_identificativo ='".$identificativo."' AND Coord_latitudine='".$latitudine."' AND Coord_longitudine='".$longitudine."'";

      if( $risultato3 = mysqli_query($link, $query3, MYSQLI_USE_RESULT) ) {

         $rigaPosizione = mysqli_fetch_row($risultato3);

         if ( $rigaPosizione[0] == $identificativo && $rigaPosizione[1] == $latitudine && $rigaPosizione[2] == $longitudine ){

            mysqli_free_result($risultato3);

            $query4 = "UPDATE Registrazione_Posizione SET data_ora='".$data_ora."' WHERE Account_identificativo ='".$identificativo."' AND Coord_latitudine='".$latitudine."' AND Coord_longitudine='".$longitudine."'";

            if( mysqli_query($link, $query4) != TRUE ) {
               errorExit();
            }

         } else {  

            mysqli_free_result($risultato3);

            $query5 = "INSERT INTO Registrazione_Posizione VALUES('".$identificativo."','".$data_ora."','".$latitudine."','".$longitudine."')";  

            if( mysqli_query($link, $query5) != TRUE ) {

               errorExit();
            }
         }
      }  else {  
         errorExit();
      }


      $status = "OK" ;


   } else { // caso in cui NON si vuole condividere la posizione

      $valoreFalse = 0; // $posizione_condivisa = false, zero in mysql



      // controllo che la posizione sia settata a false

      $query4 = "SELECT posizione_condivisa FROM Account WHERE identificativo='".$identificativo."'";

      if( $risultato4 = mysqli_query($link, $query4, MYSQLI_USE_RESULT) ) {

         $rigaAccount4 = mysqli_fetch_row($risultato4);

         if ( $rigaAccount4[0] != $valoreFalse ) {

            mysqli_free_result($risultato4);

            $query5 = "UPDATE Account SET posizione_condivisa='".$valoreFalse."' WHERE identificativo='".$identificativo."'";

            if( mysqli_query($link, $query5) != TRUE ) {
               errorExit();
            }
         } 
      } else {
         errorExit();
      }

      $status = "OK" ;

   }


   mysqli_close($link);


   $jsonArr = array('status' => $status);

   echo json_encode($jsonArr);

?>
