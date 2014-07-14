<?php


	include "JSON.php";
    
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
    
    $id_ric=$_POST['identif_ric'];
    $id_rich=$_POST['identif_rich'];
   
    $result1=mysqli_query($link, "SELECT * FROM Account WHERE identificativo='".$id_ric."'"); 
    $result2=mysqli_query($link, "SELECT * FROM Amicizia WHERE (id_richiedente='".$id_rich."' AND id_ricevente='".$id_ric."') OR (id_richiedente='".$id_ric."' AND id_ricevente='".$id_rich."')");
    
    if(mysqli_num_rows($result2)!=0){
    
    	while($e=mysqli_fetch_assoc($result2)){

			$output2[0]=$e;
            
       	}
        while($i=mysqli_fetch_assoc($result1)){

			$output1[0]=$i;
            
       }
       
       $result=array_merge((array)$output1, (array)$output2);
       
        print(json_encode($result));
       
    
    }
    else{
    
    	$output2[0]= array('statoAmicizia'=>"null");
        
        while($i=mysqli_fetch_assoc($result1)){

			$output1[0]=$i;
            
       }
       
       $result=array_merge((array)$output1, (array)$output2);
       
        print(json_encode($result));
       	
    }
?>
