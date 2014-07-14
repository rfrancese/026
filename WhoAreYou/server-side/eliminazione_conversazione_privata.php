<?php


   // DATI CONNESSIONE DATABASE
   $host = "ftp.whoareyou.altervista.org";
   $login = "whoareyou";
   $pass = "";
   $dbname = "my_whoareyou";



   $identificativoUtente = $_POST["identificativoUtente"];  

   $identificativoAmico = $_POST["identificativoAmico"]; 



   $link = mysqli_connect($host, $login, $pass, $dbname);

   /* check connection */
   if (mysqli_connect_errno()) {
      printf("Connect failed: %s\n", mysqli_connect_error());
      exit();
   }  



   /* 

   ***** CONVERSAZIONE PRIVATA *****

   -> controllo che i campi statoAmicizia e conversazione siano settati a true, altrimenti errore

   -> cerco nel DB i messaggi privati relativi alla conversazione

   -> per ogni messaggio privato setto il campo statoEliminazione al valore corrispondente, 
   cioè a 1 se il mittente è uguale a identificativoUtente, a 2 se il destinatario  è uguale a identificativoUtente

   -> se un messaggio privato aveva già un valore diverso da zero allora setto il campo statoEliminazione a 3, cioè eliminato da entrambi gli utenti

   -> controllo se ci sono ancora messaggi nella conversazione, altrimenti setto il campo conversazione a false

   */

   function errorExit() {

      mysqli_close($link);
      $status = "ERROR";
      $jsonArr = array('status' => $status);
      echo json_encode($jsonArr);
      exit();

   } 



   // controllo che i campi statoAmicizia e conversazione siano settati a true, altrimenti errore

   $valoreTrue = 1; // true, numero diverso da zero in mysql

   $query = "SELECT statoAmicizia, conversazione FROM Amicizia WHERE statoAmicizia='".$valoreTrue."' AND conversazione ='".$valoreTrue."' AND ( (id_richiedente='".$identificativoUtente."' AND id_ricevente='".$identificativoAmico."') OR (id_richiedente='".$identificativoAmico."' AND id_ricevente='".$identificativoUtente."') )";

   if( $risultato = mysqli_query($link, $query, MYSQLI_USE_RESULT) ) {

      $riga = mysqli_fetch_row($risultato);

      if ( $riga[0] == $valoreTrue && $riga[1] == $valoreTrue) {

         mysqli_free_result($risultato);

      } else {

         errorExit();
      }
   } else {

      errorExit();
   }


   // cerco nel DB i messaggi privati relativi alla conversazione

   // per ogni messaggio privato setto il campo statoEliminazione al valore corrispondente, cioè a 1 se il mittente è uguale a identificativoUtente, a 2 se il destinatario  è uguale a identificativoUtente

   // se un messaggio privato aveva già un valore diverso da zero allora setto il campo statoEliminazione a 3, cioè eliminato da entrambi gli utenti

   $statoMessaggioLetto = "1"; // 0 -> non letto; 1 -> letto


   $nonEliminato = "0"; // statoEliminazione di un messaggio privato:  0 -> non eliminato; 1 -> eliminato dal mittente ; 2 -> eliminato dal destinatario ; 3 -> eliminato da entrambi

   $eliminatoDalMittente = "1";

   $eliminatoDalDestinatario = "2";

   $eliminato = "3";


   $data_ora = date("Y-m-d H:i:s"); // formato data e ora => 0000-00-00 00:00:00 


   // CASO IN CUI UNO DEI DUE GIA' L'HA CANCELLATA e quindi si deve cancellare definitivamente

   $query1 = " UPDATE Messaggio_Privato SET statoEliminazione='".$eliminato."' WHERE ( ( mittente='".$identificativoUtente."' AND destinatario='".$identificativoAmico."' AND statoEliminazione='".$eliminatoDalDestinatario."' ) OR ( mittente='".$identificativoAmico."' AND destinatario='".$identificativoUtente."' AND statoEliminazione='".$eliminatoDalMittente."' ) ) AND data_ora <= '".$data_ora."' AND statoMessaggio='".$statoMessaggioLetto."' ";

   if( mysqli_query($link, $query1) != TRUE ) {
      errorExit();
   }


   // CASO IN CUI UNO DEI DUE UTENTI VUOLE CANCELLARLA, ma l'altro deve continuare a poterla vedere

   $query2 = " UPDATE Messaggio_Privato SET statoEliminazione='".$eliminatoDalMittente."' WHERE ( mittente='".$identificativoUtente."' AND destinatario='".$identificativoAmico."' AND statoEliminazione='".$nonEliminato."' ) AND data_ora <= '".$data_ora."' AND statoMessaggio='".$statoMessaggioLetto."' ";

   if( mysqli_query($link, $query2) != TRUE ) {
      errorExit();
   }

   $query3 = " UPDATE Messaggio_Privato SET statoEliminazione='".$eliminatoDalDestinatario."' WHERE ( mittente='".$identificativoAmico."' AND destinatario='".$identificativoUtente."' AND statoEliminazione='".$nonEliminato."' ) AND data_ora <= '".$data_ora."' AND statoMessaggio='".$statoMessaggioLetto."' ";

   if( mysqli_query($link, $query3) != TRUE ) {
      errorExit();
   }     


   // controllo se ci sono ancora messaggi nella conversazione, altrimenti setto il campo conversazione a false

   $valoreFalse = 0;

   $query4 = "SELECT COUNT(*) FROM Messaggio_Privato WHERE ( mittente='".$identificativoUtente."' AND destinatario='".$identificativoAmico."' AND ( statoEliminazione='".$nonEliminato."' OR statoEliminazione='".$eliminatoDalDestinatario."') ) OR ( mittente='".$identificativoAmico."' AND destinatario='".$identificativoUtente."' AND ( statoEliminazione='".$nonEliminato."' OR statoEliminazione='".$eliminatoDalMittente."') )"; 

   if( $risultato4 = mysqli_query($link, $query4, MYSQLI_USE_RESULT) ) {

      $row = mysqli_fetch_row($risultato4);

      if ( $row[0] == $valoreFalse ) {

		 mysqli_free_result($risultato4);

         $query5 = " UPDATE Amicizia SET conversazione='".$valoreFalse."' WHERE statoAmicizia='".$valoreTrue."' AND conversazione ='".$valoreTrue."' AND ( (id_richiedente='".$identificativoUtente."' AND id_ricevente='".$identificativoAmico."') OR (id_richiedente='".$identificativoAmico."' AND id_ricevente='".$identificativoUtente."') )";

         if( mysqli_query($link, $query5) != TRUE ) {
            errorExit();
         }
         
      } 

   } else {

      errorExit();
   }




   mysqli_close($link);


   $status = "OK";
   $jsonArr = array('status' => $status);

   echo json_encode($jsonArr);

?>
