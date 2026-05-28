Feature: Panel de administración de usuarios
  Como administrador de Helene
  Quiero gestionar los usuarios del sistema
  Para mantener el control de accesos

  Scenario: Listar todos los usuarios
    Given existen 3 usuarios en el sistema
    When el admin solicita la lista de usuarios
    Then la respuesta contiene 3 usuarios

  Scenario: Crear un usuario nuevo como admin
    Given no existe ningún usuario con username "empleado1"
    And existe el rol "EMPLEADO" en el sistema
    When el admin crea un usuario con username "empleado1" email "emp@helene.com" password "123456" y rol "EMPLEADO"
    Then el usuario es creado correctamente
    And el usuario tiene el rol "EMPLEADO"

  Scenario: Crear usuario con username duplicado
    Given existe un usuario con username "celia" y password "123456"
    When el admin crea un usuario con username "celia" email "x@x.com" password "123456" y rol "CLIENTE"
    Then se lanza una excepción con mensaje "El username ya existe: celia"

  Scenario: Eliminar un usuario existente
    Given existe un usuario con id 5 username "borrame" y rol "CLIENTE"
    When el admin elimina el usuario con id 5
    Then el usuario es eliminado correctamente

  Scenario: Intentar eliminar un usuario ADMIN
    Given existe un usuario con id 1 username "celitinga" y rol "ADMIN"
    When el admin elimina el usuario con id 1
    Then se lanza una excepción con mensaje "No se puede eliminar una cuenta con rol ADMIN"

  Scenario: Cambiar el rol de un usuario
    Given existe un usuario con id 3 username "user3" y rol "CLIENTE"
    And existe el rol "EMPLEADO" en el sistema
    When el admin cambia el rol del usuario con id 3 a "EMPLEADO"
    Then el usuario tiene el rol "EMPLEADO"