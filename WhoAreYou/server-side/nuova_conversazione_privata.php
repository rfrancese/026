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

   -> controllo che il campo statoAmicizia sia settato a true
   -> se il campo conversazione e' settato a true, resta invariato
   -> altrimenti viene settato a true

   */

   function errorExit() {

      mysqli_close($link);
      $status = "ERROR";
      $jsonArr = array('status' => $status, 'utente' => $identificativoUtente, 'Amico' => $identificativoAmico);
      echo json_encode($jsonArr);
      exit();

   } 



   // controllo che il campo statoAmicizia sia settato a true: se il campo conversazione e' settato a true, resta invariato; altrimenti viene settato a true

   $valoreTrue = "1"; // true, numero diverso da zero in mysql
   $valoreFalse= "0";

   $query = "SELECT statoAmicizia, conversazione FROM Amicizia WHERE statoAmicizia='".$valoreTrue."' AND ( (id_richiedente='".$identificativoUtente."' AND id_ricevente='".$identificativoAmico."') OR (id_richiedente='".$identificativoAmico."' AND id_ricevente='".$identificativoUtente."' ) ) ";

   if( $risultato = mysqli_query($link, $query, MYSQLI_USE_RESULT) ) {

      $riga = mysqli_fetch_row($risultato);

      if ( $riga[0] == $valoreTrue ) {

         if ( $riga[1] == $valoreTrue ) {

            mysqli_free_result($risultato);  

            // resta tutto invariato

         } else if ( $riga[1] == $valoreFalse) {

            mysqli_free_result($risultato);

            // setto conversazione a true

            $query1 = " UPDATE Amicizia SET conversazione='".$valoreTrue."' WHERE statoAmicizia='".$valoreTrue."' AND conversazione ='".$valoreFalse."' AND ( (id_richiedente='".$identificativoUtente."' AND id_ricevente='".$identificativoAmico."') OR (id_richiedente='".$identificativoAmico."' AND id_ricevente='".$identificativoUtente."') )";

            if( mysqli_query($link, $query1) != TRUE ) {

               errorExit();
            }
         } 

      } else {
         
         errorExit();
      }
   }  else {

      errorExit();
   }


   mysqli_close($link);


   $status = "OK";

   $jsonArr = array('status' => $status);

   echo json_encode($jsonArr);

?>
