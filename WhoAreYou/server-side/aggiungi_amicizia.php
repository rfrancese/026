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
    $id_ric=$_POST['ric'];
    $id_rich=$_POST['rich'];
    $conv=0;
    
    global $stato;
    
    if($statoAmici=="0") $stato=0;
    else if($statoAmici=="1") $stato=1;
    else if($statoAmici=="2") $stato=2;

    
    $risultato=mysqli_query($link,"INSERT INTO Amicizia(id_richiedente, id_ricevente, statoAmicizia, conversazione) 
    VALUES ('".$id_rich."','".$id_ric."','".$stato."','".$conv."')");
        
    $ris[0]= array('salvataggio'=>"ok");
        
    print(json_encode($ris));
    
    mysqli_close($link);

?>
