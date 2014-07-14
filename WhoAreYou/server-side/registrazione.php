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
    
    $controllo=mysqli_query($link,"SELECT * FROM Account WHERE identificativo='".$id."'");
    
    if(mysqli_num_rows($controllo)!=0){
    
    	$ris[0]= array('errore'=>"idesistente");
        $ris[1]= array('errore1'=>"reinserire");
        
        print(json_encode($ris));
        
    }
    else{
    
    	$risultato=mysqli_query($link,"INSERT INTO Account(identificativo, username, password, numero, email, citta, immagine, data_nascita, sesso, dati_condivisi) 
    	VALUES ('".$id."','".$us."','".$psw."','".$num."','".$mail."','".$city."','".$imm."','".$data."','".$sesso."','".$con."')");
        
        $ris[0]= array('salvataggio'=>"ok");
        
        print(json_encode($ris));
    }
    
    mysqli_close($link);

?>
