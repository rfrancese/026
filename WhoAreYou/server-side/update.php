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
     
    $id=$_POST['identif'];
    $us=$_POST['usern'];
    $psw=$_POST['pass'];
    $num=$_POST['numer'];
    $mail=$_POST['em'];
    $city=$_POST['cit'];
    $imm=$_POST['img'];
    $data=$_POST['dat'];
    $sesso=$_POST['sex'];
    $con=$_POST['condiv'];
    
    
    $risultato=mysqli_query($link,"UPDATE Account SET username='".$us."', password='".$psw."', numero='".$num."', email='".$mail."', 
    citta='".$city."', immagine='".$imm."', data_nascita='".$data."', sesso='".$sesso."', dati_condivisi='".$con."' WHERE identificativo='".$id."'"); 
  
        
    $ris[0]= array('salvataggio'=>"ok");
        
    print(json_encode($ris));
    
    mysqli_close($link);

?>
