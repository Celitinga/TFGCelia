Feature: Autenticación de usuarios
  Como usuario de Helene
  Quiero poder iniciar sesión y registrarme
  Para acceder a la aplicación

  Scenario: Login exitoso con credenciales correctas
    Given existe un usuario con username "celia" y password "123456"
    When el usuario intenta hacer login con username "celia" y password "123456"
    Then la respuesta de login es exitosa
    And la respuesta contiene un token JWT
    And la respuesta contiene el username "celia"

  Scenario: Login fallido con password incorrecta
    Given existe un usuario con username "celia" y password "123456"
    When el usuario intenta hacer login con username "celia" y password "wrongpass"
    Then la respuesta de login falla
    And el mensaje de error es "Usuario o contraseña incorrectos"

  Scenario: Login con campos vacíos
    When el usuario intenta hacer login con username "" y password ""
    Then la respuesta de login falla
    And el mensaje de error es "Usuario y contraseña son requeridos"

  Scenario: Login con usuario inexistente
    Given no existe ningún usuario con username "fantasma"
    When el usuario intenta hacer login con username "fantasma" y password "123456"
    Then la respuesta de login falla
    And el mensaje de error es "Usuario o contraseña incorrectos"

  Scenario: Registro exitoso de nuevo usuario
    Given no existe ningún usuario con username "nuevousuario"
    And existe el rol "CLIENTE" en el sistema
    When se registra un usuario con username "nuevousuario" email "nuevo@helene.com" y password "123456"
    Then la respuesta de registro es exitosa
    And el mensaje es "Usuario registrado correctamente"

  Scenario: Registro fallido porque el usuario ya existe
    Given existe un usuario con username "celia" y password "123456"
    When se registra un usuario con username "celia" email "celia@helene.com" y password "123456"
    Then la respuesta de registro falla
    And el mensaje es "El usuario ya existe"

  Scenario: Registro con datos nulos
    When se envía un registro con body vacío
    Then la respuesta de registro falla
    And el mensaje es "Datos inválidos"