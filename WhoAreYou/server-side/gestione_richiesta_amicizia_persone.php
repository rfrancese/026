<?php


   // DATI CONNESSIONE DATABASE
   $host = "ftp.whoareyou.altervista.org";
   $login = "whoareyou";
   $pass = "";
   $dbname = "my_whoareyou";



   $identificativoUtente = $_POST["identificativoUtente"];  

   $identificativoAmico = $_POST["identificativoAmico"];  

   $richiesta = $_POST["richiesta"];  



   $link = mysqli_connect($host, $login, $pass, $dbname);

   /* check connection */
   if (mysqli_connect_errno()) {
      printf("Connect failed: %s\n", mysqli_connect_error());
      exit();
   }  



   /* 

   ***** GESTIONE RICHIESTA D'AMICIZIA *****

   -> controllo che il campo statoAmicizia sia settati a true, altrimenti errore

   -> aggiorno il campo con il valore 1 se la richiesta e' stata accettata, con il valore 0 se la richiesta e' stata rifiutata

   */

   function errorExit() {

      mysqli_close($link);
      $status = "ERROR";
      $jsonArr = array('status' => $status);
      echo json_encode($jsonArr);
      exit();

   } 



   //controllo che il campo statoAmicizia sia settati a true, altrimenti errore
   //aggiorno il campo con il valore 1 se la richiesta e' stata accettata, con il valore 0 se la richiesta e' stata rifiutata


   // statoAmicizia : 0 => amicizia eliminata ;  1 => amici ;  2 => amicizia in pending
   $valoreFalse = 0; 
   $valoreTrue = 1;  
   $inSospeso = 2;

   if ($richiesta == "accetta") {

      $query =  "SELECT A.statoAmicizia FROM Amicizia A WHERE A.statoAmicizia ='".$inSospeso."' AND A.id_ricevente='".$identificativoUtente."' AND A.id_richiedente='".$identificativoAmico."' ";      

      if( $risultato = mysqli_query($link, $query, MYSQLI_USE_RESULT) ) {

         $riga = mysqli_fetch_row($risultato);

         if ( $riga[0] == $inSospeso ) {

            mysqli_free_result($risultato);

            $query1 = " UPDATE Amicizia A SET A.statoAmicizia='".$valoreTrue."' WHERE A.statoAmicizia ='".$inSospeso."' AND A.id_ricevente='".$identificativoUtente."' AND A.id_richiedente='".$identificativoAmico."' ";

            if( mysqli_query($link, $query1) != TRUE ) {

               errorExit();
            }

         } else {

            errorExit();
         }

      } else {

         errorExit();
      }

   } else if ($richiesta == "rifiuta") {

      $query2 =  "SELECT A.statoAmicizia FROM Amicizia A WHERE A.statoAmicizia ='".$inSospeso."' AND A.id_ricevente='".$identificativoUtente."' AND A.id_richiedente='".$identificativoAmico."' ";      

      if( $risultato2 = mysqli_query($link, $query2, MYSQLI_USE_RESULT) ) {

         $riga = mysqli_fetch_row($risultato2);

         if ( $riga[0] == $inSospeso ) {

            mysqli_free_result($risultato2);

            $query3 = " UPDATE Amicizia A SET A.statoAmicizia='".$valoreFalse."' WHERE A.statoAmicizia ='".$inSospeso."' AND A.id_ricevente='".$identificativoUtente."' AND A.id_richiedente='".$identificativoAmico."' ";

            if( mysqli_query($link, $query3) != TRUE ) {

               errorExit();
            }

         } else {

            errorExit();
         }

      } else {

         errorExit();
      }

   } else {

      errorExit();
   }



   mysqli_close($link);


   $status = "OK";
   $jsonArr = array('status' => $status);

   echo json_encode($jsonArr);

?>
