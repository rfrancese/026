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

   ***** ELIMINA AMICIZIA *****

   -> controllo che il campo statoAmicizia sia settato a true, altrimenti errore

   -> setto il campo statoAmicizia a 0, cioè amicizia eliminata 

   */

   function errorExit() {

      mysqli_close($link);
      $status = "ERROR";
      $jsonArr = array('status' => $status);
      echo json_encode($jsonArr);
      exit();

   } 



   // controllo che i campi statoAmicizia e conversazione siano settati a true, altrimenti errore

   $valoreTrue = 1; // statoAmicizia : 0 => amicizia eliminata ;  1 => amici ;  2 => amicizia in pending 
   $valoreFalse = 0; 

   $query =  "SELECT A.statoAmicizia FROM Amicizia A WHERE A.statoAmicizia ='".$valoreTrue."' AND ( A.id_ricevente='".$identificativoUtente."' AND A.id_richiedente='".$identificativoAmico."') OR ( A.id_ricevente='".$identificativoAmico."' AND A.id_richiedente='".$identificativoUtente."') ";      

   if( $risultato = mysqli_query($link, $query, MYSQLI_USE_RESULT) ) {

      $riga = mysqli_fetch_row($risultato);

      if ( $riga[0] == $valoreTrue ) {

         mysqli_free_result($risultato);

         $query1 = " UPDATE Amicizia A SET A.statoAmicizia='".$valoreFalse."', A.conversazione='".$valoreFalse."' WHERE A.statoAmicizia ='".$valoreTrue."' AND ( A.id_ricevente='".$identificativoUtente."' AND A.id_richiedente='".$identificativoAmico."') OR ( A.id_ricevente='".$identificativoAmico."' AND A.id_richiedente='".$identificativoUtente."') ";

         if( mysqli_query($link, $query1) != TRUE ) {

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
