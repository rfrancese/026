<?php


   // DATI CONNESSIONE DATABASE
   $host = "ftp.whoareyou.altervista.org";
   $login = "whoareyou";
   $pass = "";
   $dbname = "my_whoareyou";
   

    $link = new mysqli($host,$login,$pass,$dbname);
    
    if (mysqli_connect_errno()) {
    	printf("Connect failed: %s\n", mysqli_connect_error());
    	exit();
	}
     
    $statoAmici=$_POST['stato'];
    $ric=$_POST['id_ric'];
    $rich=$_POST['id_rich'];
    
    global $stato;
    
    if($statoAmici=="0") $stato=0;
    else if($statoAmici=="1") $stato=1;
    else if($statoAmici=="2") $stato=2;
    
    
    $risultato=mysqli_query($link,"UPDATE Amicizia SET statoAmicizia='".$stato."' WHERE (id_richiedente='".$rich."' AND id_ricevente='".$ric."') OR (id_richiedente='".$ric."' AND id_ricevente='".$rich."')"); 
  
        
    $ris[0]= array('salvataggio'=>"ok");
        
    print(json_encode($ris));
    
    mysqli_close($link);

?>
