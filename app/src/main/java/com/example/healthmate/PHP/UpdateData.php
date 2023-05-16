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
    $condicion = $_POST["condicion"];

    # Obtener campos y valores
    $fields = array_keys($_POST);
    array_shift($fields);
    array_shift($fields);
    $values = array_values($_POST);
    array_shift($values);
    array_shift($values);

    $sets = array();
    foreach ($fields as $key => $field) {
        $sets[] = $field . "='" . $values[$key] . "'";
    }
    $sets_str = implode(",", $sets);

    $sql = "UPDATE " . $tabla . " SET " . $sets_str;

    if (!empty($condicion)) {
        $sql .= " WHERE " . $condicion;
    }

    $sql .= ";";

    try {
        $resultado = mysqli_query($con, $sql);
    } catch (Exception $e) { }

    # Comprobar si se ha ejecutado correctamente
    if (!$resultado) {
        echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    }

    # Ejecutar consulta
    if ($resultado) {
        echo "Ok";    
    } 

    $con->close();
?>