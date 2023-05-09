<?php
    $DB_SERVER="127.0.0.1"; #la dirección del servidor
    $DB_USER="Xpllano002"; #el usuario para esa base de datos
    $DB_PASS="hiQWqsrz"; #la clave para ese usuario
    $DB_DATABASE= "Xpllano002_HealthMate"; #la base de datos a la que hay que conectarse

    # Se establece la conexión:
    $con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

    #Comprobamos conexión
    if (mysqli_connect_errno()) {
        echo 'Error de conexion: ' . mysqli_connect_error();
        exit();
    }

    $tabla = $_POST["tabla"];
    $condicion = isset($_POST["condicion"]) ? $_POST["condicion"] : "";

    
    $sql = "SELECT * FROM " . $tabla;

    if (!empty($condicion)) {
        $sql .= " WHERE " . $condicion;
    }

    $sql .= ";";

    //echo $sql . "<br>";

    try {
        $resultado = mysqli_query($con, $sql);
    } catch (Exception $e) { }

    # Comprobar si se ha ejecutado correctamente
    if (!$resultado) {
        echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    }

    $registros = array();
    while( $fila = $resultado -> fetch_assoc()){
        $resultado_final[] = $fila;
    }            
    
    # Devolver los registros en formato JSON
    echo json_encode($resultado_final);

    $con->close();
?>