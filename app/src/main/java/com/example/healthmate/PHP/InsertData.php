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

    # Obtener nombre de la tabla
    $tabla = $_POST["tabla"];

    # Obtener campos y valores
    $fields = array_keys($_POST);
    array_shift($fields);
    $values = array_values($_POST);
    array_shift($values);
    $fields_str = implode(",", $fields);
    $values_str = "'" . implode("','", $values) . "'";

    $sql = "INSERT INTO " . $tabla . " (" . $fields_str . ") VALUES (" . $values_str . ");";

    try {
        $resultado = mysqli_query($con, $sql);
    } catch (Exception $e) { }

    # Comprobar si se ha ejecutado correctamente
    if (!$resultado) {
        echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    }

    # Ejecutar consulta
    if ($resultado) {
        $last_id = mysqli_insert_id($con);
        $response = array('status' => 'success', 'message' => 'Registro insertado con éxito', 'id' => $last_id);
    } else {
        $response = array('status' => 'error', 'message' => 'Error al insertar registro: ' . $conn->error);
    }        
    
    # Devolver los registros en formato JSON
    echo json_encode($response);

    $con->close();
?>